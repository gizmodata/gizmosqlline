# GizmoSQLLine

A Flight SQL-specific version of [SQLLine](https://github.com/julianhyde/sqlline) for connecting to GizmoSQL instances.

GizmoSQLLine bundles SQLLine with the Apache Arrow Flight SQL JDBC driver, providing an easy-to-use command-line SQL client for Flight SQL servers.

## Features

- Pre-configured with Arrow Flight SQL JDBC driver (v18.3.0)
- Single executable with all dependencies included
- Full SQLLine functionality (command history, tab completion, output formatting)
- Support for TLS encryption and various authentication methods

## Installation

### Download Release

Download the latest `gizmosqlline` executable from the [Releases](../../releases) page:

```bash
# Download
curl -L -o gizmosqlline https://github.com/YOUR_ORG/gizmosqlline/releases/latest/download/gizmosqlline
chmod +x gizmosqlline

# Optionally install to PATH
sudo mv gizmosqlline /usr/local/bin/
```

**Requires:** Java 11+ runtime installed

### Build from Source

```bash
git clone https://github.com/your-org/gizmosqlline.git
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
sqlline> !connect jdbc:arrow-flight-sql://localhost:31337 user password
```

### Command Line Connection

```bash
./gizmosqlline -u "jdbc:arrow-flight-sql://localhost:31337" -n user -p password
```

### Connection URL Format

```
jdbc:arrow-flight-sql://host:port[?param1=value1&param2=value2]
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
./gizmosqlline -u "jdbc:arrow-flight-sql://localhost:31337" -n admin -p secret

# With TLS encryption
./gizmosqlline -u "jdbc:arrow-flight-sql://localhost:31337?useEncryption=true" -n admin -p secret

# With bearer token
./gizmosqlline -u "jdbc:arrow-flight-sql://localhost:31337?token=your-bearer-token"

# Skip certificate verification (development only)
./gizmosqlline -u "jdbc:arrow-flight-sql://localhost:31337?useEncryption=true&disableCertificateVerification=true" -n admin -p secret
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

This project bundles:
- [SQLLine](https://github.com/julianhyde/sqlline) - BSD License
- [Apache Arrow Flight SQL JDBC Driver](https://arrow.apache.org/) - Apache License 2.0

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
