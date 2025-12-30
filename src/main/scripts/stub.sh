#!/usr/bin/env bash
exec java --add-opens=java.base/java.nio=ALL-UNNAMED -jar "$0" "$@"
