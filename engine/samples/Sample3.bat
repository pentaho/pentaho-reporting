@echo off

REM SET UP CLASSPATH
set CLASSPATH=.

FOR %%F IN (lib\*.jar) DO call :updateClassPath %%F

goto :startjava

:updateClassPath
set CLASSPATH=%CLASSPATH%;%1
goto :eof

:startjava
java -cp %CLASSPATH% org.pentaho.reporting.engine.classic.samples.Sample3
