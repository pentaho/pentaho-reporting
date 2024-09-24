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

package org.pentaho.reporting.engine.classic.core.function;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ItemBand;

public class FunctionUtilsTest extends TestCase {
  public FunctionUtilsTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testFindElement() {
    final Band noLate = new Band();
    noLate.setName( "noLate" );

    final Band landScape = new Band();
    landScape.setName( "landscape" );
    landScape.addElement( noLate );

    final ItemBand band = new ItemBand();
    band.addElement( landScape );

    assertEquals( noLate, FunctionUtilities.findElement( band, "noLate" ) );
  }
}
