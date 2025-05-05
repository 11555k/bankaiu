@echo off
setlocal

REM Create lib directory if it doesn't exist
if not exist "lib" mkdir lib

REM Download SLF4J API and Simple binding 2.0.13
echo Downloading slf4j-api-2.0.13.jar...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.13/slf4j-api-2.0.13.jar' -OutFile 'lib\\slf4j-api-2.0.13.jar'"

echo Downloading slf4j-simple-2.0.13.jar...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.13/slf4j-simple-2.0.13.jar' -OutFile 'lib\\slf4j-simple-2.0.13.jar'"

if exist "lib\\slf4j-api-2.0.13.jar" if exist "lib\\slf4j-simple-2.0.13.jar" (
    echo Download complete: slf4j-api-2.0.13.jar and slf4j-simple-2.0.13.jar
) else (
    echo Download failed!
)

endlocal
pause 