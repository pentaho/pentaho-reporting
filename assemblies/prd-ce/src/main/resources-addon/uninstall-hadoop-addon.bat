@echo offz

REM *****************************************************************************
REM
REM Pentaho Data Integration
REM
REM Copyright (C) 2006 - 2022 by Hitachi Vantara : http://www.hitachivantara.com
REM
REM *****************************************************************************
REM
REM Licensed under the Apache License, Version 2.0 (the "License");
REM you may not use this file except in compliance with
REM the License. You may obtain a copy of the License at
REM
REM    http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.
REM
REM *****************************************************************************

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