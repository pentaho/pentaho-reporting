LibFonts - A Font Metrics handling library
==========================================


1. INTRODUCTION
---------------
LibFonts is a library developed to support advanced layouting in Pentaho
Reporting. This library allows to read physical files and to access
other font system in a uniform way to extract layouting specific informations.
LibFonts also provides heavy caching so that text processing is less
CPU and memory intensive.

For the latest news and information about LibFonts, please refer to:

    http://reporting.pentaho.org/libfonts


2. SUPPORT
----------
Free support is available via the Pentaho Reporting forum, follow the link
from the LibFonts home page.  Please note that questions are
answered by volunteers, so there is no guaranteed response time or
level of service.

Please avoid e-mailing the developers directly for support questions.
If you post a message in the forum, then everyone can see the
question, and everyone can see the answer.

The forum is available at

  http://forums.pentaho.org/


3. REPORTING BUGS
-----------------
If you find bugs in LibFonts, we'd like to hear about it so that we
can improve future releases of LibFonts.  Please post a bug report
in our JIRA bug-tracking system at:

    http://jira.pentaho.org/browse/PRD

Please be sure to provide as much information as you can.  We need to
know the version of LibFonts that you are using, the JDK version,
and the steps required to replicate the bug.  Include any other
information that you think is relevant.


4. ANT
------
We use an open source build tool called Ant to build LibFonts.  An
Ant script (tested using Ant 1.6) is included in the distribution:

    <libfonts-directory>/build.xml

You can find out more about Ant at:

    http://ant.apache.org/

Ant is licensed under the terms of the Apache Software License (a
popular open source software license).


5. Generated Encodings
----------------------

LibFonts uses generated encoding sets to provide replacements for missing
encodings in the non-internationalized JDKs. These encodings have been generated
from files available at 'http://www.unicode.org/'; copies of these files can
be found in the public SVN at svn://source.pentaho.org/ as well.


6. OTHER FEEDBACK
-----------------
For other feedback and comments, please post a message on the
Pentaho forums. The Forum is available at

  http://forums.pentaho.org/
