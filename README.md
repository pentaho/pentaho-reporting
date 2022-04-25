# Pentaho Reporting #

Pentaho Reporting is Java class library for generating reports. It provides
flexible reporting and printing functionality using data from multiple sources
and supports output to display devices, printers, PDF, Excel, XHTML,
PlainText, XML and CSV files.

The Pentaho Report Designer provides a graphical editor for report definitions
and can also be used as standalone desktop reporting tool.

The library is optimized for performance and a small memory footprint and
can run completely in memory without generating temporary files or requiring
extra compilation steps. Pentaho Reporting gives the user a great degree of
flexibility while designing reports.

This software is free and opensource software available under the
terms of the GNU Lesser General Public License (LGPL) Version 2.1.

For an up to date list of changes in the releases of Pentaho Reporting,
please visit the JIRA system at http://jira.pentaho.com/browse/PRD
There you will find all releases along with the issues fixed for each
release.



#### Pre-requisites for building the project:
* Maven, version 3+
* Java JDK 11
* This [settings.xml](https://github.com/pentaho/maven-parent-poms/blob/master/maven-support-files/settings.xml) in your <user-home>/.m2 directory

#### Building it

__Build for nightly/release__

All required profiles for the "with-osgi" or standard build are activated by the presence of a property named "release".

```
$ mvn clean install -Drelease
```

This will build, unit test, and package the whole project (all of the sub-modules). The artifact will be generated in: ```assemblies/prd-ce/target```

__Build witn no-osgi__

All required profiles for the smaller "no-osgi" build are activated by the presence of two properties named "no-osgi" and "release".

```
$ mvn clean install -Dno-osgi -Drelease
```

This will build, unit test, and package the whole project (all of the sub-modules). The artifact will be generated in: ```assemblies/prd-ce/target```


__Build for CI/dev__

The `release` builds will compile the source for production (meaning potential obfuscation and/or uglification). To build without that happening, just eliminate the `release` property.

```
$ mvn clean install
```

#### Running the tests

__Unit tests__

This will run all unit tests in the project (and sub-modules). To run integration tests as well, see Integration Tests below.
```
$ mvn test
```

If you want to remote debug a single java unit test (default port is 5005):
```
$ cd core
$ mvn test -Dtest=<<YourTest>> -Dmaven.surefire.debug
```

__Integration tests__
In addition to the unit tests, there are integration tests in the core project.
```
$ mvn verify -DrunITs
```
To run a single integration test:
```
$ mvn verify -DrunITs -Dit.test=<<YourIT>>
```

To run a single integration test in debug mode (for remote debugging in an IDE) on the default port of 5005:
```
$ mvn verify -DrunITs -Dit.test=<<YourIT>> -Dmaven.failsafe.debug
```

__Performance and "Golden" tests__

If you feel paranoid you can also run performance tests.
Expect to wait an hour while all tests run.

```
$ mvn test -Dtest.long=true -Dtest.performance=true
```

If you get OutOfMemoryErrors pointing to a JUnitTask, or if you get OutOfMemory
“PermGen Space” errors, increase the memory of your Ant process to 1024m by
setting the MAVEN\_OPTS environment variable:

```
$ export MAVEN_OPTS="-Xmx1024m"
```

__IntelliJ__

* Don't use IntelliJ's built-in maven. Make it use the same one you use from the commandline.
  * Project Preferences -> Build, Execution, Deployment -> Build Tools -> Maven ==> Maven home directory


#### Available Distributions


Pentaho Reporting is a modular system and depending on the feature set you
use, you may need a different set of applications or libraries.


##### Web-Based Reporting

If you intend to make reports available over the internet, we recommend to
use the Pentaho BI-Server/BI-Platform to host your reports. The Pentaho
BI-Platform is a J2EE-Web-Application that provides all services to run and
manage reports in a Web-2.0 environment.


##### Standalone Reporting

The Pentaho Report-Designer can be used as a desktop reporting environment.
The designer allows you to create and run reports manually and to create all
supported document types (PDF, HTML, Text, RTF, Excel and CSV-files).


##### Embedded Reporting

The Pentaho Reporting Engine consists of a set of base libraries, the
reporting engine core and several extension modules, which provide
additional datasources as well as charting and barcode capabilities.

The Pentaho Reporting Engine ships with a Swing Print Preview dialog, which
can be easily embedded into an existing Java/Swing application. The dialog
offers access to all supported export file formats.

We created a SDK with four simple code examples and documents that walk you
through the code to get you started more easily.


#### System Requirements


Pentaho Reporting requires a minimum of 192MB of allocated heap-space to
process reports. Reports with more than 400 pages or about 50.000 rows of data
may require additional memory and/or adjustments to the global configuration
parameters of the reporting engine.

The Pentaho Reporting Engine requires Java 11 or higher. The Pentaho Report
Designer and Pentaho Report Design Wizard also need at least Java 11 or higher.

##### Warning:
  
  ```
  Pentaho Reporting requires a Java Runtime environment that is fully
  compatible to the Java Platform Specification 8.0 (JSR-337). It will
  not run with the GNU GCJ suite of tools.
  ```


#### Installation


A. Windows

   Download the ZIP distribution.

   The Pentaho Report Designer can be extracted into any directory. We
   recommend that you place the report-designer into
   "C:\Program Files\report-designer" (or an equivalent) directory.

   Start the application by executing (or double-clicking) either the
   "report-designer.bat" file or the launcher.jar file.

B. Linux/Solaris/Unix

   Download either the ZIP or the TAR.GZ distribution.

   The Pentaho Report Designer can be extracted into any directory. We
   recommend that you place the report-designer into
   "/opt/report-designer" (or an equivalent) directory.

   Start the application by executing either

     cd /opt/report-designer
     ./report-designer.sh

   or

     java -jar /opt/report-designer/launcher.jar

C. MacOS

   Download either the ZIP or the TAR.GZ distribution.

   The Pentaho Report Designer can be extracted into any directory.

   Start the application by double clicking on the 

	 report-designer.command file
	
   or
   
   	 executing './report-designer.sh' in a terminal window

#### Documentation


The Javadoc HTML pages for the latest release of Pentaho Reporting are
available at

  http://javadoc.pentaho.com/reporting/


You can also regenerate the Javadocs directly from the source code. There is
a task "javadoc" in the Ant script (see below) that makes this relatively
simple.

The reporting engine and thus most of the features of the report designer are
well documented in Will Gorman's excellent book "Pentaho Reporting 3.5 for
Java Developers".

  https://www.packtpub.com/pentaho-reporting-3-5-for-java-developers/book

Technical articles and general documentation can be found in our Wiki.

  https://pentaho-community.atlassian.net/wiki/display/Reporting/

If you intend to embed Pentaho Reporting in your own applications, the SDK
contains a thorough step-by-step guide to the enclosed examples.


#### Code organization


Our code is split into three groups of modules.

* “/libraries” contains all shared libraries and code that provides
infrastructure that is not necessarily reporting related.
* “/engine” contains the runtime code for Pentaho Reporting. If you want to
embed our reporting engine into your own Swing application or whether you want
to deploy it as part of a J2EE application, this contains all your ever need.
* “/designer” contains our design-time tools, like the report-designer and the
report-design-wizard. It also contains all data source UIs that are used in
both the Report Designer and Pentaho Report Wizard.

At Pentaho we use Scrum as our development process. We end up working on a set
of features for about 3 weeks, called a Sprint. All work for that Sprint goes
into a feature branch (sprint\_XXX-4.0.0GA) and gets merged with the master at
the end of the sprint.

If you want to keep an eye on our work while we are sprinting, check out the
sprint branches. If you prefer is more stable, and are happy with updates every
three weeks, stick to the master-branch.
  

#### Reporting Bugs


Free support is available via the Pentaho Reporting forum.

  https://community.hitachivantara.com/s/topic/0TO1J0000017kVRWAY/reporting-analytics/

Please note that questions are answered by volunteers, so there is no
guaranteed response time or level of service.

Please avoid e-mailing the developers directly for support questions. If you
post a message in the forum, then everyone can see the question, and everyone
can see the answer.

If you found a bug, please either discuss it in the forum or report it in our
JIRA system. You will need to create a JIRA login before reporting the bug.
Access to our JIRA system is free for everyone.

  http://jira.pentaho.com/


#### Commercial Support

Pentaho offers commercial support for Pentaho Reporting with guaranteed
response times. Please see

  http://www.pentaho.com/products/reporting/

for more details.



