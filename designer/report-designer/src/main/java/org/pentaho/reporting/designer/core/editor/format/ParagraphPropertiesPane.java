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
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;
import org.pentaho.reporting.tools.configeditor.util.VerticalLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ParagraphPropertiesPane extends JPanel {
  private JRadioButton leftAlignButton;
  private JRadioButton centerAlignButton;
  private JRadioButton rightAlignButton;

  private JRadioButton topAlignButton;
  private JRadioButton middleAlignButton;
  private JRadioButton bottomAlignButton;
  private JTextField lineHeightField;
  private JCheckBox wrapTextCheckbox;

  private KeyedComboBoxModel whitespaceModel;
  private KeyedComboBoxModel textAlignModel;

  public ParagraphPropertiesPane() {
    setLayout( new VerticalLayout() );

    leftAlignButton = new JRadioButton( Messages.getString( "ParagraphPropertiesPane.Left" ) );
    centerAlignButton = new JRadioButton( Messages.getString( "ParagraphPropertiesPane.Center" ) );
    rightAlignButton = new JRadioButton( Messages.getString( "ParagraphPropertiesPane.Right" ) );
    final ButtonGroup halignGroup = new ButtonGroup();
    halignGroup.add( leftAlignButton );
    halignGroup.add( centerAlignButton );
    halignGroup.add( rightAlignButton );

    topAlignButton = new JRadioButton( Messages.getString( "ParagraphPropertiesPane.Top" ) );
    middleAlignButton = new JRadioButton( Messages.getString( "ParagraphPropertiesPane.Middle" ) );
    bottomAlignButton = new JRadioButton( Messages.getString( "ParagraphPropertiesPane.Bottom" ) );
    final ButtonGroup valignGroup = new ButtonGroup();
    valignGroup.add( topAlignButton );
    valignGroup.add( middleAlignButton );
    valignGroup.add( bottomAlignButton );

    textAlignModel = createTextAlignmentModel();
    whitespaceModel = createWhitspaceModel();

    final JComboBox textAlignmentComboBox = new JComboBox( textAlignModel );
    wrapTextCheckbox = new JCheckBox( Messages.getString( "ParagraphPropertiesPane.AllowTextWrapping" ) );
    lineHeightField = new JTextField();
    lineHeightField.setColumns( 10 );
    final JComboBox whitespaceComboBox = new JComboBox( whitespaceModel );

    final JPanel halignPanel = new JPanel();
    halignPanel.setLayout( new VerticalLayout() );
    halignPanel.setBorder(
      BorderFactory.createTitledBorder( Messages.getString( "ParagraphPropertiesPane.HorizontalAlignment" ) ) );
    halignPanel.add( leftAlignButton );
    halignPanel.add( centerAlignButton );
    halignPanel.add( rightAlignButton );

    final JPanel valignPanel = new JPanel();
    valignPanel.setLayout( new VerticalLayout() );
    valignPanel.setBorder(
      BorderFactory.createTitledBorder( Messages.getString( "ParagraphPropertiesPane.VerticalAlignment" ) ) );
    valignPanel.add( topAlignButton );
    valignPanel.add( middleAlignButton );
    valignPanel.add( bottomAlignButton );

    final JPanel textToTextPanel = new JPanel();
    textToTextPanel.setLayout( new GridBagLayout() );
    textToTextPanel.setBorder(
      BorderFactory.createTitledBorder( Messages.getString( "ParagraphPropertiesPane.TextToTextAlignment" ) ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    textToTextPanel.add( new JLabel( Messages.getString( "ParagraphPropertiesPane.Alignment" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    textToTextPanel.add( textAlignmentComboBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    textToTextPanel.add( new JLabel( Messages.getString( "ParagraphPropertiesPane.LineHeight" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    textToTextPanel.add( lineHeightField, gbc );

    final JPanel whitespacePanel = new JPanel();
    whitespacePanel.setLayout( new GridBagLayout() );
    whitespacePanel.setBorder(
      BorderFactory.createTitledBorder( Messages.getString( "ParagraphPropertiesPane.WhitespaceProcessing" ) ) );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    whitespacePanel.add( wrapTextCheckbox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    whitespacePanel.add( new JLabel( Messages.getString( "ParagraphPropertiesPane.WhitespaceProcessing" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    whitespacePanel.add( whitespaceComboBox, gbc );


    add( halignPanel );
    add( valignPanel );
    add( textToTextPanel );
    add( whitespacePanel );
  }

  private KeyedComboBoxModel createTextAlignmentModel() {
    final KeyedComboBoxModel model = new KeyedComboBoxModel();
    model.add( VerticalTextAlign.TOP, Messages.getString( "ParagraphPropertiesPane.Top" ) );
    model.add( VerticalTextAlign.CENTRAL, Messages.getString( "ParagraphPropertiesPane.Central" ) );
    model.add( VerticalTextAlign.MIDDLE, Messages.getString( "ParagraphPropertiesPane.Middle" ) );
    model.add( VerticalTextAlign.BASELINE, Messages.getString( "ParagraphPropertiesPane.Baseline" ) );
    model.add( VerticalTextAlign.USE_SCRIPT, Messages.getString( "ParagraphPropertiesPane.UseScript" ) );
    model.add( VerticalTextAlign.TEXT_TOP, Messages.getString( "ParagraphPropertiesPane.TextTop" ) );
    model.add( VerticalTextAlign.TEXT_BOTTOM, Messages.getString( "ParagraphPropertiesPane.TextBottom" ) );
    model.add( VerticalTextAlign.SUB, Messages.getString( "ParagraphPropertiesPane.Subscript" ) );
    model.add( VerticalTextAlign.SUPER, Messages.getString( "ParagraphPropertiesPane.Superscript" ) );
    model.add( VerticalTextAlign.BOTTOM, Messages.getString( "ParagraphPropertiesPane.Bottom" ) );
    return model;
  }

  private KeyedComboBoxModel createWhitspaceModel() {
    final KeyedComboBoxModel model = new KeyedComboBoxModel();
    model.add( WhitespaceCollapse.COLLAPSE, Messages.getString( "ParagraphPropertiesPane.CollapseWhitespaces" ) );
    model.add( WhitespaceCollapse.DISCARD, Messages.getString( "ParagraphPropertiesPane.DiscardAllWhitespaces" ) );
    model.add( WhitespaceCollapse.PRESERVE, Messages.getString( "ParagraphPropertiesPane.PreserveAllWhitespaces" ) );
    model.add( WhitespaceCollapse.PRESERVE_BREAKS, Messages.getString( "ParagraphPropertiesPane.PreserveBreaks" ) );
    return model;
  }

  public void initializeFromStyle( final ElementStyleSheet styleSheet ) {
    final ElementAlignment halign = (ElementAlignment) styleSheet.getStyleProperty( ElementStyleKeys.ALIGNMENT );
    if ( ElementAlignment.LEFT.equals( halign ) ) {
      leftAlignButton.setSelected( true );
    } else if ( ElementAlignment.CENTER.equals( halign ) ) {
      centerAlignButton.setSelected( true );
    } else {
      rightAlignButton.setSelected( true );
    }

    final ElementAlignment valign = (ElementAlignment) styleSheet.getStyleProperty( ElementStyleKeys.VALIGNMENT );
    if ( ElementAlignment.BOTTOM.equals( valign ) ) {
      bottomAlignButton.setSelected( true );
    } else if ( ElementAlignment.MIDDLE.equals( valign ) ) {
      middleAlignButton.setSelected( true );
    } else {
      topAlignButton.setSelected( true );
    }

    wrapTextCheckbox.setSelected( TextWrap.WRAP.equals( styleSheet.getStyleProperty( TextStyleKeys.TEXT_WRAP ) ) );
    lineHeightField.setText( BorderPropertiesPane.printLength( (Number) styleSheet.getStyleProperty(
      TextStyleKeys.LINEHEIGHT ) ) );

    textAlignModel.setSelectedKey( styleSheet.getStyleProperty( TextStyleKeys.VERTICAL_TEXT_ALIGNMENT ) );
    whitespaceModel.setSelectedKey( styleSheet.getStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE ) );
  }

  public void commitValues( final ElementStyleSheet styleSheet ) {
    if ( centerAlignButton.isSelected() ) {
      styleSheet.setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.CENTER );
    } else if ( rightAlignButton.isSelected() ) {
      styleSheet.setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.RIGHT );
    } else {
      styleSheet.setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.LEFT );
    }

    if ( middleAlignButton.isSelected() ) {
      styleSheet.setStyleProperty( ElementStyleKeys.VALIGNMENT, ElementAlignment.MIDDLE );
    } else if ( bottomAlignButton.isSelected() ) {
      styleSheet.setStyleProperty( ElementStyleKeys.VALIGNMENT, ElementAlignment.BOTTOM );
    } else {
      styleSheet.setStyleProperty( ElementStyleKeys.VALIGNMENT, ElementAlignment.TOP );
    }

    styleSheet.setStyleProperty( TextStyleKeys.VERTICAL_TEXT_ALIGNMENT, textAlignModel.getSelectedKey() );
    styleSheet
      .setStyleProperty( TextStyleKeys.LINEHEIGHT, BorderPropertiesPane.parseLength( lineHeightField.getText() ) );
    if ( wrapTextCheckbox.isSelected() ) {
      styleSheet.setStyleProperty( TextStyleKeys.TEXT_WRAP, TextWrap.WRAP );
    } else {
      styleSheet.setStyleProperty( TextStyleKeys.TEXT_WRAP, TextWrap.NONE );
    }

    styleSheet.setStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE, whitespaceModel.getSelectedKey() );
  }
}
