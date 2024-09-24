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

package org.pentaho.reporting.engine.classic.testcases;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.testsupport.base.MetaDataValidationTestBase;

import java.util.ArrayList;
import java.util.List;

public class DataFactoryMetaDataValidationTest extends MetaDataValidationTestBase<DataFactoryMetaData> {
  public DataFactoryMetaDataValidationTest() {
  }

  @Test
  public void testMetaData() {
    DataFactoryMetaData[] m = DataFactoryRegistry.getInstance().getAll();
    List list = super.performTest( m );
    Assert.assertEquals( new ArrayList(), list );
  }

  protected void performTestOnElement( final DataFactoryMetaData metaData ) {
    final String typeName = metaData.getName();
    logger.debug( "Processing " + typeName );

    try {
      if ( metaData.isEditable() ) {
        final Object type = metaData.createEditor();
      }
    } catch ( Exception e ) {
      Assert.fail( "metadata creation failed" );
    }

    validate( metaData );
  }
}
