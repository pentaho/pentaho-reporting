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

package org.pentaho.reporting.designer.core.actions.elements;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.table.CreateTableDialog;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.ElementEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TableLayout;
import org.pentaho.reporting.libraries.designtime.swing.FocusTracker;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class InsertTableAction extends AbstractElementSelectionAction implements SettingsListener {
  private static final long serialVersionUID = 4113715870254584033L;

  private class FocusUpdateHandler extends FocusTracker {
    protected void focusChanged( final Component c ) {
      updateSelection();
    }
  }

  protected static final Float DEFAULT_WIDTH = new Float( 100 );
  protected static final Float DEFAULT_HEIGHT = new Float( 20 );

  private boolean expert;
  private MaturityLevel maturityLevel;
  private boolean deprecated;

  /**
   * This is a listener on the global focus manager and gets called whenever the focus changed. The inspection thinks
   * that this reference should be removed, but as long as the hard-reference is here, this listener will not be garbage
   * collected.
   *
   * @noinspection FieldCanBeLocal
   */
  private FocusUpdateHandler focusTracker;

  public InsertTableAction() {
    putValue( Action.NAME, ActionMessages.getString( "InsertTableAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "InsertTableAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "InsertTableAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "InsertTableAction.Accelerator" ) );

    setEnabled( false );

    maturityLevel = MaturityLevel.Snapshot;
    expert = true;
    deprecated = false;

    // update from system clipboard status
    focusTracker = new FocusUpdateHandler();

    settingsChanged();
    WorkspaceSettings.getInstance().addSettingsListener( this );
  }

  public void settingsChanged() {
    if ( WorkspaceSettings.getInstance().isShowExpertItems() == false && expert ) {
      setVisible( false );
      return;
    }
    if ( WorkspaceSettings.getInstance().isShowDeprecatedItems() == false && deprecated ) {
      setVisible( false );
      return;
    }
    if ( !WorkspaceSettings.getInstance().isMatureFeature( maturityLevel ) ) {
      setVisible( false );
      return;
    }

    setVisible( true );
  }

  protected void selectedElementPropertiesChanged( ReportModelEvent event ) {
  }

  protected void updateSelection() {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      setEnabled( false );
      return;
    }

    Object selectedElement = null;
    final DocumentContextSelectionModel selectionModel1 = getSelectionModel();
    if ( selectionModel1 == null ) {
      setEnabled( false );
      return;
    }

    if ( selectionModel1.getSelectionCount() > 0 ) {
      selectedElement = selectionModel1.getSelectedElement( 0 );
    }
    if ( selectedElement instanceof Band ) {
      setEnabled( true );
    } else {
      setEnabled( false );
    }
  }

  public void actionPerformed( final ActionEvent e ) {
    Object selectedElement = null;
    final DocumentContextSelectionModel selectionModel1 = getSelectionModel();
    if ( selectionModel1 == null ) {
      return;
    }

    if ( selectionModel1.getSelectionCount() > 0 ) {
      selectedElement = selectionModel1.getSelectedElement( 0 );
    }
    final Band band;
    if ( selectedElement instanceof Band ) {
      band = (Band) selectedElement;
    } else {
      return;
    }

    ReportDesignerContext context = getReportDesignerContext();
    final Component parent = context.getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final CreateTableDialog dialog;
    if ( window instanceof JDialog ) {
      dialog = new CreateTableDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new CreateTableDialog( (JFrame) window );
    } else {
      dialog = new CreateTableDialog();
    }

    if ( dialog.createTable() == false ) {
      return;
    }


    try {
      final Band visualElement = createTable( dialog.getColumns(), dialog.getHeaderRows(), dialog.getDataRows() );

      final ElementStyleSheet styleSheet = visualElement.getStyle();
      styleSheet.setStyleProperty( ElementStyleKeys.MIN_WIDTH, DEFAULT_WIDTH );
      styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT, DEFAULT_HEIGHT );

      final ReportDocumentContext activeContext = getActiveContext();
      final UndoManager undo = activeContext.getUndo();
      undo.addChange( ActionMessages.getString( "InsertTableAction.UndoName" ),
        new ElementEditUndoEntry( band.getObjectID(), band.getElementCount(), null, visualElement ) );
      band.addElement( visualElement );
    } catch ( Exception ex ) {
      UncaughtExceptionsModel.getInstance().addException( ex );
    }
  }

  public static Band createTable( final int columns, final int headerRows, final int dataRows ) {
    final Band table = new Band();
    table.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE );
    table.getStyle().setStyleProperty( BandStyleKeys.TABLE_LAYOUT, TableLayout.fixed );

    if ( headerRows > 0 ) {
      final Band tableHeader = new Band();
      tableHeader.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_HEADER );

      for ( int r = 0; r < headerRows; r += 1 ) {
        final Band row = new Band();
        row.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_ROW );
        row.setName( "r-" + r );

        for ( int cellNumber = 0; cellNumber < columns; cellNumber++ ) {
          final Band cell = createCell( 1, 1 );
          row.addElement( cell );
        }
        tableHeader.addElement( row );
      }
      table.addElement( tableHeader );
    }

    final Band tableBody = new Band();
    tableBody.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_BODY );
    for ( int r = 0; r < dataRows; r += 1 ) {
      final Band row = new Band();
      row.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_ROW );
      row.setName( "r-" + ( r + headerRows ) );

      for ( int cellNumber = 0; cellNumber < columns; cellNumber++ ) {
        final Band cell = createCell( 1, 1 );
        row.addElement( cell );
      }
      tableBody.addElement( row );
    }
    table.addElement( tableBody );
    return table;
  }

  public static Band createCell( final int rowSpan, final int colSpan ) {
    final Band tableCell = new Band();
    tableCell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_CELL );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 150f );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    tableCell.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.ROWSPAN, rowSpan );
    tableCell.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.COLSPAN, colSpan );
    return tableCell;
  }

}

