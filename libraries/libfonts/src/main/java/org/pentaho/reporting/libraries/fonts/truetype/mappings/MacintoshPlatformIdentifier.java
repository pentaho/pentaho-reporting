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
 * Creation-Date: 07.11.2005, 15:11:56
 *
 * @author Thomas Morgner
 */
public class MacintoshPlatformIdentifier extends PlatformIdentifier {
  private static final String[] ENCODINGS = {
    "MacRoman", // Roman
    null,       // Japan
    null,       // Traditional Chinese
    null,       // Korean
    "MacArabic",// Arabic
    "MacHebrew",// Hebrew
    "MacGreek", // Greek
    "MacCyrillic", // Russian
    null,       // RSymbol
    null,       // Devanagari
    null,       // Gurmukhi
    null,       // Gujarati
    null,       // Oriya
    null,       // Bengali
    null,       // Tamil
    null,       // Telugu
    null,       // Kannada
    null,       // Malayalam
    null,       // Sinhalese
    null,       // Burmese
    null,       // Khmer
    null,       // Thai
    null,       // Laotian
    null,       // Georgian
    null,       // Armenian
    null,       // Simplified Chinese
    null,       // Tibetan
    null,       // Mongolian
    null,       // Geez
    null,       // Slavic
    null,       // Vietnamese
    null,       // Sindhi
  };


  public MacintoshPlatformIdentifier() {
    super( 1 );
  }

  public String getEncoding( final int encodingId, final int language ) {
    if ( encodingId < ENCODINGS.length ) {
      final String encoding = ENCODINGS[ encodingId ];
      if ( encoding != null ) {
        return encoding;
      }
    }
    return "MacRoman";
  }
}
