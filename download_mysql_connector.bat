@echo off
setlocal

REM Create lib directory if it doesn't exist
if not exist "lib" mkdir lib

REM Download MySQL Connector/J 8.3.0 jar
echo Downloading mysql-connector-j-8.3.0.jar...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar' -OutFile 'lib\\mysql-connector-j-8.3.0.jar'"

if exist "lib\\mysql-connector-j-8.3.0.jar" (
    echo Download complete: lib\\mysql-connector-j-8.3.0.jar
) else (
    echo Download failed!
)

endlocal
pause 