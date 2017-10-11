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

package org.pentaho.reporting.libraries.fonts;

import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * Creation-Date: 22.07.2007, 18:25:35
 *
 * @author Thomas Morgner
 */
public class FontMappingUtility {

  private FontMappingUtility() {
  }

  /**
   * Returns true if the logical font name is equivalent to 'SansSerif', and false otherwise.
   *
   * @return true or false.
   */
  public static boolean isSansSerif( final String fontName ) {
    return StringUtils.startsWithIgnoreCase( fontName, "SansSerif" )
      || StringUtils.startsWithIgnoreCase( fontName, "Dialog" )
      || StringUtils.startsWithIgnoreCase( fontName, "SanSerif" );
    // is it a bug? Somewhere in the JDK this name is used (typo, but heck, we accept it anyway).
  }

  /**
   * Returns true if the logical font name is equivalent to 'Courier', and false otherwise.
   *
   * @return true or false.
   */
  public static boolean isCourier( final String fontName ) {
    return ( StringUtils.startsWithIgnoreCase( fontName, "dialoginput" )
      || StringUtils.startsWithIgnoreCase( fontName, "monospaced" ) );
  }

  /**
   * Returns true if the logical font name is equivalent to 'Serif', and false otherwise.
   *
   * @return true or false.
   */
  public static boolean isSerif( final String fontName ) {
    return ( StringUtils.startsWithIgnoreCase( fontName, "serif" ) );
  }

  public static boolean isSymbol( final String fontName ) {
    return ( StringUtils.startsWithIgnoreCase( fontName, "symbol" ) );
  }
}
