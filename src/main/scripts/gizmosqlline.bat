@echo off
for /f "tokens=3" %%a in ('java -version 2^>^&1 ^| findstr /i "version"') do set JAVA_VER=%%~a
for /f "tokens=1 delims=." %%a in ("%JAVA_VER%") do set JAVA_MAJOR=%%a
set NA=
if %JAVA_MAJOR% GEQ 16 set NA=--enable-native-access=ALL-UNNAMED
if %JAVA_MAJOR% GEQ 25 set NA=%NA% --sun-misc-unsafe-memory-access=allow
java --add-opens=java.base/java.nio=ALL-UNNAMED %NA% -jar "%~dp0gizmosqlline.jar" %*
