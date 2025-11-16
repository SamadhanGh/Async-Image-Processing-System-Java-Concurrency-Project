@echo off
setlocal enabledelayedexpansion

echo ================================
echo Environment Setup Checker
echo ================================
echo.

REM Check Java version
echo Checking Java version...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Java not found in PATH
    exit /b 1
) else (
    echo [+] Java detected
    java -version 2>&1 | findstr /C:"version"
)

echo.

REM Check JAVA_HOME
echo Checking JAVA_HOME...
if defined JAVA_HOME (
    echo [+] JAVA_HOME is set: %JAVA_HOME%
) else (
    echo [X] JAVA_HOME not set
    echo     Set it with: set JAVA_HOME=C:\path\to\jdk-21
)

echo.

REM Check Maven
echo Checking Maven...
mvn -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Maven not found in PATH
    exit /b 1
) else (
    echo [+] Maven detected
    mvn -v | findstr /C:"Apache Maven"
)

echo.

REM Check pom.xml
echo Checking pom.xml...
if exist "pom.xml" (
    echo [+] pom.xml found
) else (
    echo [X] pom.xml not found
    exit /b 1
)

echo.

REM Check module-info.java
echo Checking module-info.java...
if exist "src\main\java\module-info.java" (
    echo [+] module-info.java found
) else (
    echo [X] module-info.java not found
)

echo.

REM Check FXML file
echo Checking FXML resources...
if exist "src\main\resources\enhanced-image-processing-view.fxml" (
    echo [+] enhanced-image-processing-view.fxml found
) else (
    echo [X] enhanced-image-processing-view.fxml not found
)

echo.

REM Check test image
echo Checking test image...
if exist "src\main\resources\test.jpg" (
    echo [+] test.jpg found
) else (
    echo [!] test.jpg not found (optional)
)

echo.
echo ================================
echo [+] All critical checks passed!
echo ================================
echo.

echo You can now run the project with:
echo   mvn clean javafx:run
echo.

echo Or with Maven wrapper:
echo   mvnw.cmd clean javafx:run
echo.

pause
