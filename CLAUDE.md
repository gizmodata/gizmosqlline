# Claude Code Notes - GizmoSQLLine

## Project Overview
GizmoSQLLine is a SQLLine-based CLI client for GizmoSQL, packaged as a self-executing JAR (shell stub prepended to JAR).

## Build
- **Build with Java 11**: `JAVA_HOME=$(/usr/libexec/java_home -v 11) mvn clean package`
- The output artifact (`target/gizmosqlline`) runs on Java 11+ but must be **built** with Java 11
- The executable is a concatenation of `src/main/scripts/stub.sh` + the shaded JAR — works because ZIP format is read from the end

## Dependencies
- **GizmoSQL JDBC Driver**: `com.gizmodata:gizmosql-jdbc-driver` — a shaded uber JAR published to Maven Central
  - Source repo: `~/LocalOnly/git/gizmosql-jdbc-driver`
  - The driver class name is `org.apache.arrow.driver.jdbc.ArrowFlightJdbcDriver` (same as upstream Arrow)
  - All internal classes are relocated under `org.apache.arrow.driver.jdbc.shaded.*`
- **SQLLine**: `sqlline:sqlline:1.12.0`
- **JLine**: `org.jline:jline:3.26.1`

## JVM Flags
- `--add-opens=java.base/java.nio=ALL-UNNAMED` — required for Arrow memory (all Java versions)
- `--enable-native-access=ALL-UNNAMED` — required for Java 16+ (JNA/Netty native access), but Java 11 rejects this flag
- `--sun-misc-unsafe-memory-access=allow` — required for Java 25+ (Protobuf/Netty use `sun.misc.Unsafe`)
- stub.sh, gizmosqlline.bat, CI workflow, and Homebrew formula all detect Java version and conditionally add flags
- These `Unsafe` warnings will persist until upstream Protobuf, Netty, and Arrow migrate to `java.lang.foreign` (Panama APIs)

## Testing
- **Unit tests**: Standard Maven surefire
- **Integration tests**: Use Testcontainers with `gizmodata/gizmosql:latest` Docker image
  - Require Docker: `export DOCKER_HOST=unix://$HOME/.docker/run/docker.sock` on macOS
  - `src/test/resources/docker-java.properties` has `api.version=1.44` for Docker Engine 29+
  - Failsafe plugin needs `--add-opens=java.base/java.nio=ALL-UNNAMED` in argLine
- **CI integration tests**: Run the built JAR against a GizmoSQL service container on JDK 11, 17, 21, 25

## SLF4J
- Testcontainers pulls `slf4j-api:1.7.36` transitively
- Explicit `slf4j-api:2.0.9` dependency overrides it to avoid version mismatch with `slf4j-simple:2.0.9`

## CI/CD (`.github/workflows/build.yml`)
- Build on JDK 11, integration test on JDK 11/17/21/25
- Release job triggers on `v*` tags — creates GitHub release + updates Homebrew tap
- Homebrew tap: `gizmodata/homebrew-tap` — uses `HOMEBREW_TAP_TOKEN` secret

## Common Gotchas
- **sed regex on macOS**: Use `[0-9][0-9]*` (one-or-more) not `[0-9]*` (zero-or-more) for version parsing — BSD sed handles the latter differently than GNU sed, causing version detection to silently fail
- **JVM flags in 4 places**: When adding a new JVM flag, update: `stub.sh`, `gizmosqlline.bat`, CI workflow (`build.yml` integration test step), and Homebrew formula template (also in `build.yml` release step)
- **Homebrew formula is generated**: The formula in `gizmodata/homebrew-tap` is overwritten on every release by the `build.yml` workflow — edit the template in `build.yml`, not the tap repo directly
- **JDK 25 locally via Homebrew**: `/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home` — not visible to `/usr/libexec/java_home`

## JDBC URI Format
```
jdbc:gizmosql://host:port?useEncryption=true&disableCertificateVerification=true
```
