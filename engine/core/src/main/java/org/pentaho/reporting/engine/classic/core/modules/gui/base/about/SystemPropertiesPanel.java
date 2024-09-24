/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base.about;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.pentaho.reporting.engine.classic.core.modules.gui.base.SwingPreviewModule;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * A panel containing a table of system properties.
 *
 * @author David Gilbert
 */
public class SystemPropertiesPanel extends JPanel {
  private class CopyAction implements ActionListener {
    public void actionPerformed( final ActionEvent e ) {
      copySystemPropertiesToClipboard();
    }
  }

  /**
   * The table that displays the system properties.
   */
  private JTable table;

  /**
   * Allows for a popup menu for copying.
   */
  private JPopupMenu copyPopupMenu;

  /**
   * Constructs a new panel.
   */
  public SystemPropertiesPanel() {
    final ResourceBundleSupport bundleSupport =
        new ResourceBundleSupport( Locale.getDefault(), SwingPreviewModule.BUNDLE_NAME, ObjectUtilities
            .getClassLoader( SwingPreviewModule.class ) );

    setLayout( new BorderLayout() );
    this.table = SystemPropertiesPanel.createSystemPropertiesTable();
    add( new JScrollPane( this.table ) );

    // Add a popup menu to copy to the clipboard...
    this.copyPopupMenu = new JPopupMenu();

    final String label = bundleSupport.getString( "system-properties-panel.popup-menu.copy" );
    final KeyStroke accelerator = bundleSupport.getKeyStroke( "system-properties-panel.popup-menu.copy.accelerator" );

    final JMenuItem copyMenuItem = new JMenuItem( label );
    copyMenuItem.setAccelerator( accelerator );
    copyMenuItem.getAccessibleContext().setAccessibleDescription( label );
    copyMenuItem.addActionListener( new CopyAction() );
    this.copyPopupMenu.add( copyMenuItem );

    // add popup Listener to the table
    final PopupListener copyPopupListener = new PopupListener();
    this.table.addMouseListener( copyPopupListener );

  }

  /**
   * Creates and returns a JTable containing all the system properties. This method returns a table that is configured
   * so that the user can sort the properties by clicking on the table header.
   *
   * @return a system properties table.
   */
  public static JTable createSystemPropertiesTable() {
    final ResourceBundle resources = ResourceBundle.getBundle( SwingPreviewModule.BUNDLE_NAME );

    final String[] names =
        new String[] { resources.getString( "system-properties-table.column.name" ),
          resources.getString( "system-properties-table.column.value" ), };

    final Properties sysProps = System.getProperties();

    final TreeMap data = new TreeMap( sysProps );
    final Map.Entry[] entries = (Map.Entry[]) data.entrySet().toArray( new Map.Entry[data.size()] );
    final DefaultTableModel properties = new DefaultTableModel( names, entries.length );
    for ( int i = 0; i < entries.length; i++ ) {
      final Map.Entry entry = entries[i];
      properties.setValueAt( entry.getKey(), i, 0 );
      properties.setValueAt( entry.getValue(), i, 1 );
    }

    final JTable table = new JTable( properties );
    final TableColumnModel model = table.getColumnModel();
    TableColumn column = model.getColumn( 0 );
    column.setPreferredWidth( 200 );
    column = model.getColumn( 1 );
    column.setPreferredWidth( 350 );

    table.setAutoResizeMode( JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS );
    return table;

  }

  /**
   * Copies the selected cells in the table to the clipboard, in tab-delimited format.
   */
  public void copySystemPropertiesToClipboard() {

    final StringBuffer buffer = new StringBuffer( 500 );
    final ListSelectionModel selection = this.table.getSelectionModel();
    final int firstRow = selection.getMinSelectionIndex();
    final int lastRow = selection.getMaxSelectionIndex();
    if ( ( firstRow != -1 ) && ( lastRow != -1 ) ) {
      for ( int r = firstRow; r <= lastRow; r++ ) {
        for ( int c = 0; c < this.table.getColumnCount(); c++ ) {
          buffer.append( this.table.getValueAt( r, c ) );
          if ( c != 2 ) {
            buffer.append( '\t' );
          }
        }
        buffer.append( '\n' );
      }
    }
    final StringSelection ss = new StringSelection( buffer.toString() );
    final Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
    cb.setContents( ss, ss );

  }

  /**
   * Returns the copy popup menu.
   *
   * @return Returns the copyPopupMenu.
   */
  protected final JPopupMenu getCopyPopupMenu() {
    return copyPopupMenu;
  }

  /**
   * Returns the table containing the system properties.
   *
   * @return Returns the table.
   */
  protected final JTable getTable() {
    return table;
  }

  /**
   * A popup listener.
   */
  private class PopupListener extends MouseAdapter {

    /**
     * Default constructor.
     */
    private PopupListener() {
    }

    /**
     * Mouse pressed event.
     *
     * @param e
     *          the event.
     */
    public void mousePressed( final MouseEvent e ) {
      maybeShowPopup( e );
    }

    /**
     * Mouse released event.
     *
     * @param e
     *          the event.
     */
    public void mouseReleased( final MouseEvent e ) {
      maybeShowPopup( e );
    }

    /**
     * Event handler.
     *
     * @param e
     *          the event.
     */
    private void maybeShowPopup( final MouseEvent e ) {
      if ( e.isPopupTrigger() ) {
        getCopyPopupMenu().show( getTable(), e.getX(), e.getY() );
      }
    }
  }

}
