/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
