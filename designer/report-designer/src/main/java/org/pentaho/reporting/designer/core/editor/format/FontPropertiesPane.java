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
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.FontSmooth;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.designtime.swing.BasicFontPropertiesPane;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;

import javax.swing.*;
import java.awt.*;

/**
 * A panel that edits the basic font properties.
 * <p/>
 * <ul> <li>Font-Family</li> <li>Font-Style (bold, italics)</li> <li>Font-Size</li> <li>underline</li>
 * <li>strikethrough</li> <li>aliasing</li></ul>
 *
 * @author Thomas Morgner
 */
public class FontPropertiesPane extends BasicFontPropertiesPane {
  private KeyedComboBoxModel fontSmoothModel;
  private FontPreviewPane previewPane;

  public FontPropertiesPane() {
    setLayout( new GridBagLayout() );

    fontSmoothModel = createFontSmoothModel();
    previewPane = new FontPreviewPane();

    init();
  }

  protected JComponent createPreviewPane() {
    return previewPane;
  }

  protected Component createAliasPanel() {
    final JPanel panel = new JPanel();
    panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS ) );
    panel.add( new JLabel( Messages.getString( "FontPropertiesPane.AntiAliasing" ) ) );
    panel.add( new JComboBox( fontSmoothModel ) );
    return panel;
  }

  private KeyedComboBoxModel createFontSmoothModel() {
    final KeyedComboBoxModel model = new KeyedComboBoxModel();
    model.add( FontSmooth.NEVER, Messages.getString( "FontPropertiesPane.Never" ) );
    model.add( FontSmooth.AUTO, Messages.getString( "FontPropertiesPane.Automatic" ) );
    model.add( FontSmooth.ALWAYS, Messages.getString( "FontPropertiesPane.Always" ) );
    return model;
  }

  public void initializeFromStyle( final ElementStyleSheet style ) {
    setFontFamily( (String) style.getStyleProperty( TextStyleKeys.FONT ) );
    setFontSize( style.getIntStyleProperty( TextStyleKeys.FONTSIZE, 10 ) );

    int computedStyle = 0;
    if ( style.getBooleanStyleProperty( TextStyleKeys.BOLD ) ) {
      computedStyle |= Font.BOLD;
    }
    if ( style.getBooleanStyleProperty( TextStyleKeys.ITALIC ) ) {
      computedStyle |= Font.ITALIC;
    }
    setFontStyle( computedStyle );

    setUnderlined( style.getBooleanStyleProperty( TextStyleKeys.UNDERLINED ) );
    setStrikeThrough( style.getBooleanStyleProperty( TextStyleKeys.STRIKETHROUGH ) );
    fontSmoothModel.setSelectedKey( style.getStyleProperty( TextStyleKeys.FONT_SMOOTH ) );
  }

  public void commitValues( final ElementStyleSheet styleSheet ) {
    styleSheet.setStyleProperty( TextStyleKeys.FONT, getFontFamily() );
    try {
      styleSheet.setStyleProperty( TextStyleKeys.FONTSIZE, Integer.valueOf( getFontSize() ) );
    } catch ( NumberFormatException nfe ) {
      // ignore ..
    }
    final int style = getFontStyle();
    styleSheet.setStyleProperty( TextStyleKeys.BOLD, ( style & Font.BOLD ) == Font.BOLD );
    styleSheet.setStyleProperty( TextStyleKeys.ITALIC, ( style & Font.ITALIC ) == Font.ITALIC );
    styleSheet.setBooleanStyleProperty( TextStyleKeys.UNDERLINED, isUnderlined() );
    styleSheet.setBooleanStyleProperty( TextStyleKeys.STRIKETHROUGH, isStrikeThrough() );
    styleSheet.setStyleProperty( TextStyleKeys.FONT_SMOOTH, fontSmoothModel.getSelectedKey() );
  }
}
