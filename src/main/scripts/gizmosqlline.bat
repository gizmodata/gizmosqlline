@echo off
java --add-opens=java.base/java.nio=ALL-UNNAMED -jar "%~dp0gizmosqlline.jar" %*
