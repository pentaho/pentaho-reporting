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
