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

package org.pentaho.reporting.designer.core.util;

import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.FixDefaultListCellRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GroupSelectorDialog extends CommonDialog {
  private class MouseHandler extends MouseAdapter {
    public void mouseClicked( final MouseEvent e ) {
      if ( e.getClickCount() > 1 && e.getButton() == MouseEvent.BUTTON1 ) {
        final String selectedValue = (String) fieldList.getSelectedValue();
        setSelectedGroup( selectedValue );
        if ( selectedValue != null ) {
          setConfirmed( true );
          GroupSelectorDialog.this.dispose();
        }
      }
    }
  }

  private class SelectionUpdateHandler implements ListSelectionListener {
    private SelectionUpdateHandler() {
    }

    public void valueChanged( final ListSelectionEvent e ) {
      setSelectedGroup( (String) fieldList.getSelectedValue() );
    }
  }

  private JList fieldList;
  private String selectedGroup;

  /**
   * Creates a non-modal dialog without a title with the specified <code>Dialog</code> as its owner.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
   * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public GroupSelectorDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public GroupSelectorDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public GroupSelectorDialog()
    throws HeadlessException {
    init();
  }

  protected void init() {
    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );

    fieldList = new JList();
    fieldList.setCellRenderer( new FixDefaultListCellRenderer() );
    fieldList.setVisibleRowCount( 5 );
    fieldList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    fieldList.addMouseListener( new MouseHandler() );
    fieldList.addListSelectionListener( new SelectionUpdateHandler() );

    setTitle( UtilMessages.getInstance().getString( "GroupSelectorDialog.Title" ) );
    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.GroupSelector";
  }

  protected Component createContentPane() {
    return new JScrollPane( fieldList );
  }

  public void setGroups( final String[] queries ) {
    final DefaultListModel listModel = new DefaultListModel();
    for ( int i = 0; i < queries.length; i++ ) {
      listModel.addElement( queries[ i ] );
    }
    fieldList.setModel( listModel );
  }


  public String getSelectedGroup() {
    return selectedGroup;
  }

  public void setSelectedGroup( final String selectedQuery ) {
    final String oldQuery = this.selectedGroup;
    this.selectedGroup = selectedQuery;
    firePropertyChange( "selectedGroup", oldQuery, selectedQuery );//NON-NLS
    getConfirmAction().setEnabled( validateInputs( false ) );
  }

  public String performEdit( final String[] queries, final String selectedGroup ) {
    setGroups( queries );
    setSelectedGroup( selectedGroup );
    if ( super.performEdit() ) {
      return getSelectedGroup();
    }
    return selectedGroup;
  }

  protected boolean validateInputs( final boolean onConfirm ) {
    final String selectedValue = (String) fieldList.getSelectedValue();
    return ( StringUtils.isEmpty( selectedValue ) == false );
  }
}
