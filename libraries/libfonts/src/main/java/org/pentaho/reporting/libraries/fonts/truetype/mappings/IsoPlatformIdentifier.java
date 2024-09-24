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
 * Creation-Date: 07.11.2005, 15:27:14
 *
 * @author Thomas Morgner
 */
public class IsoPlatformIdentifier extends PlatformIdentifier {
  public IsoPlatformIdentifier() {
    super( 2 );
  }

  public String getEncoding( final int encodingId, final int language ) {
    if ( encodingId == 0 ) {
      return "US_ASCII";
    } else if ( encodingId == 1 ) {
      return "UTF-16";
    }
    return "ISO-8859-1";
  }
}
