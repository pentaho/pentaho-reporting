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
package org.pentaho.plugin.jfreereport.reportcharts;


import org.junit.Assert;
import org.junit.Test;

import java.awt.Color;

public class ColorHelperTest {

	@Test
  public void testGreenColor() {
		Assert.assertEquals( "green", ColorHelper.lookupName( new Color( 0, 255, 0 ) ) );
		Assert.assertEquals(  new Color( 0, 255, 0 ), ColorHelper.lookupColor( "green" ) );
		Assert.assertEquals(  new Color( 0, 255, 0 ), ColorHelper.lookupColor( "lime" ) );
  }
}
