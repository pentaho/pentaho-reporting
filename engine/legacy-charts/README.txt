Pentaho Reporting extensions
----------------------------

The classes in this subproject extend Pentaho Reporting with additional
functionality. Please refer to either the Pentaho Reporting Engine Core
or Pentaho Report-Designer for general details on the project and for the
latest Changes.


This extension module provides the following additional functionality:

 * A set of expressions and functions to generate charts via JFreeChart.

 * A chart-element to use these expressions and functions.

--

URLGeneration:

using a formula, with fields as

CategoryURLGenerator:
CategoryTooltipGenerator:

  chart::series = dataSet.getRowKey()
  chart::category = dataSet.getColumnKey();
  chart::value = dataSet.getValue()


XY(Z)URLGenerator:
XY(Z)TooltipGenerator:
  chart::xValue =
  chart::yValue =
  chart::zValue =
  chart::xIndex =
  chart::yIndex =
  chart::zIndex =

