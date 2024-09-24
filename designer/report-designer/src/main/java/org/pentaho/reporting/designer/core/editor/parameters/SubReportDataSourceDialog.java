/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
