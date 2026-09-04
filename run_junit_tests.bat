@ECHO OFF
SETLOCAL

SET JAVA_HOME=C:\Program Files\Java\jdk-17
SET TOMCAT_HOME=C:\xampp1\tomcat
SET PROJECT_DIR=%~dp0
SET SRC_DIR=%PROJECT_DIR%src
SET TEST_DIR=%PROJECT_DIR%test
SET LIB_DIR=%PROJECT_DIR%lib
SET CLASSES_DIR=%PROJECT_DIR%WEB-INF\classes
SET TEST_CLASSES_DIR=%PROJECT_DIR%test-classes
IF NOT EXIST "%CLASSES_DIR%" MKDIR "%CLASSES_DIR%"
IF NOT EXIST "%TEST_CLASSES_DIR%" MKDIR "%TEST_CLASSES_DIR%"

SET CP="%TOMCAT_HOME%\lib\servlet-api.jar;%PROJECT_DIR%lib\*;%CLASSES_DIR%;%TEST_CLASSES_DIR%"

ECHO Compiling Source Classes...
PUSHD "%SRC_DIR%"
if exist sources.txt del sources.txt
powershell -Command "(Get-ChildItem -Recurse -Filter *.java).FullName.Replace('\', '/') | ForEach-Object { \"`\"$_`\"\" } | Out-File -FilePath sources.txt -Encoding ascii"
"%JAVA_HOME%\bin\javac.exe" -cp "%TOMCAT_HOME%\lib\servlet-api.jar;%PROJECT_DIR%lib\*" -d "%CLASSES_DIR%" @sources.txt
DEL sources.txt
POPD

ECHO Compiling Test Classes...
PUSHD "%TEST_DIR%"
if exist sources.txt del sources.txt
powershell -Command "(Get-ChildItem -Recurse -Filter *.java).FullName.Replace('\', '/') | ForEach-Object { \"`\"$_`\"\" } | Out-File -FilePath sources.txt -Encoding ascii"

"%JAVA_HOME%\bin\javac.exe" -cp %CP% -d "%TEST_CLASSES_DIR%" @sources.txt
DEL sources.txt
POPD

IF %ERRORLEVEL% NEQ 0 (
    ECHO [ERROR] Test Compilation failed.
    PAUSE & EXIT /B 1
)

ECHO Running JUnit Tests...
"%JAVA_HOME%\bin\java.exe" -cp %CP% org.junit.platform.console.ConsoleLauncher --scan-classpath

PAUSE
