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


package org.pentaho.reporting.engine.classic.core.style;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.layout.style.ManualBreakIndicatorStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;

public class StyleBehaviorTest extends TestCase {

  public StyleBehaviorTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testStylesToArray() {
    final ManualBreakIndicatorStyleSheet mbis = new ManualBreakIndicatorStyleSheet();
    final StyleSheet manualBreakBoxStyle = new SimpleStyleSheet( mbis );

    final int styleCount = ( StyleKey.getDefinedStyleKeyCount() );
    assertTrue( styleCount > 0 );
    final Object[] objects = manualBreakBoxStyle.toArray();
    assertNotNull( objects );
    assertEquals( styleCount, objects.length );
  }

}
