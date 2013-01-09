JFreeReport extensions
----------------------

The classes in this subproject extend JFreeReport with additional 
functionality. While the base package uses only a limited number of 
external libraries to perform the report generation (and remains small
and clean), most advanced usage scenarios require additional functionality 
which is usually provided by an almost infinite number of external libraries.

The extensions here rely on JFreeReport while the base classes keep their
clean and simple structures. Hopefully most classes in this subproject will
keep a modular structure so that the user can remove unneeded parts easily.

All extension packages will be located under the package 
org.pentaho.reporting.engine.classic.extensions.

Note:
When adding functionality that requires additional libraries make sure that
all depencies are documented.

Building
------------------

When rebuilding the sources, make sure, that you have all required libraries
in your classpath or exclude these classes, that should not be build.

The provided ANT script checks, whether the required libraries for certain
classes are there, if in doubt, then use this script to compile the sources.

The required libraries for a complete build are:

servlet.jar     - The Servlet package "javax.servlet" is available from Sun.
junit.jar       - The JUnit test library, available from "www.junit.org"
log4j-1.2.3.jar - The Log4J logging framework: "jakarta.apache.org".
JDK 1.4         - For the Java1.4 style logging, configuration and printing 
                  support
JFreeChart-1.0.0-pre2
               - for the JFreeChart demo.

Servlet-Demo classes
--------------------
A simple web application demo and some servlet implementation which show
how to use JFreeReport in an servlet environment are located in the package
org.pentaho.reporting.engine.classic.extensions.servletdemo.

The demo war file can be rebuild using the ant script by executing the
target "build-war".

Provided Modules
----------------

1. java.lang.prefs-configstore-module

This module provides a config store implementation, which stores all
settings by using the java.util.prefs API of JDK 1.4. Under windows, this
will store all values in the registry.

This module required that the configstore base module is present.

2. java-1.4-logging-module

The java1.4 logging module uses the classes from java.lang.logging to
write the log-entries. The log system must be configured elsewhere.

2. log4j-logging-module

The Log4 logging module uses the Log4J library from Apache.org to
perform the logging. This library supports direct logging to the
system log service of Windows and Unix (Syslogd) as well as the logging
into files. The Log4J library must be configured by the application,
the module will not attemp to configure the log system.

3. javax.print-export-gui

This module is a replacement for the print export. The implementation
will use the javax.print API of JDK1.4 to perform the printing. At the
moment, we don't implement anything special in that module.

This module depends on the java.awt.print-export-gui.


JFreeReport Tests:
------------------

JUnit test are located in org.pentaho.reporting.engine.classic.extensions.junit and provide tests
for the base package functionality. These classes are now included in an
own jar file called jfreereport-<version>-junit.jar.
