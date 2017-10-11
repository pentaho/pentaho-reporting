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
import org.pentaho.reporting.libraries.fonts.registry.FontRecord;
import org.pentaho.reporting.libraries.fonts.registry.FontRegistry;

/**
 * Creation-Date: 20.07.2007, 18:54:28
 *
 * @author Thomas Morgner
 */
public class CompoundFontFamily implements FontFamily {
  private FontFamily base;
  private FontRegistry registry;

  public CompoundFontFamily( final FontFamily base,
                             final FontRegistry registry ) {
    if ( registry instanceof CompoundFontRegistry ) {
      throw new IllegalStateException();
    }
    this.base = base;
    this.registry = registry;
  }

  public FontRegistry getRegistry() {
    return registry;
  }

  public String getFamilyName() {
    return base.getFamilyName();
  }

  public String[] getAllNames() {
    return base.getAllNames();
  }

  public FontRecord getFontRecord( final boolean bold, final boolean italics ) {
    return new CompoundFontRecord( base.getFontRecord( bold, italics ), this, bold, italics );
  }
}
