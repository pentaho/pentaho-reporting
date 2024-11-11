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
 * Creation-Date: 07.11.2005, 15:29:42
 *
 * @author Thomas Morgner
 */
public class MicrosoftPlatformIdentifier extends PlatformIdentifier {
  public MicrosoftPlatformIdentifier() {
    super( 3 );
  }

  /**
   * For now, copy the assumption of iText: Always assume UFT-16.
   *
   * @param encodingId
   * @param language
   * @return
   */
  public String getEncoding( final int encodingId, final int language ) {
    return "UTF-16";
  }
}
