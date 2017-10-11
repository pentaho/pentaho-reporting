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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
