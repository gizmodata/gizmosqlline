#!/usr/bin/env bash
V=$(java -version 2>&1 | head -1 | sed 's/.*"\([0-9]*\).*/\1/'); NA=; [ "$V" -ge 16 ] 2>/dev/null && NA="--enable-native-access=ALL-UNNAMED"; exec java --add-opens=java.base/java.nio=ALL-UNNAMED $NA -jar "$0" "$@"
