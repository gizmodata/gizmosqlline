package io.gizmosql.sqlline;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIf;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for GizmoSQLLine using a real GizmoSQL server.
 *
 * These tests spin up a GizmoSQL Docker container and verify that
 * the Flight SQL JDBC driver can connect and execute queries.
 *
 * To run locally on macOS with Docker Desktop:
 * export DOCKER_HOST=unix://$HOME/.docker/run/docker.sock
 * mvn verify
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnabledIf("isDockerAvailable")
public class GizmoSQLLineIT {

    private static final String GIZMOSQL_IMAGE = "gizmosql/gizmosql:latest";
    private static final int GIZMOSQL_PORT = 31337;
    private static final String USERNAME = "gizmosql_username";
    private static final String PASSWORD = "gizmosql_password";

    private GenericContainer<?> gizmosqlContainer;
    private String jdbcUrl;

    static boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            return true;
        } catch (Exception e) {
            System.out.println("Docker not available, skipping integration tests: " + e.getMessage());
            return false;
        }
    }

    @BeforeAll
    void setUp() {
        gizmosqlContainer = new GenericContainer<>(GIZMOSQL_IMAGE)
                .withExposedPorts(GIZMOSQL_PORT)
                .withEnv("GIZMOSQL_USERNAME", USERNAME)
                .withEnv("GIZMOSQL_PASSWORD", PASSWORD)
                .withEnv("TLS_ENABLED", "1")
                .withEnv("PRINT_QUERIES", "1")
                .waitingFor(new LogMessageWaitStrategy()
                        .withRegEx(".*GizmoSQL server - started.*\\n")
                        .withStartupTimeout(Duration.ofSeconds(60)));

        gizmosqlContainer.start();

        String host = gizmosqlContainer.getHost();
        Integer port = gizmosqlContainer.getMappedPort(GIZMOSQL_PORT);
        jdbcUrl = String.format(
                "jdbc:arrow-flight-sql://%s:%d?useEncryption=true&disableCertificateVerification=true&user=%s&password=%s",
                host, port, USERNAME, PASSWORD);
        System.out.println("GizmoSQL container started at: " + host + ":" + port);
    }

    @AfterAll
    void tearDown() {
        if (gizmosqlContainer != null && gizmosqlContainer.isRunning()) {
            gizmosqlContainer.stop();
        }
    }

    @Test
    void testDriverLoads() throws Exception {
        // Verify the Flight SQL JDBC driver can be loaded
        Class<?> driverClass = Class.forName("org.apache.arrow.driver.jdbc.ArrowFlightJdbcDriver");
        assertNotNull(driverClass, "Flight SQL JDBC driver should be loadable");
    }

    @Test
    void testConnectionEstablished() throws Exception {
        // Verify we can establish a connection to GizmoSQL
        try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
            assertNotNull(conn, "Connection should not be null");
            assertFalse(conn.isClosed(), "Connection should be open");
            System.out.println("Successfully connected to GizmoSQL");
        }
    }

    @Test
    void testSimpleQuery() throws Exception {
        // Execute a simple query
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 AS value")) {

            assertTrue(rs.next(), "Result set should have at least one row");
            int value = rs.getInt("value");
            assertEquals(1, value, "Query should return 1");
            System.out.println("Simple query executed successfully: SELECT 1 = " + value);
        }
    }

    @Test
    void testArithmeticExpression() throws Exception {
        // Test arithmetic expressions
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 2 + 3 AS sum, 10 * 5 AS product")) {

            assertTrue(rs.next(), "Result set should have at least one row");
            assertEquals(5, rs.getInt("sum"), "2 + 3 should equal 5");
            assertEquals(50, rs.getInt("product"), "10 * 5 should equal 50");
            System.out.println("Arithmetic query executed successfully");
        }
    }

    @Test
    void testStringFunctions() throws Exception {
        // Test string operations
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 'Hello' || ' ' || 'GizmoSQL' AS greeting")) {

            assertTrue(rs.next(), "Result set should have at least one row");
            String greeting = rs.getString("greeting");
            assertEquals("Hello GizmoSQL", greeting, "String concatenation should work");
            System.out.println("String query executed successfully: " + greeting);
        }
    }

    @Test
    void testShowTables() throws Exception {
        // Test SHOW TABLES command (may return empty if no tables exist)
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES")) {

            // Just verify the query executes without error
            assertNotNull(rs, "Result set should not be null");
            System.out.println("SHOW TABLES executed successfully");

            // Count and print tables if any
            int tableCount = 0;
            while (rs.next()) {
                tableCount++;
            }
            System.out.println("Found " + tableCount + " tables");
        }
    }

    @Test
    void testCreateAndQueryTable() throws Exception {
        // Test creating a table, inserting data, and querying
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement()) {

            // Create a test table
            stmt.execute("CREATE TABLE IF NOT EXISTS test_table (id INT, name VARCHAR)");
            System.out.println("Created test_table");

            // Insert data
            stmt.execute("INSERT INTO test_table VALUES (1, 'Alice'), (2, 'Bob'), (3, 'Charlie')");
            System.out.println("Inserted test data");

            // Query the data
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM test_table ORDER BY id")) {
                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    System.out.println("  Row: id=" + id + ", name=" + name);
                }
                assertEquals(3, rowCount, "Should have 3 rows");
            }

            // Test aggregation
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM test_table")) {
                assertTrue(rs.next());
                assertEquals(3, rs.getInt("cnt"), "COUNT should return 3");
            }

            // Clean up
            stmt.execute("DROP TABLE test_table");
            System.out.println("Dropped test_table");
        }
    }

    @Test
    void testMultipleConnections() throws Exception {
        // Test that multiple connections can be established
        try (Connection conn1 = DriverManager.getConnection(jdbcUrl);
             Connection conn2 = DriverManager.getConnection(jdbcUrl)) {

            assertNotNull(conn1);
            assertNotNull(conn2);
            assertFalse(conn1.isClosed());
            assertFalse(conn2.isClosed());

            // Execute queries on both connections
            try (Statement stmt1 = conn1.createStatement();
                 Statement stmt2 = conn2.createStatement();
                 ResultSet rs1 = stmt1.executeQuery("SELECT 1");
                 ResultSet rs2 = stmt2.executeQuery("SELECT 2")) {

                assertTrue(rs1.next());
                assertTrue(rs2.next());
                assertEquals(1, rs1.getInt(1));
                assertEquals(2, rs2.getInt(1));
            }
            System.out.println("Multiple connections work correctly");
        }
    }
}
