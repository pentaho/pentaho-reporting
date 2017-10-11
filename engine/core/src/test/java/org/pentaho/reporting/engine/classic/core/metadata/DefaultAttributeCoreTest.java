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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata;


import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
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
    metaData = Mockito.mock( AttributeMetaData.class );
    element = Mockito.mock( ReportElement.class );
  }

  @Test( expected = NullPointerException.class )
  public void testGetReferencedFieldsWithoutElement() {
    attrCore.getReferencedFields( metaData, null, null );
  }

  @Test
  public void testGetReferencedFields() {
    String[] result = attrCore.getReferencedFields( metaData, element, null );
    Assert.assertThat( result, CoreMatchers.is( Matchers.emptyArray() ) );

    String[] attributeValue = new String[] { "attr_0" };
    Mockito.doReturn( "Field" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedFields( metaData, element, attributeValue );
    Assert.assertThat( result, CoreMatchers.is( CoreMatchers.equalTo( attributeValue ) ) );

    result = attrCore.getReferencedFields( metaData, element, attributeValue[ 0 ] );
    Assert.assertThat( result, CoreMatchers.is( CoreMatchers.equalTo( attributeValue ) ) );

    Mockito.doReturn( "Message" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedFields( metaData, element, attributeValue[ 0 ] );
    Assert.assertThat( result, CoreMatchers.is( Matchers.emptyArray() ) );

    Mockito.doReturn( "Formula" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedFields( metaData, element, "namespace:[a+b]" );
    Assert.assertThat( result, CoreMatchers.is( CoreMatchers.equalTo( new String[] { "a+b" } ) ) );

    Mockito.doReturn( "Formula" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedFields( metaData, element, "namespace" );
    Assert.assertThat( result, CoreMatchers.is( Matchers.emptyArray() ) );

    Mockito.doReturn( "incorrect" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedFields( metaData, element, "namespace:[a+b]" );
    Assert.assertThat( result, CoreMatchers.is( Matchers.emptyArray() ) );
  }

  @Test( expected = NullPointerException.class )
  public void testGetReferencedGroupsWithoutElement() {
    attrCore.getReferencedGroups( metaData, null, null );
  }

  @Test
  public void testGetReferencedGroups() {
    String[] result = attrCore.getReferencedGroups( metaData, element, null );
    Assert.assertThat( result, CoreMatchers.is( Matchers.emptyArray() ) );

    String[] attributeValue = new String[] { "attr_0" };
    Mockito.doReturn( "Group" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedGroups( metaData, element, attributeValue );
    Assert.assertThat( result, CoreMatchers.is( CoreMatchers.equalTo( attributeValue ) ) );

    result = attrCore.getReferencedGroups( metaData, element, attributeValue[ 0 ] );
    Assert.assertThat( result, CoreMatchers.is( CoreMatchers.equalTo( attributeValue ) ) );

    Mockito.doReturn( "incorrect" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedGroups( metaData, element, attributeValue[ 0 ] );
    Assert.assertThat( result, CoreMatchers.is( Matchers.emptyArray() ) );
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
    ResourceManagerBackend resourceManagerBackend = Mockito.mock( ResourceManagerBackend.class );
    ResourceManager resourceManager = new ResourceManager( resourceManagerBackend );

    ResourceKey contentBase = new ResourceKey( "contentBase_schema", "contentBase_id", null );
    ResourceKey elementSource = new ResourceKey( "elementSource_schema", "elementSource_id", null );
    ResourceKey attributeValue = new ResourceKey( "attributeValue_schema", "attributeValue_id", null );

    Mockito.doReturn( "Content" ).when( metaData ).getValueRole();
    Mockito.doReturn( contentBase ).when( element ).getAttribute( AttributeNames.Core.NAMESPACE,
      AttributeNames.Core.CONTENT_BASE );
    Mockito.doReturn( elementSource ).when( element )
      .getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SOURCE );
    Mockito.doReturn( elementSource ).when( resourceManagerBackend ).deriveKey( contentBase, "attr_0", null );
    Mockito.doReturn( elementSource ).when( resourceManagerBackend ).createKey( "attr_0", null );

    ResourceReference[] result = attrCore.getReferencedResources( metaData, element, resourceManager, attributeValue );
    Assert.assertThat( result, CoreMatchers.is( CoreMatchers.notNullValue() ) );
    Assert.assertThat( result.length, CoreMatchers.is( CoreMatchers.equalTo( 1 ) ) );
    Assert.assertThat( result[ 0 ].getPath(), CoreMatchers.is( CoreMatchers.equalTo( attributeValue ) ) );
    Assert.assertThat( result[ 0 ].isLinked(), CoreMatchers.is( CoreMatchers.equalTo( false ) ) );

    result = attrCore.getReferencedResources( metaData, element, resourceManager, "attr_0" );
    Assert.assertThat( result, CoreMatchers.is( CoreMatchers.notNullValue() ) );
    Assert.assertThat( result.length, CoreMatchers.is( CoreMatchers.equalTo( 1 ) ) );
    Assert.assertThat( result[ 0 ].getPath(), CoreMatchers.is( CoreMatchers.equalTo( elementSource ) ) );
    Assert.assertThat( result[ 0 ].isLinked(), CoreMatchers.is( CoreMatchers.equalTo( true ) ) );

    Mockito.doReturn( null ).when( element )
      .getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.CONTENT_BASE );
    result = attrCore.getReferencedResources( metaData, element, resourceManager, "attr_0" );
    Assert.assertThat( result, CoreMatchers.is( CoreMatchers.notNullValue() ) );
    Assert.assertThat( result.length, CoreMatchers.is( CoreMatchers.equalTo( 1 ) ) );
    Assert.assertThat( result[ 0 ].getPath(), CoreMatchers.is( CoreMatchers.equalTo( elementSource ) ) );
    Assert.assertThat( result[ 0 ].isLinked(), CoreMatchers.is( CoreMatchers.equalTo( true ) ) );

    result = attrCore.getReferencedResources( metaData, element, resourceManager, null );
    Assert.assertThat( result, CoreMatchers.is( Matchers.emptyArray() ) );

    Mockito.doReturn( "incorrect" ).when( metaData ).getValueRole();
    result = attrCore.getReferencedResources( metaData, element, resourceManager, "attr_0" );
    Assert.assertThat( result, CoreMatchers.is( Matchers.emptyArray() ) );
  }

  @Test
  public void testGetExtraCalculationFields() {
    Assert.assertThat( attrCore.getExtraCalculationFields( null ), CoreMatchers.is( Matchers.emptyArray() ) );
  }
}
