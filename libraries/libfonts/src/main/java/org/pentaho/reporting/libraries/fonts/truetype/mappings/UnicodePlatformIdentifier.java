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

package org.pentaho.reporting.libraries.fonts.truetype.mappings;

/**
 * Creation-Date: 06.11.2005, 21:44:21
 *
 * @author Thomas Morgner
 */
public class UnicodePlatformIdentifier extends PlatformIdentifier {
  public UnicodePlatformIdentifier() {
    super( 0 );
  }

  /**
   * According to the Apple OpenType specifications, all Unicode characters must be encoded using UTF-16. Depending on
   * the encodingId, some blocks may be interpreted differently. LibFont ignores that and uses the Java-Default UTF-16
   * mapping.
   * <p/>
   * <a href="http://developer.apple.com/fonts/TTRefMan/RM06/Chap6name.html#ID">Source</a>
   *
   * @param encodingId
   * @param language
   * @return the encoding, always "UTF-16"
   */
  public String getEncoding( final int encodingId, final int language ) {
    return "UTF-16";
  }
}
