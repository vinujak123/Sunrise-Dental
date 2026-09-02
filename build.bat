@ECHO OFF
:: ============================================================
:: Sunrise Dental Clinic – Build & Deploy Script
:: CIS6003 Advanced Programming
:: Compiles Java sources and deploys to XAMPP Tomcat
:: ============================================================

SETLOCAL

:: ---- CONFIGURE THESE PATHS IF NEEDED ----------------------
SET JAVA_HOME=C:\Program Files\Java\jdk-17
SET TOMCAT_HOME=C:\xampp1\tomcat
SET MYSQL_CONNECTOR=lib\mysql-connector-j-8.3.0.jar
SET PROJECT_DIR=%~dp0
:: ------------------------------------------------------------

SET SERVLET_API=%TOMCAT_HOME%\lib\servlet-api.jar
SET CLASSES_DIR=%PROJECT_DIR%WEB-INF\classes
SET SRC_DIR=%PROJECT_DIR%src

ECHO ============================================================
ECHO  Sunrise Dental Clinic -- Build Script
ECHO ============================================================

:: Step 1 – Verify Java
IF NOT EXIST "%JAVA_HOME%\bin\javac.exe" (
    ECHO [ERROR] javac not found at %JAVA_HOME%\bin
    ECHO         Please update JAVA_HOME in this script.
    PAUSE & EXIT /B 1
)

:: Step 2 – Verify Servlet API
IF NOT EXIST "%SERVLET_API%" (
    ECHO [ERROR] servlet-api.jar not found at %SERVLET_API%
    ECHO         Make sure XAMPP Tomcat is installed.
    PAUSE & EXIT /B 1
)

:: Step 3 – Verify MySQL Connector
IF NOT EXIST "%PROJECT_DIR%%MYSQL_CONNECTOR%" (
    ECHO [WARN] MySQL Connector JAR not found at %PROJECT_DIR%%MYSQL_CONNECTOR%
    ECHO        Download mysql-connector-j-8.x.x.jar and place it in the lib\ folder.
    ECHO        Continuing build without it...
)

:: Step 4 – Create output directories
IF NOT EXIST "%CLASSES_DIR%" MKDIR "%CLASSES_DIR%"
IF NOT EXIST "%PROJECT_DIR%WEB-INF\lib" MKDIR "%PROJECT_DIR%WEB-INF\lib"

:: Step 5 – Compile all Java source files
ECHO.
ECHO [INFO] Compiling Java source files...

PUSHD "%SRC_DIR%"
if exist sources.txt del sources.txt
powershell -Command "(Get-ChildItem -Recurse -Filter *.java).FullName.Replace('\', '/') | ForEach-Object { \"`\"$_`\"\" } | Out-File -FilePath sources.txt -Encoding ascii"

"%JAVA_HOME%\bin\javac.exe" ^
    -cp "%SERVLET_API%;%PROJECT_DIR%%MYSQL_CONNECTOR%" ^
    -d "%CLASSES_DIR%" ^
    -sourcepath "%SRC_DIR%" ^
    @sources.txt

DEL sources.txt
POPD

IF %ERRORLEVEL% NEQ 0 (
    ECHO [ERROR] Compilation failed. Check the errors above.
    PAUSE & EXIT /B 1
)

ECHO [SUCCESS] Compilation complete.

:: Step 6 – Copy MySQL connector to WEB-INF\lib
IF EXIST "%PROJECT_DIR%%MYSQL_CONNECTOR%" (
    COPY /Y "%PROJECT_DIR%%MYSQL_CONNECTOR%" "%PROJECT_DIR%WEB-INF\lib\"
    ECHO [INFO] MySQL connector copied to WEB-INF\lib
)

:: Step 7 – Deploy to Tomcat webapps
SET DEPLOY_DIR=%TOMCAT_HOME%\webapps\SunriseDental
ECHO.
ECHO [INFO] Deploying to %DEPLOY_DIR%...
IF NOT EXIST "%DEPLOY_DIR%" MKDIR "%DEPLOY_DIR%"

:: Copy WEB-INF (compiled classes + web.xml)
XCOPY /E /Y "%PROJECT_DIR%WEB-INF\*" "%DEPLOY_DIR%\WEB-INF\"

:: Copy web content (HTML, CSS, JS)
XCOPY /E /Y "%PROJECT_DIR%web\*" "%DEPLOY_DIR%\"

ECHO [SUCCESS] Deployment complete!
ECHO.
ECHO ============================================================
ECHO  System deployed to: %DEPLOY_DIR%
ECHO  Access via:  http://localhost:8080/SunriseDental/
ECHO ============================================================
ECHO.
ECHO  Default Logins:
ECHO    Admin       : admin      / admin123
ECHO    Receptionist: recept01   / recept123
ECHO    Dentist     : dr_silva   / dentist123
ECHO ============================================================
PAUSE
