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

REM SET UP CLASSPATH
set CLASSPATH=.

FOR %%F IN (bin\) DO call :updateClassPath %%F
FOR %%F IN (lib\*.jar) DO call :updateClassPath %%F

goto :startjava

:updateClassPath
set CLASSPATH=%CLASSPATH%;%1
goto :eof

:startjava
java -cp %CLASSPATH% org.pentaho.reporting.engine.classic.samples.Sample3
