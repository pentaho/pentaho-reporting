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

package gui;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;

public class ExpressionMetaDataEditor extends JFrame {
  private class ShowPropertiesAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ShowPropertiesAction() {
      putValue( Action.NAME, "Show Properties" );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      StringBuffer completeText = new StringBuffer();
      final int[] selectedRows = expressionsTable.getSelectedRows();
      for ( int i = 0; i < selectedRows.length; i++ ) {
        final int selectedRow = selectedRows[ i ];
        final int modelRow = expressionsTable.convertRowIndexToModel( selectedRow );
        final EditableExpressionMetaData data = metaData[ modelRow ];
        data.sort( expressionsTableModel.getLocale() );
        final String text = data.printBundleText( expressionsTableModel.getLocale() );
        System.out.println( "# Printing metadata for " + metaData[ modelRow ].getName() );
        System.out.println( text );
        completeText.append( "# Printing metadata for " + metaData[ modelRow ].getName() );
        completeText.append( "\n" );
        completeText.append( text );
      }

      dialog.showText( completeText.toString() );
    }
  }

  private class ShowModifiedAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ShowModifiedAction() {
      putValue( Action.NAME, "Show Modified" );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      StringBuffer completeText = new StringBuffer();
      for ( int modelRow = 0; modelRow < metaData.length; modelRow++ ) {
        final EditableExpressionMetaData data = metaData[ modelRow ];
        if ( data.isModified() == false ) {
          continue;
        }
        data.sort( expressionsTableModel.getLocale() );
        final String text = data.printBundleText( expressionsTableModel.getLocale() );
        System.out.println( "# Printing metadata for " + data.getName() );
        System.out.println( text );
        completeText.append( "# Printing metadata for " + data.getName() );
        completeText.append( "\n" );
        completeText.append( text );
      }

      if ( completeText.length() == 0 ) {
        return;
      }

      dialog.showText( completeText.toString() );
    }
  }

  private class EditExpressionAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private EditExpressionAction() {
      putValue( Action.NAME, "Edit Expression" );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final int selectedRow = expressionsTable.getSelectedRow();
      if ( selectedRow == -1 ) {
        return;
      }
      final int mapped = expressionsTable.convertRowIndexToModel( selectedRow );
      final EditableExpressionMetaData data = metaData[ mapped ];
      expressionPropertyMetaDataEditor.performEdit( data.getName(), data.getProperties() );
    }
  }

  private class SortAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private SortAction() {
      putValue( Action.NAME, "Sort" );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      Arrays.sort( metaData, new GroupedMetaDataComparator( expressionsTableModel.getLocale() ) );
      for ( int i = 0; i < metaData.length; i++ ) {
        final EditableExpressionMetaData expressionMetaData = metaData[ i ];
        expressionMetaData.sort( expressionsTableModel.getLocale() );
      }
      expressionsTableModel.populate( metaData );

    }
  }


  private class ExitAction extends AbstractAction implements WindowListener {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ExitAction() {
      putValue( Action.NAME, "Quit" );
    }

    /**
     * Invoked the first time a window is made visible.
     */
    public void windowOpened( final WindowEvent e ) {

    }

    /**
     * Invoked when the user attempts to close the window from the window's system menu.
     */
    public void windowClosing( final WindowEvent e ) {
      actionPerformed( null );
    }

    /**
     * Invoked when a window has been closed as the result of calling dispose on the window.
     */
    public void windowClosed( final WindowEvent e ) {

    }

    public void windowIconified( final WindowEvent e ) {

    }

    /**
     * Invoked when a window is changed from a minimized to a normal state.
     */
    public void windowDeiconified( final WindowEvent e ) {

    }

    /**
     * Invoked when the Window is set to be the active Window. Only a Frame or a Dialog can be the active Window. The
     * native windowing system may denote the active Window or its children with special decorations, such as a
     * highlighted title bar. The active Window is always either the focused Window, or the first Frame or Dialog that
     * is an owner of the focused Window.
     */
    public void windowActivated( final WindowEvent e ) {

    }

    /**
     * Invoked when a Window is no longer the active Window. Only a Frame or a Dialog can be the active Window. The
     * native windowing system may denote the active Window or its children with special decorations, such as a
     * highlighted title bar. The active Window is always either the focused Window, or the first Frame or Dialog that
     * is an owner of the focused Window.
     */
    public void windowDeactivated( final WindowEvent e ) {

    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      boolean modified = false;
      for ( int i = 0; i < metaData.length; i++ ) {
        EditableExpressionMetaData data = metaData[ i ];
        if ( data.isModified() ) {
          modified = true;
          break;
        }
      }

      if ( modified ) {
        if ( JOptionPane.showConfirmDialog( ExpressionMetaDataEditor.this,
          "Really Quit? You have modified something.", "Warning", JOptionPane.YES_NO_OPTION )
          == JOptionPane.YES_OPTION ) {
          System.exit( 0 );
        }
      } else {
        System.exit( 0 );
      }
    }
  }

  private EditableExpressionMetaData[] metaData;
  private JTable expressionsTable;
  private EditableMetaDataTableModel expressionsTableModel;
  private ShowTextDialog dialog;
  private ExpressionPropertyMetaDataEditor expressionPropertyMetaDataEditor;

  public ExpressionMetaDataEditor()
    throws HeadlessException {
    setTitle( "Expression Metadata Editor" );
    setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

    final ExitAction exitAction = new ExitAction();
    addWindowListener( exitAction );

    expressionsTableModel = new EditableMetaDataTableModel();
    expressionsTable = new JTable( expressionsTableModel );
    expressionsTable.setDefaultRenderer( String.class, new EditableMetaDataRenderer() );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( new JScrollPane( expressionsTable ), BorderLayout.CENTER );
    setContentPane( contentPane );

    final JMenu actionsMenu = new JMenu( "Actions" );
    final SortAction sortAction = new SortAction();
    actionsMenu.add( sortAction );
    actionsMenu.add( new EditExpressionAction() );
    actionsMenu.add( new ShowPropertiesAction() );
    actionsMenu.add( new ShowModifiedAction() );
    actionsMenu.addSeparator();
    actionsMenu.add( exitAction );

    final JMenuBar menuBar = new JMenuBar();
    menuBar.add( actionsMenu );
    setJMenuBar( menuBar );

    final JToolBar toolBar = new JToolBar();
    toolBar.add( sortAction );
    toolBar.add( new EditExpressionAction() );
    toolBar.add( new ShowPropertiesAction() );
    toolBar.add( new ShowModifiedAction() );
    contentPane.add( toolBar, BorderLayout.NORTH );

    final ExpressionMetaData[] allExpressionMetaDatas = ExpressionRegistry.getInstance().getAllExpressionMetaDatas();
    metaData = new EditableExpressionMetaData[ allExpressionMetaDatas.length ];
    for ( int i = 0; i < allExpressionMetaDatas.length; i++ ) {
      ExpressionMetaData expressionMetaData = allExpressionMetaDatas[ i ];
      metaData[ i ] = new EditableExpressionMetaData( (DefaultExpressionMetaData) expressionMetaData );
    }
    expressionsTableModel.populate( metaData );
    sortAction.actionPerformed( null );

    dialog = new ShowTextDialog( this );
    expressionPropertyMetaDataEditor = new ExpressionPropertyMetaDataEditor( this );
  }

  public static void main( String[] args ) {
    ClassicEngineBoot.getInstance().start();
    ExpressionMetaDataEditor editor = new ExpressionMetaDataEditor();
    editor.pack();
    editor.setSize( 800, 600 );
    LibSwingUtil.centerFrameOnScreen( editor );
    editor.setVisible( true );
  }

}
