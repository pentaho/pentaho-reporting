Pentaho Reporting extensions
----------------------------

The classes in this subproject extend Pentaho Reporting with additional
functionality. Please refer to either the Pentaho Reporting Engine Core
or Pentaho Report-Designer for general details on the project and for the
latest Changes.


This extension module provides the following additional functionality:

 * A set of legacy expression. These functions and expressions were defined
   in earlier versions of the report-designer and are only needed when running
   really ancient reports.


The functions in this project are not mean to be used in any sane environment.
They only exist to maintain full backward compatibility with old reports. The
bugs in these files are part of the contract (Sic!) and therefore we do not
change them.

For all functions in here, there exist sane versions in the reporting engine and
the legacy-chart project.

This project must not receive any new additions, unless you want to mark the new
classes as messed up beyond repair and want a safe storage so that you can forget
about your past mistakes.
