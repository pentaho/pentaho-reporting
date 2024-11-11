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

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;
import org.pentaho.reporting.libraries.fonts.encoding.manual.Utf16LE;

public class GraphemeClusterProducerTest extends TestCase {
  public GraphemeClusterProducerTest() {
  }

  public void testGraphemeClusterGenerationWindows() {
    CodePointBuffer buffer = Utf16LE.getInstance().decodeString( "T\r\n\r\n\r\nT", null ); //$NON-NLS-1$
    final int[] data = buffer.getBuffer();
    final boolean[] result = new boolean[]
      {
        true, true, false, true, false, true, false, true
      };
    GraphemeClusterProducer prod = new GraphemeClusterProducer();

    for ( int i = 0; i < buffer.getLength(); i++ ) {
      final int codepoint = data[ i ];
      if ( prod.createGraphemeCluster( codepoint ) != result[ i ] ) {
        TestCase.fail();
      }
    }
  }

  public void testGraphemeClusterGenerationUnix() {
    CodePointBuffer buffer = Utf16LE.getInstance().decodeString( "T\n\n\nT", null ); //$NON-NLS-1$
    final int[] data = buffer.getBuffer();
    final boolean[] result = new boolean[]
      {
        true, true, true, true, true
      };
    GraphemeClusterProducer prod = new GraphemeClusterProducer();

    for ( int i = 0; i < buffer.getLength(); i++ ) {
      final int codepoint = data[ i ];
      if ( prod.createGraphemeCluster( codepoint ) != result[ i ] ) {
        TestCase.fail();
      }
    }
  }

  public void testGraphemeClusterGenerationOldMac() {
    CodePointBuffer buffer = Utf16LE.getInstance().decodeString( "T\r\r\rT", null ); //$NON-NLS-1$
    final int[] data = buffer.getBuffer();
    final boolean[] result = new boolean[]
      {
        true, true, true, true, true
      };
    GraphemeClusterProducer prod = new GraphemeClusterProducer();

    for ( int i = 0; i < buffer.getLength(); i++ ) {
      final int codepoint = data[ i ];
      if ( prod.createGraphemeCluster( codepoint ) != result[ i ] ) {
        TestCase.fail();
      }
    }
  }
}
