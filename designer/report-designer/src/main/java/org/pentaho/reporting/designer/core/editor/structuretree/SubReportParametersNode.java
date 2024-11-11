/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.editor.structuretree;

/**
 * Todo: Document Me
 *
 * @author Ezequiel Cuellar
 */
public class SubReportParametersNode {
  public static class ImportParametersNode {
    public String toString() {
      return "org.pentaho.reporting.designer.core.editor.structuretree"
        + ".SubReportParametersNode$ImportParametersNode{}"; // NON-NLS
    }
  }

  public static class ExportParametersNode {
    public String toString() {
      return "org.pentaho.reporting.designer.core.editor.structuretree"
        + ".SubReportParametersNode$ExportParametersNode{}"; // NON-NLS
    }
  }

  private ImportParametersNode importParametersNode;
  private ExportParametersNode exportParametersNode;

  public SubReportParametersNode() {
    importParametersNode = new ImportParametersNode();
    exportParametersNode = new ExportParametersNode();
  }

  public ImportParametersNode getImportParametersNode() {
    return importParametersNode;
  }

  public ExportParametersNode getExportParametersNode() {
    return exportParametersNode;
  }

  public String toString() {
    return "org.pentaho.reporting.designer.core.editor.structuretree.SubReportParametersNode{}"; // NON-NLS
  }
}
