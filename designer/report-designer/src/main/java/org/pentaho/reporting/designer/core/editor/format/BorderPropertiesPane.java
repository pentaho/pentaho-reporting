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

package org.pentaho.reporting.designer.core.editor.format;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.designtime.swing.ColorComboBox;
import org.pentaho.reporting.libraries.designtime.swing.EllipsisButton;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BorderPropertiesPane extends JPanel {
  private class BorderSelectionUpdateHandler implements BorderSelectionListener {
    private BorderSelectionUpdateHandler() {
    }

    public void selectionAdded( final BorderSelectionEvent event ) {
      final BorderSelection borderSelection = event.getSelection();
      applyBorder( borderSelection );

    }


    public void selectionRemoved( final BorderSelectionEvent event ) {
      // ignored ..
    }
  }

  private class BorderStyleUpdateHandler implements ListSelectionListener, ActionListener, ChangeListener {
    private BorderStyleUpdateHandler() {
    }

    public void actionPerformed( final ActionEvent e ) {
      final BorderSelection[] selections = borderEditorPanel.getSelectionModel().getSelections();
      for ( int i = 0; i < selections.length; i++ ) {
        final BorderSelection borderSelection = selections[ i ];
        applyBorder( borderSelection );
      }
    }

    public void valueChanged( final ListSelectionEvent e ) {
      final BorderSelection[] selections = borderEditorPanel.getSelectionModel().getSelections();
      for ( int i = 0; i < selections.length; i++ ) {
        final BorderSelection borderSelection = selections[ i ];
        applyBorder( borderSelection );
      }
    }

    public void stateChanged( final ChangeEvent e ) {
      final BorderSelection[] selections = borderEditorPanel.getSelectionModel().getSelections();
      for ( int i = 0; i < selections.length; i++ ) {
        final BorderSelection borderSelection = selections[ i ];
        applyBorder( borderSelection );
      }
    }
  }

  private class SelectNoneBordersAction extends AbstractAction {
    private SelectNoneBordersAction() {
      putValue( Action.NAME, Messages.getString( "BorderPropertiesPane.None" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      borderEditorPanel.getSelectionModel().clearSelection();
    }
  }

  private class SelectAllBordersAction extends AbstractAction {
    private SelectAllBordersAction() {
      putValue( Action.NAME, Messages.getString( "BorderPropertiesPane.All" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      borderEditorPanel.getSelectionModel().clearSelection();
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.TOP );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.TOP_LEFT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.TOP_RIGHT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.LEFT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.RIGHT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.BOTTOM );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.BOTTOM_LEFT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.BOTTOM_RIGHT );
    }
  }

  private class SelectHorizontalBordersAction extends AbstractAction {
    private SelectHorizontalBordersAction() {
      putValue( Action.NAME, Messages.getString( "BorderPropertiesPane.Horizontal" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      borderEditorPanel.getSelectionModel().clearSelection();
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.TOP );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.TOP_LEFT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.TOP_RIGHT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.BOTTOM );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.BOTTOM_LEFT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.BOTTOM_RIGHT );
    }
  }

  private class SelectVerticalBordersAction extends AbstractAction {
    private SelectVerticalBordersAction() {
      putValue( Action.NAME, Messages.getString( "BorderPropertiesPane.Vertical" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      borderEditorPanel.getSelectionModel().clearSelection();
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.TOP_LEFT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.TOP_RIGHT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.LEFT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.RIGHT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.BOTTOM_LEFT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.BOTTOM_RIGHT );
    }
  }

  private class SelectLeftBordersAction extends AbstractAction {
    private SelectLeftBordersAction() {
      putValue( Action.NAME, Messages.getString( "BorderPropertiesPane.Left" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      borderEditorPanel.getSelectionModel().clearSelection();
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.TOP_LEFT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.LEFT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.BOTTOM_LEFT );
    }
  }

  private class SelectRightBordersAction extends AbstractAction {
    private SelectRightBordersAction() {
      putValue( Action.NAME, Messages.getString( "BorderPropertiesPane.Right" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      borderEditorPanel.getSelectionModel().clearSelection();
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.TOP_RIGHT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.RIGHT );
      borderEditorPanel.getSelectionModel().addSelection( BorderSelection.BOTTOM_RIGHT );
    }
  }

  private static class FloatSpinner extends JSpinner {
    public FloatSpinner() {
      super( createSpinnerModel() );
    }

    protected JComponent createEditor( final SpinnerModel model ) {
      return new NumberEditor( this, "0.0##" );
    }

    protected static SpinnerNumberModel createSpinnerModel() {
      return new SpinnerNumberModel( new Float( 0 ), new Float( 0 ), new Float( Short.MAX_VALUE ), new Float( 1 ) );
    }

  }


  private JSpinner cornerWidth;
  private JSpinner cornerHeight;

  private JSpinner paddingLeft;
  private JSpinner paddingTop;
  private JSpinner paddingBottom;
  private JSpinner paddingRight;
  private JComboBox colorSelectorBox;
  private JList styleList;
  private BorderRenderPanel borderEditorPanel;
  private JSpinner borderWidth;
  private JTextField minimumWidth;
  private JTextField minimumHeight;

  public BorderPropertiesPane() {
    final BorderStyleUpdateHandler updateHandler = new BorderStyleUpdateHandler();
    styleList = new JList( createBorderStyleModel() );
    styleList.addListSelectionListener( updateHandler );
    colorSelectorBox = new ColorComboBox();
    colorSelectorBox.addActionListener( updateHandler );

    minimumWidth = new JTextField();
    minimumHeight = new JTextField();

    paddingTop = new FloatSpinner();
    paddingLeft = new FloatSpinner();
    paddingBottom = new FloatSpinner();
    paddingRight = new FloatSpinner();

    cornerWidth = new FloatSpinner();
    cornerWidth.addChangeListener( updateHandler );
    cornerHeight = new FloatSpinner();
    cornerHeight.addChangeListener( updateHandler );

    borderEditorPanel = new BorderRenderPanel();
    borderEditorPanel.setMinimumSize( new Dimension( 100, 100 ) );
    borderEditorPanel.setPreferredSize( new Dimension( 250, 250 ) );
    borderEditorPanel.setMaximumSize( new Dimension( 250, 250 ) );
    borderEditorPanel.getSelectionModel().addBorderSelectionListener( new BorderSelectionUpdateHandler() );

    borderWidth = new FloatSpinner();
    borderWidth.addChangeListener( updateHandler );

    final JPanel borderCornerCarrier = new JPanel( new GridBagLayout() );
    borderCornerCarrier
      .setBorder( BorderFactory.createTitledBorder( Messages.getString( "BorderPropertiesPane.RoundedCorners" ) ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 0 );
    borderCornerCarrier.add( new JLabel( Messages.getString( "BorderPropertiesPane.Width" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    borderCornerCarrier.add( cornerWidth, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 5, 0 );
    borderCornerCarrier.add( new JLabel( Messages.getString( "BorderPropertiesPane.Height" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    borderCornerCarrier.add( cornerHeight, gbc );

    final JPanel arrangementPanel = new JPanel();
    arrangementPanel.setLayout( new FlowLayout() );
    arrangementPanel.add( new JButton( new SelectNoneBordersAction() ) );
    arrangementPanel.add( new JButton( new SelectAllBordersAction() ) );
    arrangementPanel.add( new JButton( new SelectHorizontalBordersAction() ) );
    arrangementPanel.add( new JButton( new SelectVerticalBordersAction() ) );
    arrangementPanel.add( new JButton( new SelectLeftBordersAction() ) );
    arrangementPanel.add( new JButton( new SelectRightBordersAction() ) );

    final JPanel borderPanel = new JPanel();
    borderPanel.setLayout( new BorderLayout() );
    borderPanel.setBorder( BorderFactory.createTitledBorder( Messages.getString( "BorderPropertiesPane.Borders" ) ) );
    borderPanel.add( arrangementPanel, BorderLayout.NORTH );
    borderPanel.add( borderEditorPanel, BorderLayout.CENTER );

    final JPanel borderEditorCarrier = new JPanel( new BorderLayout() );
    borderEditorCarrier.add( borderPanel, BorderLayout.CENTER );
    borderEditorCarrier.add( borderCornerCarrier, BorderLayout.SOUTH );

    final JPanel styleListPanel = new JPanel();
    styleListPanel.setLayout( new BorderLayout() );
    styleListPanel.add( new JLabel( Messages.getString( "BorderPropertiesPane.Style" ) ), BorderLayout.NORTH );
    styleListPanel.add( new JScrollPane( styleList ), BorderLayout.CENTER );

    final JPanel styleColorPanel = new JPanel();
    styleColorPanel.setLayout( new GridBagLayout() );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    styleColorPanel.add( new JLabel( Messages.getString( "BorderPropertiesPane.Width" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.weightx = 1;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    styleColorPanel.add( borderWidth, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    styleColorPanel.add( new JLabel( Messages.getString( "BorderPropertiesPane.Color" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    styleColorPanel.add( colorSelectorBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    gbc.fill = GridBagConstraints.VERTICAL;
    styleColorPanel.add( new EllipsisButton( new SelectCustomColorAction( colorSelectorBox ) ), gbc );

    final JPanel linestylePanel = new JPanel();
    linestylePanel.setLayout( new BorderLayout() );
    linestylePanel
      .setBorder( BorderFactory.createTitledBorder( Messages.getString( "BorderPropertiesPane.LineStyle" ) ) );
    linestylePanel.add( styleListPanel, BorderLayout.CENTER );
    linestylePanel.add( styleColorPanel, BorderLayout.SOUTH );

    final JPanel paddingPanel = new JPanel();
    paddingPanel.setLayout( new GridBagLayout() );
    paddingPanel.setBorder( BorderFactory.createTitledBorder( Messages.getString( "BorderPropertiesPane.Paddings" ) ) );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 0 );
    paddingPanel.add( new JLabel( Messages.getString( "BorderPropertiesPane.Left" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    paddingPanel.add( paddingLeft, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 0 );
    paddingPanel.add( new JLabel( Messages.getString( "BorderPropertiesPane.Right" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    paddingPanel.add( paddingRight, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 0 );
    paddingPanel.add( new JLabel( Messages.getString( "BorderPropertiesPane.Top" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    paddingPanel.add( paddingTop, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 5, 0 );
    paddingPanel.add( new JLabel( Messages.getString( "BorderPropertiesPane.Bottom" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    paddingPanel.add( paddingBottom, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 2;
    gbc.weighty = 100;
    gbc.anchor = GridBagConstraints.WEST;
    paddingPanel.add( new JPanel(), gbc );

    final JPanel minSizePanel = new JPanel();
    minSizePanel.setLayout( new GridBagLayout() );
    minSizePanel
      .setBorder( BorderFactory.createTitledBorder( Messages.getString( "BorderPropertiesPane.MinimumSize" ) ) );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 0 );
    minSizePanel.add( new JLabel( Messages.getString( "BorderPropertiesPane.Width" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    minSizePanel.add( minimumWidth, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 5, 0 );
    minSizePanel.add( new JLabel( Messages.getString( "BorderPropertiesPane.Height" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    minSizePanel.add( minimumHeight, gbc );

    final JPanel paddingsAndSizePanel = new JPanel();
    paddingsAndSizePanel.setLayout( new BorderLayout() );
    paddingsAndSizePanel.add( minSizePanel, BorderLayout.NORTH );
    paddingsAndSizePanel.add( paddingPanel, BorderLayout.CENTER );

    setLayout( new GridBagLayout() );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.NORTHEAST;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    add( paddingsAndSizePanel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.weightx = 3;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    add( borderEditorCarrier, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    add( linestylePanel, gbc );
  }


  /**
   * The model's item index matches the AWT-font-style flags.
   *
   * @return
   */
  private DefaultListModel createBorderStyleModel() {
    final DefaultListModel model = new DefaultListModel();
    model.addElement( BorderStyle.SOLID );
    model.addElement( BorderStyle.DASHED );
    model.addElement( BorderStyle.DOT_DASH );
    model.addElement( BorderStyle.DOT_DOT_DASH );
    model.addElement( BorderStyle.DOTTED );
    model.addElement( BorderStyle.HIDDEN );
    model.addElement( BorderStyle.NONE );
    model.addElement( BorderStyle.DOUBLE );
    model.addElement( BorderStyle.GROOVE );
    model.addElement( BorderStyle.INSET );
    model.addElement( BorderStyle.OUTSET );
    model.addElement( BorderStyle.RIDGE );
    model.addElement( BorderStyle.WAVE );
    return model;
  }


  protected void applyBorder( final BorderSelection borderSelection ) {
    final ElementStyleSheet styleSheet = borderEditorPanel.getElementStyleSheet();
    if ( BorderSelection.TOP.equals( borderSelection ) ) {
      final Color color = (Color) colorSelectorBox.getSelectedItem();
      final Number width = (Number) borderWidth.getValue();
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_COLOR, color );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, new Float( width.floatValue() ) );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_STYLE, styleList.getSelectedValue() );
    } else if ( BorderSelection.LEFT.equals( borderSelection ) ) {
      final Color color = (Color) colorSelectorBox.getSelectedItem();
      final Number width = (Number) borderWidth.getValue();
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_LEFT_COLOR, color );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_LEFT_WIDTH, new Float( width.floatValue() ) );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_LEFT_STYLE, styleList.getSelectedValue() );
    } else if ( BorderSelection.BOTTOM.equals( borderSelection ) ) {
      final Color color = (Color) colorSelectorBox.getSelectedItem();
      final Number width = (Number) borderWidth.getValue();
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_COLOR, color );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_WIDTH, new Float( width.floatValue() ) );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_STYLE, styleList.getSelectedValue() );
    } else if ( BorderSelection.RIGHT.equals( borderSelection ) ) {
      final Color color = (Color) colorSelectorBox.getSelectedItem();
      final Number width = (Number) borderWidth.getValue();
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_COLOR, color );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_WIDTH, new Float( width.floatValue() ) );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_STYLE, styleList.getSelectedValue() );
    } else if ( BorderSelection.TOP_LEFT.equals( borderSelection ) ) {
      final Number width = (Number) cornerWidth.getValue();
      final Number height = (Number) cornerHeight.getValue();
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH, new Float( width.floatValue() ) );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT, new Float( height.floatValue() ) );
    } else if ( BorderSelection.TOP_RIGHT.equals( borderSelection ) ) {
      final Number width = (Number) cornerWidth.getValue();
      final Number height = (Number) cornerHeight.getValue();
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH, new Float( width.floatValue() ) );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT, new Float( height.floatValue() ) );
    } else if ( BorderSelection.BOTTOM_LEFT.equals( borderSelection ) ) {
      final Number width = (Number) cornerWidth.getValue();
      final Number height = (Number) cornerHeight.getValue();
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH, new Float( width.floatValue() ) );
      styleSheet
        .setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT, new Float( height.floatValue() ) );
    } else if ( BorderSelection.BOTTOM_RIGHT.equals( borderSelection ) ) {
      final Number width = (Number) cornerWidth.getValue();
      final Number height = (Number) cornerHeight.getValue();
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH, new Float( width.floatValue() ) );
      styleSheet
        .setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT, new Float( height.floatValue() ) );
    }

    borderEditorPanel.repaint();
  }

  public void initializeFromStyle( final ElementStyleSheet styleSheet ) {
    borderEditorPanel.updateElementStyleSheet( styleSheet );
    minimumWidth.setText( printLength( (Number) styleSheet.getStyleProperty( ElementStyleKeys.MIN_WIDTH ) ) );
    minimumHeight.setText( printLength( (Number) styleSheet.getStyleProperty( ElementStyleKeys.MIN_HEIGHT ) ) );

    paddingTop.setValue( styleSheet.getStyleProperty( ElementStyleKeys.PADDING_TOP, 0f ) );
    paddingLeft.setValue( styleSheet.getStyleProperty( ElementStyleKeys.PADDING_LEFT, 0f ) );
    paddingBottom.setValue( styleSheet.getStyleProperty( ElementStyleKeys.PADDING_BOTTOM, 0f ) );
    paddingRight.setValue( styleSheet.getStyleProperty( ElementStyleKeys.PADDING_RIGHT, 0f ) );

  }

  public void commitValues( final ElementStyleSheet styleSheet ) {
    styleSheet.setStyleProperty( ElementStyleKeys.PADDING_TOP, parseLength( (Number) paddingTop.getValue() ) );
    styleSheet.setStyleProperty( ElementStyleKeys.PADDING_LEFT, parseLength( (Number) paddingLeft.getValue() ) );
    styleSheet.setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, parseLength( (Number) paddingBottom.getValue() ) );
    styleSheet.setStyleProperty( ElementStyleKeys.PADDING_RIGHT, parseLength( (Number) paddingRight.getValue() ) );

    styleSheet.setStyleProperty( ElementStyleKeys.MIN_WIDTH, parseLength( minimumWidth.getText() ) );
    styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT, parseLength( minimumHeight.getText() ) );

    borderEditorPanel.commitValues( styleSheet );
  }

  public static String printLength( final Number length ) {
    if ( length == null ) {
      return null;
    }

    if ( length.floatValue() >= 0 ) {
      return String.valueOf( length );
    }
    return -length.floatValue() + "%";
  }

  public static Float parseLength( final Number value ) {
    if ( value instanceof Float ) {
      return (Float) value;
    }
    if ( value != null ) {
      return new Float( value.floatValue() );
    }
    return null;
  }

  public static Float parseLength( final String value ) {
    if ( value == null ) {
      return null;
    }
    try {
      final String tvalue = value.trim();
      //noinspection MagicCharacter
      if ( tvalue.length() > 0 && tvalue.charAt( tvalue.length() - 1 ) == '%' ) {
        final String number = tvalue.substring( 0, tvalue.length() - 1 );
        return Float.parseFloat( number ) * -1.0f;
      } else {
        return Float.parseFloat( tvalue );
      }
    } catch ( NumberFormatException e ) {
      // ignore exception
      return null;
    }
  }

}
