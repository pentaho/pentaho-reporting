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

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd;

public class PentahoMetaDataGenerator {
  public static void main( final String[] args ) throws Exception {
    final PentahoMetaDataTest test = new PentahoMetaDataTest();
    test.setUp();
    test.runGenerate( PentahoMetaDataTest.QUERIES_AND_RESULTS );
    test.runGenerateDesignTime( PentahoMetaDataTest.QUERIES_AND_RESULTS );
    test.runGenerateMultiAgg();

  }

}
