package io.gizmosql.sqlline;

import sqlline.SqlLine;
import sqlline.SqlLine.Status;

import java.io.IOException;
import java.time.Year;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GizmoSQLLine - A Flight SQL-specific version of SQLLine for connecting to GizmoSQL instances.
 *
 * This wrapper pre-configures SQLLine with the Arrow Flight SQL JDBC driver,
 * making it easy to connect to Flight SQL servers.
 *
 * Usage:
 *   java -jar gizmosqlline.jar -u "jdbc:arrow-flight-sql://host:port" -n user -p password
 *
 * Or start interactive mode:
 *   java -jar gizmosqlline.jar
 *   sqlline> !connect jdbc:arrow-flight-sql://host:port user password
 */
public class GizmoSQLLine {

    private static final String FLIGHT_SQL_DRIVER = "org.apache.arrow.driver.jdbc.ArrowFlightJdbcDriver";
    private static final String VERSION = "1.0.0";

    public static void main(String[] args) throws IOException {
        // Suppress verbose Arrow INFO logging
        Logger.getLogger("org.apache.arrow").setLevel(Level.WARNING);

        // Ensure the Flight SQL JDBC driver is loaded
        try {
            Class.forName(FLIGHT_SQL_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Arrow Flight SQL JDBC driver not found.");
            System.err.println("Please ensure the driver is on the classpath.");
            System.exit(1);
        }

        // Print banner if no arguments or help requested
        if (args.length == 0 || containsHelp(args)) {
            printBanner();
        }

        // Delegate to SQLLine
        SqlLine sqlLine = new SqlLine();
        Status status = sqlLine.begin(args, null, true);
        System.exit(status.ordinal());
    }

    private static boolean containsHelp(String[] args) {
        for (String arg : args) {
            if ("--help".equals(arg) || "-h".equals(arg) || "-?".equals(arg)) {
                return true;
            }
        }
        return false;
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("  ____  _                      ____   ___  _     _     _            ");
        System.out.println(" / ___|(_)___  _ __ ___   ___ / ___| / _ \\| |   | |   (_)_ __   ___ ");
        System.out.println("| |  _ | |_  /| '_ ` _ \\ / _ \\\\___ \\| | | | |   | |   | | '_ \\ / _ \\");
        System.out.println("| |_| || |/ / | | | | | | (_) |___) | |_| | |___| |___| | | | |  __/");
        System.out.println(" \\____||_/___||_| |_| |_|\\___/|____/ \\__\\_\\_____|_____|_|_| |_|\\___|");
        System.out.println();
        System.out.println("GizmoSQLLine v" + VERSION + " - Flight SQL Client for GizmoSQL");
        System.out.println("Copyright (c) " + Year.now().getValue() + " GizmoData LLC");
        System.out.println("Built with Apache Arrow Flight SQL JDBC Driver");
        System.out.println();
        System.out.println("Quick Start:");
        System.out.println("  !connect jdbc:arrow-flight-sql://localhost:31337 user password");
        System.out.println();
        System.out.println("Connection URL format:");
        System.out.println("  jdbc:arrow-flight-sql://host:port[?param1=value1&param2=value2]");
        System.out.println();
        System.out.println("Common parameters:");
        System.out.println("  useEncryption=true/false    Enable/disable TLS encryption");
        System.out.println("  disableCertificateVerification=true  Skip certificate verification");
        System.out.println("  token=<bearer_token>        Use bearer token authentication");
        System.out.println();
    }
}
