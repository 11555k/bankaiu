@echo off
setlocal

REM Create lib directory if it doesn't exist
if not exist "lib" mkdir lib

REM Download HikariCP 5.0.1 jar
echo Downloading HikariCP-5.0.1.jar...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/zaxxer/HikariCP/5.0.1/HikariCP-5.0.1.jar' -OutFile 'lib\\HikariCP-5.0.1.jar'"

if exist "lib\\HikariCP-5.0.1.jar" (
    echo Download complete: lib\\HikariCP-5.0.1.jar
) else (
    echo Download failed!
)

endlocal
pause 