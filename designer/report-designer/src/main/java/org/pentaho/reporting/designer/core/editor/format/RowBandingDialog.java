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


package org.pentaho.reporting.designer.core.editor.format;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.engine.classic.core.function.RowBandingFunction;
import org.pentaho.reporting.libraries.designtime.swing.ColorCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.ColorComboBox;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;
import org.pentaho.reporting.libraries.designtime.swing.ValuePassThroughCellEditor;

import javax.swing.*;
import java.awt.*;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class RowBandingDialog extends CommonDialog {
  private JTextField elementNameField;
  private ColorComboBox visibleColorBox;
  private ColorComboBox invisibleColorBox;
  private KeyedComboBoxModel<Boolean, String> pageStateModel;
  private KeyedComboBoxModel<Boolean, String> initialStateModel;

  public RowBandingDialog() {
    init();
  }

  public RowBandingDialog( final Frame owner ) {
    super( owner );
    init();
  }

  public RowBandingDialog( final Dialog owner ) {
    super( owner );
    init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.RowBanding";
  }

  protected Component createContentPane() {
    setTitle( Messages.getString( "RowBandingDialog.RowBanding" ) );

    visibleColorBox = new ColorComboBox();
    visibleColorBox.setEditable( true );
    visibleColorBox.setEditor( new ValuePassThroughCellEditor( visibleColorBox, new ColorCellRenderer() ) );

    invisibleColorBox = new ColorComboBox();
    invisibleColorBox.setEditable( true );
    invisibleColorBox.setEditor( new ValuePassThroughCellEditor( invisibleColorBox, new ColorCellRenderer() ) );

    elementNameField = new JTextField();
    elementNameField.setColumns( 40 );

    initialStateModel = createInitialStateModel();
    pageStateModel = createNewPageStateModel();

    final JComboBox initialStateBox = new JComboBox( initialStateModel );
    final JComboBox newPageStateBox = new JComboBox( pageStateModel );

    final JPanel contentPanel = new JPanel();
    contentPanel.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    contentPanel.add( new JLabel( Messages.getString( "RowBandingDialog.VisibleColor" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add( visibleColorBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    contentPanel.add( new JButton( new SelectCustomColorAction( visibleColorBox ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    contentPanel.add( new JLabel( Messages.getString( "RowBandingDialog.InvisibleColor" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add( invisibleColorBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    contentPanel.add( new JButton( new SelectCustomColorAction( invisibleColorBox ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    contentPanel.add( new JLabel( Messages.getString( "RowBandingDialog.Element" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add( elementNameField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    contentPanel.add( new JLabel( Messages.getString( "RowBandingDialog.StateOnNewGroup" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add( initialStateBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    contentPanel.add( new JLabel( Messages.getString( "RowBandingDialog.StateOnNewPage" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add( newPageStateBox, gbc );

    return contentPanel;
  }

  public boolean performEdit( final RowBandingFunction function ) {
    if ( function == null ) {
      throw new NullPointerException();
    }

    final String element = function.getElement();
    if ( element != null ) {
      elementNameField.setText( element );
    } else {
      elementNameField.setText( "" );
    }

    initialStateModel.setSelectedKey( Boolean.valueOf( function.getInitialState() ) );
    pageStateModel.setSelectedKey( function.getNewPageState() );
    invisibleColorBox.setValueFromModel( function.getInvisibleBackground() );
    visibleColorBox.setValueFromModel( function.getVisibleBackground() );


    if ( super.performEdit() == false ) {
      return false;
    }

    final String elementName = elementNameField.getText();
    if ( elementName.length() == 0 ) {
      function.setElement( null );
    } else {
      function.setElement( elementName );
    }
    function.setInitialState( Boolean.TRUE.equals( initialStateModel.getSelectedKey() ) );
    function.setNewPageState( pageStateModel.getSelectedKey() );
    function.setInvisibleBackground( invisibleColorBox.getValueFromModel() );
    function.setVisibleBackground( visibleColorBox.getValueFromModel() );
    return true;
  }

  private KeyedComboBoxModel<Boolean, String> createNewPageStateModel() {
    final KeyedComboBoxModel<Boolean, String> model = new KeyedComboBoxModel<Boolean, String>();
    model.add( null, Messages.getString( "RowBandingDialog.SameAsInitialState" ) );
    model.add( Boolean.TRUE, Messages.getString( "RowBandingDialog.Visible" ) );
    model.add( Boolean.FALSE, Messages.getString( "RowBandingDialog.Invisible" ) );
    return model;
  }

  private KeyedComboBoxModel<Boolean, String> createInitialStateModel() {
    final KeyedComboBoxModel<Boolean, String> model = new KeyedComboBoxModel<Boolean, String>();
    model.add( Boolean.TRUE, Messages.getString( "RowBandingDialog.Visible" ) );
    model.add( Boolean.FALSE, Messages.getString( "RowBandingDialog.Invisible" ) );
    return model;
  }
}
