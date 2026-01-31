# GizmoSQLLine

A GizmoSQL JDBC-based version of [SQLLine](https://github.com/julianhyde/sqlline) for connecting to GizmoSQL instances.

GizmoSQLLine bundles SQLLine with the GizmoSQL JDBC driver, providing an easy-to-use command-line SQL client for GizmoSQL servers. The GizmoSQL JDBC driver supports both client-side and server-side query cancellation.

## Features

- Pre-configured with GizmoSQL JDBC driver (v1.4.1)
- Client-side and server-side query cancellation support
- Single executable with all dependencies included
- Full SQLLine functionality (command history, tab completion, output formatting)
- Support for TLS encryption and various authentication methods

## Installation

### Homebrew (Recommended)

The easiest way to install GizmoSQLLine on macOS or Linux:

```bash
brew install gizmodata/tap/gizmosqlline
```

This automatically installs Java (OpenJDK) if needed.

### Download Release

Download from the [Releases](../../releases) page:

| Platform | File | Instructions |
|----------|------|--------------|
| Linux/macOS | `gizmosqlline` | `chmod +x gizmosqlline && ./gizmosqlline` |
| Windows | `gizmosqlline.jar` + `gizmosqlline.bat` | Download both to same folder, run `gizmosqlline.bat` |
| Any | `gizmosqlline.jar` | `java --add-opens=java.base/java.nio=ALL-UNNAMED --enable-native-access=ALL-UNNAMED -jar gizmosqlline.jar` |

#### Quick Install (Linux/macOS)

```bash
curl -L -o gizmosqlline https://github.com/gizmodata/gizmosqlline/releases/latest/download/gizmosqlline
chmod +x gizmosqlline
sudo mv gizmosqlline /usr/local/bin/
```

**Requires:** Java 11+ runtime installed

### Build from Source

```bash
git clone https://github.com/gizmodata/gizmosqlline.git
cd gizmosqlline
mvn clean package
```

The executable will be available at `target/gizmosqlline`.

## Usage

### Interactive Mode

```bash
./gizmosqlline
```

Then connect to a GizmoSQL server:

```sql
sqlline> !connect jdbc:gizmosql://localhost:31337 user password
```

### Command Line Connection

```bash
./gizmosqlline -u "jdbc:gizmosql://localhost:31337" -n user -p password
```

### Connection URL Format

```
jdbc:gizmosql://host:port[?param1=value1&param2=value2]
```

### Common Connection Parameters

| Parameter | Description |
|-----------|-------------|
| `useEncryption` | Enable TLS encryption (`true`/`false`) |
| `disableCertificateVerification` | Skip certificate verification (`true`/`false`) |
| `token` | Bearer token for authentication |
| `user` | Username for authentication |
| `password` | Password for authentication |

### Example Connections

```bash
# Basic connection (no encryption)
./gizmosqlline -u "jdbc:gizmosql://localhost:31337" -n admin -p secret

# With TLS encryption
./gizmosqlline -u "jdbc:gizmosql://localhost:31337?useEncryption=true" -n admin -p secret

# With bearer token
./gizmosqlline -u "jdbc:gizmosql://localhost:31337?token=your-bearer-token"

# Skip certificate verification (development only)
./gizmosqlline -u "jdbc:gizmosql://localhost:31337?useEncryption=true&disableCertificateVerification=true" -n admin -p secret
```

## SQLLine Commands

Once connected, you can use standard SQLLine commands:

| Command | Description |
|---------|-------------|
| `!help` | Show all available commands |
| `!tables` | List all tables |
| `!columns <table>` | Show columns for a table |
| `!describe <table>` | Describe a table |
| `!outputformat <format>` | Set output format (table, csv, json, etc.) |
| `!quit` | Exit GizmoSQLLine |

## Building

### Requirements

- Java 11 or higher
- Maven 3.6 or higher

### Build Commands

```bash
# Build the executable
mvn clean package

# Skip tests
mvn clean package -DskipTests
```

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.

This project bundles:
- [SQLLine](https://github.com/julianhyde/sqlline) - BSD-3-Clause License
- [GizmoSQL JDBC Driver](https://github.com/gizmodata/gizmosql-jdbc-driver) - Apache License 2.0

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
