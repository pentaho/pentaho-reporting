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


package org.pentaho.reporting.libraries.pixie.wmf;

import java.awt.*;

/**
 * A color implementation, that supports some additional flags defined by the Windows API, but has no Alpha-Channel.
 * This is a BGR color implementation, the flags are stored in the highest byte.
 */
public class GDIColor extends Color {
  private static final long serialVersionUID = 1161533883291249918L;

  public static final int PC_RESERVED = 0x01;
  public static final int PC_EXPLICIT = 0x02;
  public static final int PC_NOCOLLAPSE = 0x04;

  /**
   * The color flags.
   */
  private int flags;

  /**
   * Creates a new GDI color instance by parsing the color reference.
   *
   * @param colorref the integer color reference.
   */
  public GDIColor( final int colorref ) {
    this( getR( colorref ), getG( colorref ), getB( colorref ), getFlags( colorref ) );
  }

  /**
   * Creates a new GDI Color instance.
   *
   * @param r     the red channel.
   * @param g     the green channel.
   * @param b     the blue channel.
   * @param flags the Windows Color flags.
   */
  public GDIColor( final int r, final int g, final int b, final int flags ) {
    super( r, g, b );
    this.flags = flags;
  }

  /**
   * Extracts the RED channel from the given ColorReference.
   *
   * @param ref the color reference.
   * @return the red channel.
   */
  private static int getR( final int ref ) {
    int retval = ( ref & 0x000000ff );
    if ( retval < 0 ) {
      retval = ( retval + 256 );
    }
    return retval;
  }

  /**
   * Extracts the GREEN channel from the given ColorReference.
   *
   * @param ref the color reference.
   * @return the green channel.
   */
  private static int getG( final int ref ) {
    return ( ref & 0x0000ff00 ) >> 8;
  }

  /**
   * Extracts the BLUE channel from the given ColorReference.
   *
   * @param ref the color reference.
   * @return the blue channel.
   */
  private static int getB( final int ref ) {
    return ( ref & 0x00ff0000 ) >> 16;
  }

  /**
   * Extracts the Color Flags from the given ColorReference.
   *
   * @param ref the color reference.
   * @return the color flags.
   */
  private static int getFlags( final int ref ) {
    return ( ref & 0xff000000 ) >> 24;
  }

  /**
   * Returns the PC_RESERVED flag state for this color.
   *
   * @return true, if PC_RESERVED is set, false otherwise.
   */
  public boolean isReserved() {
    return ( this.flags & PC_RESERVED ) == PC_RESERVED;
  }

  /**
   * Returns the PC_EXPLICIT flag state for this color.
   *
   * @return true, if PC_EXPLICIT is set, false otherwise.
   */
  public boolean isExplicit() {
    return ( this.flags & PC_EXPLICIT ) == PC_EXPLICIT;
  }

  /**
   * Returns the PC_NOCOLLAPSE flag state for this color.
   *
   * @return true, if PC_NOCOLLAPSE is set, false otherwise.
   */
  public boolean isNoCollapse() {
    return ( this.flags & PC_NOCOLLAPSE ) == PC_NOCOLLAPSE;
  }

  /**
   * Gets the assigned flag for the color.
   *
   * @return the flags.
   */
  public int getFlags() {
    return flags;
  }

  /**
   * Translates the given color instance into a GDI color reference.
   *
   * @param c the color that should be translated.
   * @return the created color reference.
   */
  public static int translateColor( final Color c ) {
    final int red = c.getRed();
    final int green = c.getGreen();
    final int blue = c.getBlue();
    int flags = 0;

    if ( c instanceof GDIColor ) {
      final GDIColor gc = (GDIColor) c;
      flags = gc.getFlags();
    }

    int retval = flags;
    retval = ( retval << 8 ) + blue;
    retval = ( retval << 8 ) + green;
    retval = ( retval << 8 ) + red;
    return retval;
  }
}
