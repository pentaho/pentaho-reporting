@echo off
@REM
@REM WARNING: Pentaho Report Designer needs JDK 1.6 or newer to run.
@REM
setlocal
cd /D %~dp0
set PENTAHO_JAVA=javaw
call "%~dp0set-pentaho-env.bat"

start "Pentaho Report Designer" "%_PENTAHO_JAVA%" -XX:MaxPermSize=256m -Xmx512M -jar "%~dp0launcher.jar" %*
