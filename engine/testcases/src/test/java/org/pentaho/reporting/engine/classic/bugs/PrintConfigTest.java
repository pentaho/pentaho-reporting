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


package org.pentaho.reporting.engine.classic.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class PrintConfigTest extends TestCase {
  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testEnvironment() throws Exception {
    final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
    final Iterator<String> propertyKeys = config.findPropertyKeys( "" );
    final ArrayList<String> keys = new ArrayList<String>();
    while ( propertyKeys.hasNext() ) {
      keys.add( propertyKeys.next() );
    }
    Collections.sort( keys );
    for ( final String key : keys ) {
      System.out.println( key + "=" + config.getConfigProperty( key ) );
    }
  }
}
