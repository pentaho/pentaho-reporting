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
