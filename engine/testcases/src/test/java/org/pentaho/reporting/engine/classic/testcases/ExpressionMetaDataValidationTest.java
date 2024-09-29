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
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.testsupport.base.MetaDataValidationTestBase;

import java.util.ArrayList;
import java.util.List;

public class ExpressionMetaDataValidationTest extends MetaDataValidationTestBase<ExpressionMetaData> {
  public ExpressionMetaDataValidationTest() {
  }

  @Test
  public void testMetaData() {
    ExpressionMetaData[] m = ExpressionRegistry.getInstance().getAllExpressionMetaDatas();
    List list = super.performTest( m );
    Assert.assertEquals( new ArrayList(), list );
  }

  protected void performTestOnElement( final ExpressionMetaData metaData ) {
    final String typeName = metaData.getName();
    logger.debug( "Processing " + typeName );

    try {
      final Object type = metaData.create();
    } catch ( Exception e ) {
      Assert.fail( "metadata creation failed" );

    }

    validate( metaData );

    for ( ExpressionPropertyMetaData propertyDescription : metaData.getPropertyDescriptions() ) {
      validate( propertyDescription );
    }
  }
}
