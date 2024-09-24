/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
