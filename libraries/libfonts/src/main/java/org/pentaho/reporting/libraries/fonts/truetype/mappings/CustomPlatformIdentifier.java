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
 * Creation-Date: 07.11.2005, 15:42:07
 *
 * @author Thomas Morgner
 */
public class CustomPlatformIdentifier extends PlatformIdentifier {
  public CustomPlatformIdentifier( final int type ) {
    super( type );
  }

  public String getEncoding( final int encodingId, final int language ) {
    return "ISO-8859-1";
  }
}
