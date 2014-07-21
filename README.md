Pentaho Reporting
=================

0. Maven Warning
----------------

If you intend to use Maven to use Pentaho Reporting, be aware that Maven does
not resolve correctly against Pentaho's repository. The Pentaho build process
produces invalid snapshot builds.

To fix that, run the build with the ant-property set to:

reporting.build.file=${REPORTING_SOURCES}/build-res/report-shared-experimental.xml

A local publish will work fine as long as you use ivy to resolve the dependencies.


1. Introduction
---------------

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


2. Available Distributions
--------------------------

Pentaho Reporting is a modular system and depending on the feature set you
use, you may need a different set of applications or libraries.


### Web-Based Reporting

If you intend to make reports available over the internet, we recommend to
use the Pentaho BI-Server/BI-Platform to host your reports. The Pentaho
BI-Platform is a J2EE-Web-Application that provides all services to run and
manage reports in a Web-2.0 environment.


### Standalone Reporting

The Pentaho Report-Designer can be used as a desktop reporting environment.
The designer allows you to create and run reports manually and to create all
supported document types (PDF, HTML, Text, RTF, Excel and CSV-files).


### Embedded Reporting

The Pentaho Reporting Engine consists of a set of base libraries, the
reporting engine core and several extension modules, which provide
additional datasources as well as charting and barcode capabilities.

The Pentaho Reporting Engine ships with a Swing Print Preview dialog, which
can be easily embedded into an existing Java/Swing application. The dialog
offers access to all supported export file formats.

We created a SDK with four simple code examples and documents that walk you
through the code to get you started more easily.


3. System Requirements
----------------------

Pentaho Reporting requires a minimum of 192MB of allocated heap-space to
process reports. Reports with more than 400 pages or about 50.000 rows of data
may require additional memory and/or adjustments to the global configuration
parameters of the reporting engine.

The Pentaho Reporting Engine requires Java 1.5 or higher. The Pentaho Report
Designer and Pentaho Report Design Wizard need at least Java 1.6 or higher.

  Warning:
  --------
  Pentaho Reporting requires a Java Runtime environment that is fully
  compatible to the Java Platform Specification 5.0 (JSR-176). It will
  not run with the GNU GCJ suite of tools.


4. Installation
---------------

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

   Download the Mac-OS specific ZIP file and extract it by double clicking
   on it in the Finder. Move the extracted "Pentaho Report Designer"
   application into your Applications folder.


5. Documentation
----------------

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

  http://wiki.pentaho.com/display/Reporting/

If you intend to embed Pentaho Reporting in your own applications, the SDK
contains a thorough step-by-step guide to the enclosed examples.


6. Compilation
--------------


### Code organization

Our code is split into three groups of modules.

* “/libraries” contains all shared libraries and code that provides
infrastructure that is not necessarily reporting related.
* “/engine” contains the runtime code for Pentaho Reporting. If you want to
embed our reporting engine into your own Swing application or whether you want
to deploy it as part of a J2EE application, this contains all your ever need.
* “/designer” contains our design-time tools, like the report-designer and the
report-design-wizard. It also contains all data source UIs that are used in
both the Report Designer and Pentaho Report Wizard.


If you use IntelliJ Idea for your Java work, then you will be delighted to find
that the sources act as a fully configured IntelliJ project. Just open the
‘pentaho-reporting’ directory as project in IntelliJ and off you go.  If you
use Eclipse, well, why not give IntelliJ a try?  Branching system


At Pentaho we use Scrum as our development process. We end up working on a set
of features for about 3 weeks, called a Sprint. All work for that Sprint goes
into a feature branch (sprint\_XXX-4.0.0GA) and gets merged with the master at
the end of the sprint.


If you want to keep an eye on our work while we are sprinting, check out the
sprint branches. If you prefer is more stable, and are happy with updates every
three weeks, stick to the master-branch.


During a Sprint, our CI system will build and publish artifacts from the sprint
branches. If you don’t want that, then it is now easy to get your own build up
and running in under 5 minutes (typing time, not waiting time).  Building the
project


The project root contains a global multibuild.xml file that can build all
modules in one go. If you want it more finely granulated, each top level group
(‘libraries’, ‘engine’, ‘designer’) contains its own ‘build.xml’ file to
provide the same service for these modules.


To successfully build Pentaho Reporting, you do need Apache Ant 1.8.2 or newer.
Go download it from the Apache Ant Website if you haven’t done it yet.


After you cloned our Git repository, you have all the source files on your
computer. But before you can use the project, you will have to download the
third party libraries used in the code.


On a command line in the project directory, call

	ant resolve

to download all libraries.


If you’re going to use IntelliJ for your work, you are all set now and can

start our IntelliJ project.

To build all projects locally, invoke

	ant continuous-local-testless

to run.


If you feel paranoid and want to run the tests while building, then use the
‘continuous-local’ target. This can take quite some time, as it also runs all
tests. Expect to wait an hour while all tests run.


	ant continuous-local


After the process is finished, you will find “Report Designer” zip and tar.gz
packages in the folder “/designer/report-designer/assembly/dist”.


If you get OutOfMemoryErrors pointing to a JUnitTask, or if you get OutOfMemory
“PermGen Space” errors, increase the memory of your Ant process to 1024m by
setting the ANT\_OPTS environment variable:


	export ANT\_OPTS="-Xmx1024m -XX:MaxPermSize=256m"


### Building the project on a CI server


Last but not least: Do you want to run Pentaho Reporting in your own continuous
integration server and you want to publish all created artifacts to your own
maven-server? Then make sure you set up Maven to allow you to publish files to a
repository.

1. Install Artifactory or any other maven repository server.
2. Copy one of the ‘ivy-settings.xml’ configurations from any of the modules and
   edit it to point to your own Maven server. Put this file into a location
   outside of the project, for instance into “$HOME/prd-ivy-settings.xml”
3. Download and install maven as usual, then configure it to talk to the
   Artifactory server.

Edit your $HOME/.m2/settings.xml file and locate the ‘servers’ tag. Then
configure it with the username and password of a user that can publish to your
Artifactory server.  Replace ‘your-server-id’ with a name describing your
server. You will need that later.  Replace ‘publish-username’ and
‘publish-password’ with the username and password of an account of your
artifactory installation that has permission to deploy artifacts.

	<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"           
	          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"           
	          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
	                    http://maven.apache.org/xsd/settings-1.0.0.xsd">
	   ...
	   <servers>
	     <server>
	       <id>your-server-id</id>
	       <username>publish-username</username>
	       <password>publish-password</password>
	       <configuration>
	         <wagonprovider>httpclient</wagonprovider>
	         <httpconfiguration>
	           <put>
	             <params>
	               <param>
	                 <name>http.authentication.preemptive</name>
	                 <value>%b,true</value>
	               </param>
	             </params>
	           </put>
	         </httpconfiguration>
	       </configuration>
	     </server>
	   </servers>
	    ..
	</settings>


Now set up your CI job. You can either override the ivy properties on each CI
job, or your can create a global default by creating a
‘$HOME/.pentaho-reporting-build-settings.properties’ file. The settings of this
file will be included in all Ant-builds for Pentaho Reporting projects.


	ivy.settingsurl=file:${user.home}/prd-ivy-settings.xml
	ivy.repository.id=your-server-id
	ivy.repository.publish=http://repo.your-server.com/ext-snapshot-local


After that, test your setup by invoking


	ant -f multibuild.xml continuous


It should run without errors now. If you see errors on publish, check your Maven
configuration or your Artifactory installation.



7. Reporting Bugs
-----------------

Free support is available via the Pentaho Reporting forum.

  http://forums.pentaho.org/forumdisplay.php?f=57

Please note that questions are answered by volunteers, so there is no
guaranteed response time or level of service.

Please avoid e-mailing the developers directly for support questions. If you
post a message in the forum, then everyone can see the question, and everyone
can see the answer.

If you found a bug, please either discuss it in the forum or report it in our
JIRA system. You will need to create a JIRA login before reporting the bug.
Access to our JIRA system is free for everyone.

  http://jira.pentaho.com/


8. Commercial Support
---------------------
Pentaho offers commercial support for Pentaho Reporting with guaranteed
response times. Please see

  http://www.pentaho.com/products/reporting/

for more details.

