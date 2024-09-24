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

package org.pentaho.reporting.designer.layout;

import junit.framework.Assert;
import org.pentaho.reporting.engine.classic.core.testsupport.graphics.TestGraphics2D;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class ValidateTextGraphics extends TestGraphics2D {
  private ArrayList<String> expectedWords;

  public ValidateTextGraphics( final int width, final int height ) {
    super( width, height );
    expectedWords = new ArrayList<String>();
  }

  public void expect( final String word ) {
    expectedWords.add( word );
  }

  public void expect( final String... word ) {
    for ( int i = 0; i < word.length; i++ ) {
      expect( word[ i ] );
    }
  }

  public void expectSentence( final String sentence ) {
    final StringTokenizer strtok = new StringTokenizer( sentence );
    while ( strtok.hasMoreTokens() ) {
      expect( strtok.nextToken() );
    }
  }

  public void drawString( final String str, final float x, final float y ) {
    Assert.assertTrue( "Text " + str + " outside of clipping area", hitClip( (int) x, (int) y, 1, 1 ) );
    if ( !expectedWords.isEmpty() ) {
      Assert.assertEquals( expectedWords.get( 0 ), str );
      expectedWords.remove( 0 );
    }
    super.drawString( str, x, y );
  }

  public boolean isValid() {
    return expectedWords.isEmpty();
  }
}
