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


package org.pentaho.reporting.engine.classic.core.util;

import junit.framework.TestCase;

import java.awt.*;

public class StrokeUtilityTest extends TestCase {
  public StrokeUtilityTest() {
  }

  public StrokeUtilityTest( String string ) {
    super( string );
  }

  public void testClassification() {
    {
      Stroke stroke = StrokeUtility.createStroke( 4, 1 );
      int recognizedType = StrokeUtility.getStrokeType( stroke );
      assertEquals( "Stroke Type", 4, recognizedType );
    }

    for ( int type = 0; type < 5; type += 1 ) {
      Stroke stroke = StrokeUtility.createStroke( type, 1 );
      int recognizedType = StrokeUtility.getStrokeType( stroke );
      assertEquals( "Stroke Type", type, recognizedType );
    }
  }
}
