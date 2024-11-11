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


package org.pentaho.openformula.ui.util;

import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.openformula.ui.Messages;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FieldSelectorDialog extends CommonDialog {
  private class MouseHandler extends MouseAdapter {
    private MouseHandler() {
    }

    public void mouseClicked( final MouseEvent e ) {
      if ( e.getClickCount() > 1 ) {
        setSelectedDefinition( (FieldDefinition) fieldList.getSelectedValue() );
        getConfirmAction().actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, null ) );
        FieldSelectorDialog.this.dispose();
      }
    }
  }

  public static final String SELECTED_DEFINITION_PROPERTY = "selectedDefinition";
  private JList fieldList;
  private FieldDefinition selectedDefinition;
  private Component focusReturn;

  /**
   * Creates a non-modal dialog without a title with the specified <code>Dialog</code> as its owner.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
   * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see GraphicsEnvironment#isHeadless
   * @see JComponent#getDefaultLocale
   */
  public FieldSelectorDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public FieldSelectorDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public FieldSelectorDialog()
    throws HeadlessException {
    init();
  }

  protected void init() {
    // focus logic currently depends on this
    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );

    fieldList = new JList( new FieldListModel() );
    fieldList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    fieldList.setCellRenderer( new FieldDefinitionCellRenderer() );
    fieldList.addMouseListener( new MouseHandler() );

    setTitle( Messages.getInstance().getString( "FieldSelectorDialog.Title" ) );

    super.init();
  }

  protected String getDialogId() {
    return "LibFormula.FieldSelector"; // NON-NLS
  }

  protected Component createContentPane() {
    return new JScrollPane( fieldList );
  }

  public void setFields( final FieldDefinition[] fields ) {
    fieldList.setModel( new FieldListModel( fields ) );
  }

  public FieldDefinition getSelectedDefinition() {
    return selectedDefinition;
  }

  public void setSelectedDefinition( final FieldDefinition definition ) {
    final FieldDefinition old = this.selectedDefinition;
    this.selectedDefinition = definition;
    firePropertyChange( SELECTED_DEFINITION_PROPERTY, old, definition );
  }

  public FieldDefinition performEdit( final FieldDefinition[] fields, final FieldDefinition selectedDefinition ) {
    setFields( fields );
    this.fieldList.setSelectedValue( selectedDefinition, true );
    this.selectedDefinition = selectedDefinition;

    if ( performEdit() ) {
      return getSelectedDefinition();
    }
    return null;
  }


  protected boolean validateInputs( final boolean onConfirm ) {
    if ( onConfirm ) {
      setSelectedDefinition( (FieldDefinition) fieldList.getSelectedValue() );
    }
    return selectedDefinition != null;
  }

  public void setFocusReturn( Component component ) {
    focusReturn = component;
  }

  private void close() {
    if ( focusReturn != null ) {
      focusReturn.requestFocusInWindow();
    }
  }

  @Override
  public void dispose() {
    close();
    super.dispose();
  }

}
