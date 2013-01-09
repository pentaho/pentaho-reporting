Pentaho Reporting extensions
----------------------------

The classes in this subproject extend Pentaho Reporting with additional
functionality. Please refer to either the Pentaho Reporting Engine Core
or Pentaho Report-Designer for general details on the project and for the
latest Changes.


This extension module provides the following additional functionality:

 * A datasource that can produce table-models by running a script written
   in JavaScript, BeanShell or any other script language that is supported
   by the Bean-Scripting-Framework.

 * A JavaScript Expression.


Note
----
This is a legacy datasource with limited general value. It exists to maintain
backward compatibility with the XPath datasources of the Pentaho Platform and
the report-designer.

The datasource only works on a specific XML fileformat and requires special
comments inside the file to recognized a limited set of datatypes.
