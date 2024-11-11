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


package org.pentaho.reporting.designer.core.editor.report.elements;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.parameters.SubReportDataSourceDialog;
import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import java.awt.*;

abstract class AbstractSubreportHandler<T extends SubReport> implements Runnable {
  final Component component;
  final ReportDesignerContext designerContext;
  final ReportDocumentContext renderContext;
  final T subReport;
  final Band parent;
  final boolean rootband;

  AbstractSubreportHandler( T subReport,
                            Band parent,
                            ReportElementEditorContext dragContext,
                            boolean rootband ) {
    ArgumentNullException.validate( "subReport", subReport );
    ArgumentNullException.validate( "parent", parent );
    ArgumentNullException.validate( "dragContext", dragContext );

    this.subReport = subReport;
    this.parent = parent;
    this.component = dragContext.getRepresentationContainer();
    this.designerContext = dragContext.getDesignerContext();
    this.renderContext = dragContext.getRenderContext();
    this.rootband = rootband;
  }

  AbstractSubreportHandler( T subReport,
                            Band parent,
                            ReportDesignerContext designerContext,
                            ReportDocumentContext renderContext ) {
    ArgumentNullException.validate( "subReport", subReport );
    ArgumentNullException.validate( "parent", parent );
    ArgumentNullException.validate( "designerContext", designerContext );
    ArgumentNullException.validate( "renderContext", renderContext );

    this.subReport = subReport;
    this.parent = parent;
    this.component = designerContext.getView().getParent();
    this.designerContext = designerContext;
    this.renderContext = renderContext;
    this.rootband = parent instanceof AbstractRootLevelBand;
  }

  public void run() {
    if ( !showConfirmationDialog() ) {
      return;
    }
    createSubReportTab();
    showDataSourceDialog();
    completeExecution();
  }

  /**
   * Shows a dialog, that asks the user to confirm creating a sub-report.
   *
   * @return <tt>true</tt> or <tt>false</tt> depending on the user's decision
   */
  abstract boolean showConfirmationDialog();

  /**
   * Adds the sub-report to the designer's context.
   */
  abstract void createSubReportTab();

  /**
   * Shows a dialog asking the user what datasource he wants to include into the sub-report
   */
  void showDataSourceDialog() {
    SubReportDataSourceDialog dialog = createSubReportDataSourceDialog();
    final String queryName = dialog.performSelection( designerContext );
    if ( queryName == null ) {
      subReport.setDataFactory( new CompoundDataFactory() );
    } else {
      subReport.setQuery( queryName );
      doSetQuery( queryName );
    }
  }

  /**
   * Executes additional actions after <tt>queryName</tt> was obtained from dialog. Empty by default
   *
   * @param queryName a query name, strictly not null
   */
  void doSetQuery( String queryName ) {
  }


  /**
   * Creates an instance of <code>SubReportDataSourceDialog</code> to ask user what data sources to include into
   * subreport.
   *
   * @return dialog's instance
   */
  SubReportDataSourceDialog createSubReportDataSourceDialog() {
    final Window window = LibSwingUtil.getWindowAncestor( designerContext.getView().getParent() );
    // Prompt user to either create or use an existing data-source.
    if ( window instanceof Dialog ) {
      return new SubReportDataSourceDialog( (Dialog) window );
    } else if ( window instanceof Frame ) {
      return new SubReportDataSourceDialog( (Frame) window );
    } else {
      return new SubReportDataSourceDialog();
    }
  }


  /**
   * Last actions before the instance will complete execution
   */
  void completeExecution() {
    subReport.addInputParameter( "*", "*" );
    renderContext.getSelectionModel().setSelectedElements( new Object[] { subReport } );
  }
}
