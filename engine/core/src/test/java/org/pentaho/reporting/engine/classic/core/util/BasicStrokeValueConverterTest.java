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


package org.pentaho.reporting.engine.classic.core.util;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.util.beans.BasicStrokeValueConverter;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;

import java.awt.*;

public class BasicStrokeValueConverterTest extends TestCase {
  public BasicStrokeValueConverterTest() {
  }

  public BasicStrokeValueConverterTest( final String name ) {
    super( name );
  }

  public void testParse() throws BeanException {
    final BasicStroke b = new BasicStroke();
    BasicStrokeValueConverter c = new BasicStrokeValueConverter();
    final BasicStroke b2 = (BasicStroke) c.toPropertyValue( c.toAttributeValue( b ) );
    assertEquals( b, b2 );
  }
}
