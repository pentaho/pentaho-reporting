@echo offz
REM ******************************************************************************
REM
REM Pentaho
REM
REM Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
REM
REM Use of this software is governed by the Business Source License included
REM in the LICENSE.TXT file.
REM
REM Change Date: 2029-07-20
REM ******************************************************************************

SET CURRENTFOLDER=%~dp0
md %CURRENTFOLDER%\.uninstalled
set UNINSTALLEDFOLDER=%CURRENTFOLDER%\.uninstalled
md %UNINSTALLEDFOLDER%\resources
if exist %CURRENTFOLDER%\resources\kettle-lifecycle-listeners.xml move %CURRENTFOLDER%\resources\kettle-lifecycle-listeners.xml %UNINSTALLEDFOLDER%\resources\kettle-lifecycle-listeners.xml
if exist %CURRENTFOLDER%\resources\kettle-registry-extensions.xml move %CURRENTFOLDER%\resources\kettle-registry-extensions.xml %UNINSTALLEDFOLDER%\resources\kettle-registry-extensions.xml
if exist %CURRENTFOLDER%\drivers move %CURRENTFOLDER%\drivers %UNINSTALLEDFOLDER%\drivers
if exist %CURRENTFOLDER%\system move %CURRENTFOLDER%\system %UNINSTALLEDFOLDER%\system
md %UNINSTALLEDFOLDER%\lib
move "%CURRENTFOLDER%\lib\org.apache.karaf*.jar" %UNINSTALLEDFOLDER%\lib\
move "%CURRENTFOLDER%\lib\org.apache.felix*.jar" %UNINSTALLEDFOLDER%\lib\
move "%CURRENTFOLDER%\lib\osgi.*.jar" %UNINSTALLEDFOLDER%\lib\
move "%CURRENTFOLDER%\lib\org.osgi*.jar" %UNINSTALLEDFOLDER%\lib\
move "%CURRENTFOLDER%\lib\pax-logging-log4j2*.jar" %UNINSTALLEDFOLDER%\lib\
move "%CURRENTFOLDER%\lib\jline*.jar" %UNINSTALLEDFOLDER%\lib\
move "%CURRENTFOLDER%\lib\jna-platform*.jar" %UNINSTALLEDFOLDER%\lib\