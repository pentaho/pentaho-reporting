/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2024 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
