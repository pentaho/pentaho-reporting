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

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;

import java.lang.reflect.InvocationTargetException;

public class AttributeMetaRegistryTest extends TestCase {

  public static final String BUNDLE_LOCATION =
    "org.pentaho.reporting.engine.classic.core.metadata.attributemetadatatest";

  public AttributeMetaRegistryTest() {
  }

  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRegisterAttribute()
    throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException,
    InstantiationException {
    final ElementTypeRegistry elementTypeRegistry = new ElementTypeRegistry();
    for ( ElementMetaData metaData : ElementTypeRegistry.getInstance().getAllElementTypes() ) {
      elementTypeRegistry.registerElement( metaData );
    }

    final DefaultElementMetaData metaData =
      (DefaultElementMetaData) elementTypeRegistry
        .getElementType( LabelType.INSTANCE.getMetaData().getName() );
    final AttributeMetaData attrMeta = metaData.getAttributeDescription( "namespace", "Name" );
    assertNull( attrMeta );

    final AttributeRegistry attributeRegistry =
      elementTypeRegistry.getAttributeRegistry( LabelType.INSTANCE );

    final DefaultAttributeMetaData m =
      new DefaultAttributeMetaData( "namespace", "Name", BUNDLE_LOCATION, "prefix", String.class, false,
        ClassicEngineBoot.computeCurrentVersionId() );
    attributeRegistry.putAttributeDescription( m );

    final AttributeMetaData attributeDescription = metaData.getAttributeDescription( "namespace", "Name" );
    assertEquals( "prefix", attributeDescription.getKeyPrefix() );
    assertEquals( BUNDLE_LOCATION, attributeDescription.getBundleLocation() );

  }
}
