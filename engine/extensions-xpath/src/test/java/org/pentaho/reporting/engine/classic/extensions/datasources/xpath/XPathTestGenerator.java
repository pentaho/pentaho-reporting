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



package org.pentaho.reporting.engine.classic.extensions.datasources.xpath;

public final class XPathTestGenerator {
  private XPathTestGenerator() {
  }

  public static void main( final String[] args ) throws Exception {
    NGXPathQueryTest.main( args );
    XPathQueryTest.main( args );
  }
}
