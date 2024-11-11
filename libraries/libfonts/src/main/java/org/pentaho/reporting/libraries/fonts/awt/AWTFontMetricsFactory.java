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


package org.pentaho.reporting.libraries.fonts.awt;

import org.pentaho.reporting.libraries.fonts.registry.DefaultFontNativeContext;
import org.pentaho.reporting.libraries.fonts.registry.FontContext;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontMetricsFactory;
import org.pentaho.reporting.libraries.fonts.registry.FontRecord;
import org.pentaho.reporting.libraries.fonts.registry.FontType;

import java.awt.*;

/**
 * Creation-Date: 16.12.2005, 21:14:54
 *
 * @author Thomas Morgner
 */
public class AWTFontMetricsFactory implements FontMetricsFactory {
  public AWTFontMetricsFactory() {
  }

  public FontMetrics createMetrics( final FontIdentifier identifier,
                                    final FontContext context ) {
    if ( FontType.AWT.equals( identifier.getFontType() ) == false ) {
      throw new IllegalArgumentException
        ( "This identifier does not belong to the AWT-font system." );
    }

    // AWT-FontRecords and AWT-FontIdentifiers are implemented in the same class.
    final FontRecord record = (FontRecord) identifier;

    int style = Font.PLAIN;
    if ( record.isBold() ) {
      style |= Font.BOLD;
    }
    if ( record.isItalic() ) {
      style |= Font.ITALIC;
    }
    final Font font = new Font( record.getFamily().getFamilyName(), style, (int) context.getFontSize() );
    return new AWTFontMetrics( new DefaultFontNativeContext( record.isBold(), record.isItalic() ), font, context );
  }
}
