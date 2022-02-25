Pentaho Reporting Engine
========================


1. Introduction
---------------
Pentaho Reporting is Java class library for generating reports. It provides
flexible reporting and printing functionality using data from multiple sources
and supports output to display devices, printers, PDF, Excel, XHTML,
PlainText, XML and CSV files.

The library is optimized for performance and a small memory footprint and
can run completely in memory without generating temporary files or requiring
extra compilation steps. Pentaho Reporting gives the user a great degree of
flexibility while designing reports.

Pentaho Reporting is developed by Thomas Morgner, Cedric Pronzato and others.

The official web-page for Pentaho Reporting is:

    http://reporting.pentaho.org/

Additional news and development updates are available via Thomas Morgner's
Blog.

    http://www.sherito.org/


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

The Pentaho Reporting Engine and Pentaho Report Designer and Pentaho Report
Design Wizard require Java 1.5 or higher.

  Warning:
  --------
  Pentaho Reporting requires a Java Runtime environment that is fully
  compatible to the Java Platform Specification 5.0 (JSR-176). It will
  not run with the GNU GCJ suite of tools.


3. Installation
---------------
The Pentaho Reporting Engine does not require any special installation steps.
To use it in a Java development project, add it and all its dependencies to
the project's ClassPath and you can start using it.



4. Dependencies and used Libraries
----------------------------------
Pentaho Reporting uses several other open source libraries:

  LibBase            - http://reporting.pentaho.org/libbase/
  LibSerializer      - http://reporting.pentaho.org/libserializer/
  Pixie              - http://reporting.pentaho.org/pixie/
  LibFonts           - http://reporting.pentaho.org/libfonts/
  LibLoader          - http://reporting.pentaho.org/libloader/
  LibFormula         - http://reporting.pentaho.org/libformula/
  LibXml             - http://reporting.pentaho.org/libxml/
  LibRepository      - http://reporting.pentaho.org/librepository/
  LibDocbundle       - http://reporting.pentaho.org/libdocbundle/
  LibFormat          - http://reporting.pentaho.org/libformat/

If you want to use the bean shell module, you will need:

  BeanShell - http://www.beanshell.org/

The RTF or PDF support requires the iText library (version 1.5 or higher):

  iText - http://www.lowagie.com/iText/

The Excel export requires the Apache POI library (version 3.0.1 or higher)

  POI - http://jakarta.apache.org/poi/

The Pentaho Reporting Project provides several extension modules that enable
additional features, like Charting via JFreeChart, Sparklines, Barcodes and
additional datasources (Pentaho-Data-Integration/Kettle, Mondrian, Olap4J).


4. Configuration
----------------
Pentaho Reporting can be configured by placing a 'classic-engine.properties'
file into the root of the classpath. Modifying the supplied properties file
in the package org/pentaho/reporting/engine/classic/core is not recommended,
as these files  provide the built-in default configuration and every other
report configuration source will be preferred over these files.

The configuration contains both global and locale settings

Global settings apply to all reports and usually configure a system wide
default or control behaviour that applies to the whole system (like caches,
or parser configurations).

Local settings configure report specific behaviour, like text encodings or
export target settings. Local settings can be configured from within the
Pentaho Report Designer.



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

Please be sure to provide as much information as you can.  We need to
know the version of Pentaho Reporting that you are using, the JDK version,
and the steps required to replicate the bug.  Include any other
information that you think is relevant.


7. Commercial Support
---------------------
Pentaho offers commercial support for Pentaho Reporting with guaranteed
response times. Please see

  http://www.pentaho.com/products/reporting/

for more details.


8. Building Pentaho reporting
-----------------------------
We use an open source build tool called Ant to build Pentaho Reporting.  An
Ant script (tested using Ant 1.6) is included in the distribution:

    <pentaho-reporting-directory>/build.xml

You can find out more about Ant at:

    http://ant.apache.org/

Ant is licensed under the terms of the Apache Software License (a
popular open source software license).

Pentaho Reporting uses IVY to resolve its dependencies. The first run of
our build scripts will download and install IVY and then download all
required libraries into the "lib" directory.


9. Notes on Output-Target Restrictions and Limitations
------------------------------------------------------

All table based output targets (HTML, Execl, Layouted CSV) have some
layout restrictions. These layout restrictions also apply to the
PlainText output target:

* The elements must be aligned properly, they must not overlap. When
  overlapping, the left element gets preferred over the right element,
  and top elements have a higher preferrence level as bottom elements.

* The elements must be large enough to contain the element. This is
  important when specifying the cell/line height, the elements height
  must be greater than the minimum height.

  For Excel/Html cells the minimum height is equal to the font height,
  so you'll just have to make sure that the cell is able to print at
  least one line of text.

  For PlainText, the line height is defined by the LPI setting, if LPI
  (lines per inch) is set to 10, then the minimum height of a line
  should be 72/10 points, so specifying a element height of 8 is safe.
  If LPI is set to 6, then the line height should be at least 72/6, so
  a element height of 12 should be ok.

* The Epson- and IBM plain text output does not handle 16-bit character
  sets. When using the PlainText output target to send the output to an
  printer, make sure that your printer is able to handle the used text
  encoding.

* When specifying colors for the Excel target, then these colors are
  translated into one of the predefined excel color constants. This
  translation tries to find the nearest color, if that fails, the colors
  could look weird. Using one of the predefined AWT colors is always safe,
  everything else should work, but there are no guarantees.


10. Defining the selectable encodings in the Export Dialogs
-----------------------------------------------------------
By default, the export dialogs make all defined string encodings available
in the encodings comboboxes. As the availability of all encodings is not
necessary for most deployment scenarios, we implemeted a way to customize
the available encodings.

The general availabilty can be defined by specifying the report configuration
key "org.pentaho.reporting.engine.classic.core.modules.gui.base.EncodingsAvailable".
If the key is set to "all", then all supported encodings of the JDK are
enabled and selectable. Setting the key to "none" will disable the encodings
completly, and only the report's defined target encoding is visible and
selectable.

If the configuration key is set to "file", then a encodings property file can
be specified with the configuration key

"org.pentaho.reporting.engine.classic.core.modules.gui.base.EncodingsFile".

The key should point to a properties file, which contains all selectable
encodings. A sample for such a property file can be found as resource

"/org/pentaho/reporting/engine/classic/core/modules/gui/commonswing/encoding-names.properties"

in the library and the sources.


11. Using True-Type Fonts
-------------------------
Unix:
  If you want to use TrueType in Java, you will have to copy all
  needed TrueType fonts to the $JRE/lib/fonts directory. After copying
  the fonts, make sure that you update the font.dir file of that directory,
  so that the JRE can find these fonts.

  When Pentaho Reporting is used to generate PDF files only, copying the files
  will not be necessary, iText uses the all fonts of the default font path
  without using AWT functionality.

  The PDF-Target will register all fonts found in the directory
  "/usr/X11R6/lib/X11/fonts" and all subdirectories. Additionally the directory
  "/usr/share/fonts" is searched for usable font files.

  See also: (This should also work for JDK 1.2.2)
  http://java.sun.com/j2se/1.3/docs/guide/intl/addingfonts.html#adding

MacOS:
  The iText library uses only TTF and Adobe font files to generate PDF
  files. If your system stores the fonts in the ResourceFork, Java may not be
  able to access them.

  You can use FONDU (http://fondu.sourceforge.net/) to convert the MacOS fonts
  into a usable format.

  Additionally the MacOS X font directories are searched in the following order:

  ~/Library/Fonts
  /Library/Fonts
  /Network/Library/Fonts
  /System/Library/Fonts

  Any TTF, AFM or PFB file found in these directories will be registered with
  iText and can be used to create PDF files.

Windows:
  The %WINDIR%/fonts directory is used to search and register the system
  fonts. The JDK uses all fonts that are available, no additional work is needed.


12. Strict Floating Point Warning
---------------------------------
Since Java 1.2 Virtual Maschines are free to implement a relaxed floating
point arithmetics, which increases the exponent size.

This kind of arithmetics seems to break the PDF generation in Pentaho Reporting.
The generated documents have missing characters while the Graphics2D output
is perfectly.

If the non-strict floatingpoint arithmetics is enabled on the VM running
Pentaho Reporting, the following warning will be printed:

WARN: The used VM seems to use a non-strict floating point arithmetics
WARN: Layouts computed with this Java Virtual Maschine may be invalid.
WARN: Pentaho Reporting and the library 'iText' depend on the strict floating
WARN: point rules of Java1.1 as implemented by the Sun Virtual Maschines.
WARN: If you are using the BEA JRockit VM, start the Java VM with the option
WARN: '-Xstrictfp' to restore the default behaviour.

At the moment, this behaviour seems to be limited to the JRockit VM from
BEA, as this is the first VM implementation that uses this feature.
The default floating point behaviour of Java 1.1 can be restored using
the virtual maschine flag '-Xstrictfp' when starting the Java VM.


13. OTHER FEEDBACK
-----------------
For other feedback and comments, please post a message on the
Pentaho Reporting forum. The Forum is available at

  https://community.hitachivantara.com/s/topic/0TO1J0000017kVRWAY/reporting-analytics
