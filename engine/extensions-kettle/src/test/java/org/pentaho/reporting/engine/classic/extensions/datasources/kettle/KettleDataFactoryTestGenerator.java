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


package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

public final class KettleDataFactoryTestGenerator {
  private KettleDataFactoryTestGenerator() {
  }

  public static void main( final String[] args ) throws Exception {
    KettleDataFactoryTest.generate();
  }
}
