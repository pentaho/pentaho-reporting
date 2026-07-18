/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.xmlns.writer;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;

public class DefaultTagDescriptionTest extends TestCase {
  public DefaultTagDescriptionTest() {
  }

  public DefaultTagDescriptionTest( final String s ) {
    super( s );
  }

  public void testSillyTag() {
    final DefaultTagDescription dt = new DefaultTagDescription( new DefaultConfiguration(), "silly-prefix" );
    assertTrue( dt.hasCData( "basas", "adsda" ) );
  }
}
