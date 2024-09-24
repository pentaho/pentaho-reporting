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

package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportQueryNode;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.Anonymizer;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AnonymizeDataAction extends AbstractElementSelectionAction {
  private static class AnonymizeDataSourceTask extends ConvertDataSourceAction.ConvertDataSourceTask {
    private Anonymizer anonymizer;

    private AnonymizeDataSourceTask( final ReportDocumentContext activeContext ) {
      super( activeContext );
      this.anonymizer = new Anonymizer();
    }

    protected Object process( final Object o ) throws BeanException {
      return anonymizer.anonymize( o );
    }
  }

  public AnonymizeDataAction() {
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getLayoutBandsIcon() );
    putValue( Action.NAME, ActionMessages.getString( "AnonymizeDataAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "AnonymizeDataAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "AnonymizeDataAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "AnonymizeDataAction.Accelerator" ) );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  protected void updateSelection() {
    final DocumentContextSelectionModel model = getSelectionModel();
    if ( model == null ) {
      setEnabled( false );
      return;
    }

    final Object[] selectedObjects = model.getSelectedElements();
    for ( int i = 0; i < selectedObjects.length; i++ ) {
      final Object selectedObject = selectedObjects[ i ];
      if ( selectedObject instanceof ReportQueryNode == false ) {
        continue;
      }
      final ReportQueryNode queryNode = (ReportQueryNode) selectedObject;
      final DataFactory dataFactory = queryNode.getDataFactory();
      final DataFactoryMetaData metadata = dataFactory.getMetaData();
      if ( metadata.isEditable() ) {
        setEnabled( true );
        return;
      }
    }

    setEnabled( false );
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    final Thread thread = new Thread( new AnonymizeDataSourceTask( getActiveContext() ) );
    thread.setName( "AnonymizeDataSource-Worker" );
    thread.setDaemon( true );
    BackgroundCancellableProcessHelper.executeProcessWithCancelDialog( thread, null,
      getReportDesignerContext().getView().getParent(), ActionMessages.getString( "AnonymizeDataAction.TaskTitle" ) );
  }
}
