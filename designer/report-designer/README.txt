Pentaho Reporting 3.7
=====================


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


2. Available Distributions
--------------------------

Pentaho Reporting is a modular system and depending on the feature set you
use, you may need a different set of applications or libraries.


Web-Based Reporting

If you intend to make reports available over the internet, we recommend to
use the Pentaho BI-Server/BI-Platform to host your reports. The Pentaho
BI-Platform is a J2EE-Web-Application that provides all services to run and
manage reports in a Web-2.0 environment.


Standalone Reporting

The Pentaho Report-Designer can be used as a desktop reporting environment.
The designer allows you to create and run reports manually and to create all
supported document types (PDF, HTML, Text, RTF, Excel and CSV-files).


Embedded Reporting

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

  https://pentaho-community.atlassian.net/wiki/display/Reporting/

If you intend to embed Pentaho Reporting in your own applications, the SDK
contains a thorough step-by-step guide to the enclosed examples.


6. Reporting Bugs
-----------------
Free support is available via the Pentaho Reporting forum.

  https://community.hitachivantara.com/s/topic/0TO1J0000017kVRWAY/reporting-analytics

Please note that questions are answered by volunteers, so there is no
guaranteed response time or level of service.

Please avoid e-mailing the developers directly for support questions. If you
post a message in the forum, then everyone can see the question, and everyone
can see the answer.

If you found a bug, please either discuss it in the forum or report it in our
JIRA system. You will need to create a JIRA login before reporting the bug.
Access to our JIRA system is free for everyone.

  http://jira.pentaho.com/


7. Commercial Support
---------------------
Pentaho offers commercial support for Pentaho Reporting with guaranteed
response times. Please see

  http://www.pentaho.com/products/reporting/

for more details.

