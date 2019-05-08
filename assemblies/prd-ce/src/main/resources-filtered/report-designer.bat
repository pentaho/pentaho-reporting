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
