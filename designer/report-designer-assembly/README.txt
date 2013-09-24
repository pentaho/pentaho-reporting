Pentaho Reporting assembly
--------------------------

This is the assembly project. The assembly pulls all libraries from IVY/Maven
and produces the distributable tar.gz and zip files.

Use

  ant dist

to create the files.


Building a Mac App-bundle
-------------------------

To successfully build an Mac-App-Bundle, the build-property "mac.java.runtime.home"
must be set and must point to a OSX JDK-1.7 installation. The contents of that
JDK will be used to create an embedded JRE for the application.

You can execute this step on any system, as long as you have a copy of an MacOS
JDK available. The content will only be copied, not executed.

If the variable "mac.java.runtime.home" is undefined, the dist-target will not
create an Mac-OS build.