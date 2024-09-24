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

package org.pentaho.reporting.libraries.fonts.text;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.fonts.tools.ByteTable;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Creation-Date: 11.06.2006, 17:11:16
 *
 * @author Thomas Morgner
 */
public final class GraphemeClassifier {
  private static final Log logger = LogFactory.getLog( GraphemeClassifier.class );

  public static final int OTHER = 0;

  public static final int CR = 0x01;
  public static final int LF = 0x02;
  public static final int CONTROL = 0x03;

  public static final int EXTEND = 4;

  public static final int L = 0x08; // 0000 0000 1000;
  public static final int LV = 0x18; // 0000 0001 1000;
  public static final int V = 0x38; // 0000 0011 1000;
  public static final int T = 0x68; // 0000 0110 1000;
  public static final int LVT = 0x48; // 0000 0100 1000;

  public static final int ANY_HANGUL_MASK = 0x8;
  public static final int V_OR_LV_MASK = 0x18;
  public static final int V_OR_T_MASK = 0x28;
  public static final int LVT_OR_T_MASK = 0x48;

  private ByteTable classificationData;
  private static GraphemeClassifier classifier;

  private GraphemeClassifier() {
    InputStream in = getClass()
      .getResourceAsStream( "/org/pentaho/reporting/libraries/fonts/text/generated/grapheme-classification.ser" );
    if ( in != null ) {
      try {
        final ObjectInputStream oin = new ObjectInputStream( in );
        classificationData = (ByteTable) oin.readObject();
        oin.close();
        in = null;
      } catch ( Exception e ) {
        logger.warn( "Unable to load the pre-generated classification data.", e );
      } finally {
        if ( in != null ) {
          try {
            in.close();
          } catch ( IOException e ) {
            // ignore ..
          }
        }
      }
    }
  }

  public int getGraphemeClassification( final int codePoint ) {
    if ( classificationData != null ) {
      final int row = codePoint >> 8;
      final int col = codePoint & 0xFF;
      return classificationData.getByte( row, col, (byte) OTHER );
    }
    if ( codePoint == 0x0D ) {
      return CR;
    }
    if ( codePoint == 0x0A ) {
      return LF;
    }
    return OTHER;
  }

  public static synchronized GraphemeClassifier getClassifier() {
    if ( classifier == null ) {
      classifier = new GraphemeClassifier();
    }
    return classifier;
  }
}
