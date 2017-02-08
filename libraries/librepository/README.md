LibRepository
=============


1. INTRODUCTION
---------------
LibRepository provides a simple abstraction layer to access bulk content that
is organized in a hierarchical layer.

Unlike the JSR-000170, this library does not aim to solve all problems
associated with content storages. The main purpose of LibRepository is to
give users an abstract view over an filesystem like structure so that content
generator and content consumer do no longer have to make assumptions about
where to store the generated content.

The repositories described here should not be used to store other things than
BLOBs.

For the latest news and information about LibRepository, please refer to:

    http://reporting.pentaho.org/librepository/


2. SUPPORT
----------
Free support is available via the Pentaho Reporting forum, follow the link
from the LibRepository home page.  Please note that questions are
answered by volunteers, so there is no guaranteed response time or
level of service.

Please avoid e-mailing the developers directly for support questions.
If you post a message in the forum, then everyone can see the
question, and everyone can see the answer.


3. REPORTING BUGS
-----------------
If you find bugs in LibRepository, we'd like to hear about it so that we
can improve future releases of LibRepository.  Please post a bug report
in our JIRA bug-tracking system at:

    http://jira.pentaho.org/browse/PRD

Please be sure to provide as much information as you can.  We need to
know the version of LibRepository that you are using, the JDK version,
and the steps required to replicate the bug.  Include any other
information that you think is relevant.


4. ANT
------
We use an open source build tool called Ant to build LibRepository.  An
Ant script (tested using Ant 1.6.2) is included in the distribution:

    <librepository-directory>/build.xml

You can find out more about Ant at:

    http://ant.apache.org/

Ant is licensed under the terms of the Apache Software License (a
popular open source software license).


5. OTHER FEEDBACK
-----------------
For other feedback and comments, please post a message on the
Pentaho forums. The Forum is available at

  http://forums.pentaho.org/

