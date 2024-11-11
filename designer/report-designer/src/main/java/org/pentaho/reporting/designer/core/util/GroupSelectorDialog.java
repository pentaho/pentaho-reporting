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
