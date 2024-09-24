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
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.EncodingComboBoxModel;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.tools.configeditor.util.VerticalLayout;

import javax.swing.*;
import java.util.Locale;

/**
 * A panel that edits the not so common font properties.
 * <p/>
 * <ul> <li>embed</li> <li>encoding</li> <li>trim-text</li> <li>overflow-x/y</li> <li>reserved-literal</li></ul>
 *
 * @author Thomas Morgner
 */
public class AdvancedFontPropertiesPane extends JPanel {
  private EncodingComboBoxModel encodingModel;
  private JCheckBox embedFontsCheckbox;
  private JCheckBox trimTextCheckbox;
  private JCheckBox overflowXCheckbox;
  private JCheckBox overflowYCheckbox;
  private JTextField quateField;

  public AdvancedFontPropertiesPane() {
    encodingModel = EncodingComboBoxModel.createDefaultModel( Locale.getDefault(), true );
    embedFontsCheckbox = new JCheckBox( Messages.getString( "AdvancedFontPropertiesPane.EmbedFonts" ) );
    trimTextCheckbox = new JCheckBox( Messages.getString( "AdvancedFontPropertiesPane.TrimText" ) );
    overflowXCheckbox = new JCheckBox( Messages.getString( "AdvancedFontPropertiesPane.OverflowX" ) );
    overflowYCheckbox = new JCheckBox( Messages.getString( "AdvancedFontPropertiesPane.OverflowY" ) );
    quateField = new JTextField();
    quateField.setColumns( 30 );

    setLayout( new VerticalLayout() );

    final JPanel encodingCarrier = new JPanel();
    encodingCarrier.setLayout( new BoxLayout( encodingCarrier, BoxLayout.X_AXIS ) );
    encodingCarrier.add( new JLabel( Messages.getString( "AdvancedFontPropertiesPane.Encoding" ) ) );
    encodingCarrier.add( new JComboBox( encodingModel ) );

    final JPanel pdfPanel = new JPanel();
    pdfPanel.setLayout( new VerticalLayout() );
    pdfPanel
      .setBorder( BorderFactory.createTitledBorder( Messages.getString( "AdvancedFontPropertiesPane.PDFSettings" ) ) );
    pdfPanel.add( embedFontsCheckbox );
    pdfPanel.add( encodingCarrier );

    final JPanel quateCarrier = new JPanel();
    quateCarrier.setLayout( new BoxLayout( quateCarrier, BoxLayout.X_AXIS ) );
    quateCarrier.add( new JLabel( Messages.getString( "AdvancedFontPropertiesPane.Quate" ) ) );
    quateCarrier.add( quateField );

    final JPanel textProcessingPanel = new JPanel();
    textProcessingPanel.setLayout( new VerticalLayout() );
    textProcessingPanel.setBorder(
      BorderFactory.createTitledBorder( Messages.getString( "AdvancedFontPropertiesPane.TextProcessingSettings" ) ) );
    textProcessingPanel.add( trimTextCheckbox );
    textProcessingPanel.add( overflowXCheckbox );
    textProcessingPanel.add( overflowYCheckbox );
    textProcessingPanel.add( quateCarrier );

    add( pdfPanel );
    add( textProcessingPanel );
  }

  public void initializeFromStyle( final ElementStyleSheet style ) {
    encodingModel.setSelectedEncoding( (String) style.getStyleProperty( TextStyleKeys.FONTENCODING ) );
    embedFontsCheckbox.setSelected( style.getBooleanStyleProperty( TextStyleKeys.EMBEDDED_FONT ) );
    trimTextCheckbox.setSelected( style.getBooleanStyleProperty( TextStyleKeys.TRIM_TEXT_CONTENT ) );
    overflowXCheckbox.setSelected( style.getBooleanStyleProperty( ElementStyleKeys.OVERFLOW_X ) );
    overflowYCheckbox.setSelected( style.getBooleanStyleProperty( ElementStyleKeys.OVERFLOW_Y ) );
    quateField.setText( (String) style.getStyleProperty( TextStyleKeys.RESERVED_LITERAL ) );
  }

  public void commitValues( final ElementStyleSheet styleSheet ) {
    styleSheet.setStyleProperty( TextStyleKeys.FONTENCODING, encodingModel.getSelectedEncoding() );
    styleSheet.setBooleanStyleProperty( TextStyleKeys.EMBEDDED_FONT, embedFontsCheckbox.isSelected() );
    styleSheet.setBooleanStyleProperty( TextStyleKeys.TRIM_TEXT_CONTENT, trimTextCheckbox.isSelected() );
    styleSheet.setBooleanStyleProperty( ElementStyleKeys.OVERFLOW_X, overflowXCheckbox.isSelected() );
    styleSheet.setBooleanStyleProperty( ElementStyleKeys.OVERFLOW_Y, overflowYCheckbox.isSelected() );
    styleSheet.setStyleProperty( TextStyleKeys.RESERVED_LITERAL, quateField.getText() );
  }
}
