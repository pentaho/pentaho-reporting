/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.designer.core.editor.parameters;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import java.awt.*;

/**
 * This dialog appears when user adds the Crosstab/Subreport element onto the canvas
 *
 * @author Sulaiman Karmali
 */
public class SubReportDataSourceDialog extends CommonDialog {
  private ProvisionDataSourcePanel provisionDataSourcePanel;

  public SubReportDataSourceDialog( final Dialog owner ) throws HeadlessException {
    super( owner );
    init();
  }

  public SubReportDataSourceDialog() {
    init();
  }

  public SubReportDataSourceDialog( final Frame aParent ) {
    super( aParent );
    init();
  }

  protected void init() {
    setTitle( Messages.getString( "SubreportDataSourceDialog.Title" ) );
    setSize( 350, 250 );

    provisionDataSourcePanel = new ProvisionDataSourcePanel();

    super.init();
  }

  public String performSelection( final ReportDesignerContext context ) {
    provisionDataSourcePanel.setReportDesignerContext( context );

    final AbstractReportDefinition reportDefinition = context.getActiveContext().getReportDefinition();
    provisionDataSourcePanel.importDataSourcesFromMaster( (CompoundDataFactory) reportDefinition.getDataFactory() );

    provisionDataSourcePanel.expandAllNodes();

    if ( super.performEdit() == false ) {
      return null; // cancel
    }

    // TODO - return a data object that contains both the query and the data-source that is selected
    return provisionDataSourcePanel.getSelectedQueryName();
  }

  public DataFactory getSubReportDataFactory() {
    return provisionDataSourcePanel.getSelectedDataSource();
  }


  protected boolean performEdit() {
    return super.performEdit();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.CrosstabDataSource";
  }


  protected Component createContentPane() {
    return provisionDataSourcePanel;
  }
}
