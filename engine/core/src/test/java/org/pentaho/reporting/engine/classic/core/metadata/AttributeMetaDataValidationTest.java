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


package org.pentaho.reporting.engine.classic.core.metadata;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.testsupport.base.MetaDataValidationTestBase;

@SuppressWarnings( "HardCodedStringLiteral" )
public class AttributeMetaDataValidationTest extends MetaDataValidationTestBase<ElementMetaData> {
  private static final Log logger = LogFactory.getLog( AttributeMetaDataValidationTest.class );

  public AttributeMetaDataValidationTest() {
  }

  @Test
  public void testMetaData() {
    final ElementTypeRegistry registry = ElementTypeRegistry.getInstance();
    final ElementMetaData[] elementMetaDatas = registry.getAllElementTypes();
    List<ElementMetaData> failedOnes = super.performTest( elementMetaDatas );
    Assert.assertEquals( new ArrayList<ElementMetaData>(), failedOnes );
  }

  protected void performTestOnElement( final ElementMetaData metaData ) {
    final String typeName = metaData.getName();
    logger.debug( "Processing " + typeName );
    try {
      final Object type = metaData.create();
    } catch ( InstantiationException e ) {
      Assert.fail( "metadata creation failed" );

    }
    validate( metaData );

    for ( StyleMetaData md : metaData.getStyleDescriptions() ) {
      validate( md );
    }

    for ( AttributeMetaData md : metaData.getAttributeDescriptions() ) {
      validate( md );
    }
  }
}
