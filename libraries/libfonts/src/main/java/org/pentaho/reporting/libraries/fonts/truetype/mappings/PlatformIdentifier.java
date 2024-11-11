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


package org.pentaho.reporting.libraries.fonts.truetype.mappings;

/**
 * Creation-Date: 06.11.2005, 21:33:16
 *
 * @author Thomas Morgner
 */
public abstract class PlatformIdentifier {
  public static final PlatformIdentifier UNICODE =
    new UnicodePlatformIdentifier();
  public static final PlatformIdentifier MACINTOSH =
    new MacintoshPlatformIdentifier();
  public static final PlatformIdentifier ISO =
    new IsoPlatformIdentifier();
  public static final PlatformIdentifier MICROSOFT =
    new MicrosoftPlatformIdentifier();

  private final int type; // for debug only

  protected PlatformIdentifier( final int type ) {
    this.type = type;
  }

  public int getType() {
    return type;
  }

  public String toString() {
    switch( type ) {
      case 0:
        return "Unicode";
      case 1:
        return "Macintosh";
      case 2:
        return "ISO (deprecated)";
      case 3:
        return "Microsoft";
      default:
        return "Custom";
    }
  }

  public static PlatformIdentifier getIdentifier( final int param ) {

    switch( param ) {
      case 0:
        return UNICODE;
      case 1:
        return MACINTOSH;
      case 2:
        return ISO;
      case 3:
        return MICROSOFT;
      default:
        return new CustomPlatformIdentifier( param );
    }
  }

  /**
   * Quoted from the OpenTypeSpecs:
   * <p/>
   * Note that OS/2 and Windows both require that all name strings be defined in Unicode. Thus all 'name' table strings
   * for platform ID = 3 (Microsoft) will require two bytes per character. Macintosh fonts require single byte strings.
   *
   * @param encodingId
   * @param language
   * @return
   */
  public abstract String getEncoding( int encodingId, int language );
}
