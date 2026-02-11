package com.gizmodata.sqlline;

import sqlline.SqlLine;
import sqlline.SqlLine.Status;

import java.io.IOException;
import java.time.Year;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GizmoSQLLine - A GizmoSQL JDBC-based version of SQLLine for connecting to GizmoSQL instances.
 *
 * This wrapper pre-configures SQLLine with the GizmoSQL JDBC driver,
 * making it easy to connect to GizmoSQL servers.
 *
 * Usage:
 *   java -jar gizmosqlline.jar -u "jdbc:gizmosql://host:port" -n user -p password
 *
 * Or start interactive mode:
 *   java -jar gizmosqlline.jar
 *   sqlline> !connect jdbc:gizmosql://host:port user password
 */
public class GizmoSQLLine {

    private static final String GIZMOSQL_JDBC_DRIVER = "org.apache.arrow.driver.jdbc.ArrowFlightJdbcDriver";

    public static void main(String[] args) throws IOException {
        // Suppress verbose Arrow INFO logging
        Logger.getLogger("org.apache.arrow").setLevel(Level.WARNING);

        // Ensure the GizmoSQL JDBC driver is loaded
        try {
            Class.forName(GIZMOSQL_JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: GizmoSQL JDBC driver not found.");
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
        System.out.println("GizmoSQLLine " + getVersion() + " - SQL Client for GizmoSQL");
        System.out.println("Copyright (c) " + Year.now().getValue() + " GizmoData LLC");
        System.out.println("Built with GizmoSQL JDBC Driver");
        System.out.println();
        System.out.println("Quick Start:");
        System.out.println("  !connect jdbc:gizmosql://localhost:31337 user password");
        System.out.println();
        System.out.println("  OAuth/SSO:");
        System.out.println("  !connect jdbc:gizmosql://host:port?authType=external \"\" \"\"");
        System.out.println();
        System.out.println("Connection URL format:");
        System.out.println("  jdbc:gizmosql://host:port[?param1=value1&param2=value2]");
        System.out.println();
        System.out.println("Common parameters:");
        System.out.println("  useEncryption=true/false    Enable/disable TLS encryption");
        System.out.println("  disableCertificateVerification=true  Skip certificate verification");
        System.out.println("  token=<bearer_token>        Use bearer token authentication");
        System.out.println("  authType=external           Enable server-side OAuth/SSO");
        System.out.println("  oauthServerPort=<port>      Custom OAuth server port (default: 31339)");
        System.out.println();
    }

    private static String getVersion() {
        String version = GizmoSQLLine.class.getPackage().getImplementationVersion();
        return version != null ? version : "dev";
    }
}
