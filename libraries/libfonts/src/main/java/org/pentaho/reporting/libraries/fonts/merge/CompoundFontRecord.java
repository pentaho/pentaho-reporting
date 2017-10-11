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

package org.pentaho.reporting.libraries.fonts.merge;

import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontRecord;

/**
 * Creation-Date: 20.07.2007, 18:55:08
 *
 * @author Thomas Morgner
 */
public class CompoundFontRecord implements FontRecord {
  /*
   * Specifiying the boldSpecified and italicsSpecified is a dirty hack and should be removed pretty soon.  
   */

  private FontRecord base;
  private CompoundFontFamily family;
  private boolean boldSpecified;
  private boolean italicsSpecified;
  private FontIdentifier identifier;


  public CompoundFontRecord( final FontRecord base,
                             final CompoundFontFamily family,
                             final boolean boldSpecified,
                             final boolean italicsSpecified ) {
    this.base = base;
    this.family = family;
    this.boldSpecified = boldSpecified;
    this.italicsSpecified = italicsSpecified;
  }

  public FontRecord getBase() {
    return base;
  }

  public FontFamily getFamily() {
    return family;
  }

  public boolean isBold() {
    return base.isBold();
  }

  public boolean isItalic() {
    return base.isItalic();
  }

  public boolean isOblique() {
    return base.isOblique();
  }

  public FontIdentifier getIdentifier() {
    if ( identifier == null ) {
      identifier = new CompoundFontIdentifier
        ( base.getIdentifier(), family.getRegistry(), boldSpecified, italicsSpecified );
    }
    return identifier;
  }
}
