@echo off

REM -----------------------------------------------------------------------------------------------
REM This program is free software; you can redistribute it and/or modify it under the
REM terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
REM Foundation.
REM
REM You should have received a copy of the GNU Lesser General Public License along with this
REM program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
REM or from the Free Software Foundation, Inc.,
REM 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
REM
REM This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
REM without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
REM See the GNU Lesser General Public License for more details.
REM
REM Copyright (c) 2013 - ${copyright.year} Hitachi Vantara. All rights reserved.
REM -----------------------------------------------------------------------------------------------

@REM
@REM WARNING: Pentaho Report Designer needs JDK 11 or newer to run.
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


set ISJAVA11=0
pushd "%_PENTAHO_JAVA_HOME%"
if exist java.exe goto USEJAVAFROMPENTAHOJAVAHOME
cd bin
if exist java.exe goto USEJAVAFROMPENTAHOJAVAHOME
popd
pushd "%_PENTAHO_JAVA_HOME%\jre\bin"
if exist java.exe goto USEJAVAFROMPATH
goto USEJAVAFROMPATH
:USEJAVAFROMPENTAHOJAVAHOME
FOR /F %%a IN ('.\java.exe -version 2^>^&1^|%windir%\system32\find /C "version ""11."') DO (SET /a ISJAVA11=%%a)
GOTO VERSIONCHECKDONE
:USEJAVAFROMPATH
FOR /F %%a IN ('java -version 2^>^&1^|%windir%\system32\find /C "version ""11."') DO (SET /a ISJAVA11=%%a)
:VERSIONCHECKDONE
popd

set JAVA_ADD_OPENS=
SET JAVA_LOCALE_COMPAT=
IF NOT %ISJAVA11% == 1 GOTO :SKIPLOCALE
set JAVA_ADD_OPENS=--add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/sun.net.www.protocol.jar=ALL-UNNAMED
set JAVA_LOCALE_COMPAT=-Djava.locale.providers=COMPAT,SPI
:SKIPLOCALE

start "Pentaho Report Designer" "%_PENTAHO_JAVA%" %JAVA_ADD_OPENS% -Dswing.useSystemFontSettings=false -Xms1024m -Xmx2048m %JAVA_LOCALE_COMPAT% -jar "%~dp0launcher.jar" %*
