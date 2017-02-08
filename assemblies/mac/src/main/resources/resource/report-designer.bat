@echo off
@REM
@REM WARNING: Pentaho Report Designer needs JDK 1.7 or newer to run.
@REM
setlocal
cd /D %~dp0

REM Special console/debug options when called from report-designer.bat or report-designer-debug.bat
if "%CONSOLE%"=="1" set PENTAHO_JAVA=java
if not "%CONSOLE%"=="1" set PENTAHO_JAVA=javaw

if "%_PENTAHO_JAVA_HOME%" == "" goto callSetEnv
set PENTAHO_JAVA_HOME=%_PENTAHO_JAVA_HOME%

:callSetEnv
call "%~dp0set-pentaho-env.bat"

start "Pentaho Report Designer" "%_PENTAHO_JAVA%" -Dswing.useSystemFontSettings=false -Xms1024m -Xmx2048m -XX:MaxPermSize=256m -jar "%~dp0launcher.jar" %*
