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
 * Copyright (c) 2000 - 2015 Pentaho Corporation, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.emptyArray;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.ResourceManagerBackend;

public class DefaultAttributeCoreTest {

  private DefaultAttributeCore attrCore = new DefaultAttributeCore();
  private AttributeMetaData metaData;
  private ReportElement element;

  @Before
  public void setUp() {
    metaData = mock( AttributeMetaData.class );
    element = mock( ReportElement.class );
  }

  @Test( expected = NullPointerException.class )
  public void testGetReferencedFieldsWithoutElement() {
    attrCore.getReferencedFields( metaData, null, null );
  }

  @Test
  public void testGetReferencedFields() {
    String[] result = attrCore.getReferencedFields( metaData, element, null );
    assertThat( result, is( emptyArray() ) );

    String[] attributeValue = new String[] { "attr_0" };
    doReturn( "Field" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedFields( metaData, element, attributeValue );
    assertThat( result, is( equalTo( attributeValue ) ) );

    result = attrCore.getReferencedFields( metaData, element, attributeValue[0] );
    assertThat( result, is( equalTo( attributeValue ) ) );

    doReturn( "Message" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedFields( metaData, element, attributeValue[0] );
    assertThat( result, is( emptyArray() ) );

    doReturn( "Formula" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedFields( metaData, element, "namespace:[a+b]" );
    assertThat( result, is( equalTo( new String[] { "a+b" } ) ) );

    doReturn( "Formula" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedFields( metaData, element, "namespace" );
    assertThat( result, is( emptyArray() ) );

    doReturn( "incorrect" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedFields( metaData, element, "namespace:[a+b]" );
    assertThat( result, is( emptyArray() ) );
  }

  @Test( expected = NullPointerException.class )
  public void testGetReferencedGroupsWithoutElement() {
    attrCore.getReferencedGroups( metaData, null, null );
  }

  @Test
  public void testGetReferencedGroups() {
    String[] result = attrCore.getReferencedGroups( metaData, element, null );
    assertThat( result, is( emptyArray() ) );

    String[] attributeValue = new String[] { "attr_0" };
    doReturn( "Group" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedGroups( metaData, element, attributeValue );
    assertThat( result, is( equalTo( attributeValue ) ) );

    result = attrCore.getReferencedGroups( metaData, element, attributeValue[0] );
    assertThat( result, is( equalTo( attributeValue ) ) );

    doReturn( "incorrect" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedGroups( metaData, element, attributeValue[0] );
    assertThat( result, is( emptyArray() ) );
  }

  @Test( expected = NullPointerException.class )
  public void testGetReferencedResourcesWithoutElement() {
    attrCore.getReferencedResources( metaData, null, null, null );
  }

  // @Test( expected = NullPointerException.class )
  // public void testGetReferencedResourcesWithoutReportElement() {
  // attrCore.getReferencedResources( metaData, element, null, null );
  // }

  @Test( expected = NullPointerException.class )
  public void testGetReferencedResourcesWithoutResourceManager() {
    attrCore.getReferencedResources( metaData, element, null, null );
  }

  @Test
  public void testGetReferencedResources() throws ResourceKeyCreationException {
    ResourceManagerBackend resourceManagerBackend = mock( ResourceManagerBackend.class );
    ResourceManager resourceManager = new ResourceManager( resourceManagerBackend );

    ResourceKey contentBase = new ResourceKey( "contentBase_schema", "contentBase_id", null );
    ResourceKey elementSource = new ResourceKey( "elementSource_schema", "elementSource_id", null );
    ResourceKey attributeValue = new ResourceKey( "attributeValue_schema", "attributeValue_id", null );

    doReturn( "Content" ).when( metaData ).getValueRole();
    doReturn( contentBase ).when( element ).getAttribute( AttributeNames.Core.NAMESPACE,
        AttributeNames.Core.CONTENT_BASE );
    doReturn( elementSource ).when( element ).getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SOURCE );
    doReturn( elementSource ).when( resourceManagerBackend ).deriveKey( contentBase, "attr_0", null );
    doReturn( elementSource ).when( resourceManagerBackend ).createKey( "attr_0", null );

    ResourceReference[] result = attrCore.getReferencedResources( metaData, element, resourceManager, attributeValue );
    assertThat( result, is( notNullValue() ) );
    assertThat( result.length, is( equalTo( 1 ) ) );
    assertThat( result[0].getPath(), is( equalTo( attributeValue ) ) );
    assertThat( result[0].isLinked(), is( equalTo( false ) ) );

    result = attrCore.getReferencedResources( metaData, element, resourceManager, "attr_0" );
    assertThat( result, is( notNullValue() ) );
    assertThat( result.length, is( equalTo( 1 ) ) );
    assertThat( result[0].getPath(), is( equalTo( elementSource ) ) );
    assertThat( result[0].isLinked(), is( equalTo( true ) ) );

    doReturn( null ).when( element ).getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.CONTENT_BASE );
    result = attrCore.getReferencedResources( metaData, element, resourceManager, "attr_0" );
    assertThat( result, is( notNullValue() ) );
    assertThat( result.length, is( equalTo( 1 ) ) );
    assertThat( result[0].getPath(), is( equalTo( elementSource ) ) );
    assertThat( result[0].isLinked(), is( equalTo( true ) ) );

    result = attrCore.getReferencedResources( metaData, element, resourceManager, null );
    assertThat( result, is( emptyArray() ) );

    doReturn( "incorrect" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedResources( metaData, element, resourceManager, "attr_0" );
    assertThat( result, is( emptyArray() ) );
  }

  @Test
  public void testGetExtraCalculationFields() {
    assertThat( attrCore.getExtraCalculationFields( null ), is( emptyArray() ) );
  }
}
