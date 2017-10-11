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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.emptyArray;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.ResourceManagerBackend;

public class DefaultReportPreProcessorPropertyCoreTest {

  private DefaultReportPreProcessorPropertyCore preProcessor = new DefaultReportPreProcessorPropertyCore();
  private ReportPreProcessorPropertyMetaData metaData;
  private Expression expression;

  @Before
  public void setUp() {
    metaData = mock( ReportPreProcessorPropertyMetaData.class );
    expression = mock( Expression.class );
  }

  @Test( expected = NullPointerException.class )
  public void testGetReferencedFieldsWithoutExpression() {
    preProcessor.getReferencedFields( metaData, null, null );
  }

  @Test
  public void testGetReferencedFields() {
    String[] result = preProcessor.getReferencedFields( metaData, expression, null );
    assertThat( result, is( emptyArray() ) );

    String[] attributeValue = new String[] { "attr_0" };
    doReturn( "Field" ).when( metaData ).getPropertyRole();
    result = preProcessor.getReferencedFields( metaData, expression, attributeValue );
    assertThat( result, is( equalTo( attributeValue ) ) );

    result = preProcessor.getReferencedFields( metaData, expression, attributeValue[0] );
    assertThat( result, is( equalTo( attributeValue ) ) );

    doReturn( "Message" ).when( metaData ).getPropertyRole();
    result = preProcessor.getReferencedFields( metaData, expression, attributeValue[0] );
    assertThat( result, is( emptyArray() ) );

    doReturn( "Formula" ).when( metaData ).getPropertyRole();
    result = preProcessor.getReferencedFields( metaData, expression, "namespace:[a+b]" );
    assertThat( result, is( equalTo( new String[] { "a+b" } ) ) );

    doReturn( "Formula" ).when( metaData ).getPropertyRole();
    result = preProcessor.getReferencedFields( metaData, expression, "namespace" );
    assertThat( result, is( emptyArray() ) );

    doReturn( "incorrect" ).when( metaData ).getPropertyRole();
    result = preProcessor.getReferencedFields( metaData, expression, "namespace:[a+b]" );
    assertThat( result, is( emptyArray() ) );
  }

  @Test( expected = NullPointerException.class )
  public void testGetReferencedGroupsWithoutExpression() {
    preProcessor.getReferencedGroups( metaData, null, null );
  }

  @Test
  public void testGetReferencedGroups() {
    String[] result = preProcessor.getReferencedGroups( metaData, expression, null );
    assertThat( result, is( emptyArray() ) );

    String[] attributeValue = new String[] { "attr_0" };
    doReturn( "Group" ).when( metaData ).getPropertyRole();
    result = preProcessor.getReferencedGroups( metaData, expression, attributeValue );
    assertThat( result, is( equalTo( attributeValue ) ) );

    result = preProcessor.getReferencedGroups( metaData, expression, attributeValue[0] );
    assertThat( result, is( equalTo( attributeValue ) ) );

    doReturn( "incorrect" ).when( metaData ).getPropertyRole();
    result = preProcessor.getReferencedGroups( metaData, expression, attributeValue[0] );
    assertThat( result, is( emptyArray() ) );
  }

  @Test( expected = NullPointerException.class )
  public void testGetReferencedElementsWithoutExpression() {
    preProcessor.getReferencedElements( metaData, null, null );
  }

  @Test
  public void testGetReferencedElements() {
    String[] result = preProcessor.getReferencedElements( metaData, expression, null );
    assertThat( result, is( emptyArray() ) );

    String[] attributeValue = new String[] { "attr_0" };
    doReturn( "ElementName" ).when( metaData ).getPropertyRole();
    result = preProcessor.getReferencedElements( metaData, expression, attributeValue );
    assertThat( result, is( equalTo( attributeValue ) ) );

    result = preProcessor.getReferencedElements( metaData, expression, attributeValue[0] );
    assertThat( result, is( equalTo( attributeValue ) ) );

    doReturn( "incorrect" ).when( metaData ).getPropertyRole();
    result = preProcessor.getReferencedElements( metaData, expression, attributeValue[0] );
    assertThat( result, is( emptyArray() ) );
  }

  @Test( expected = NullPointerException.class )
  public void testGetReferencedResourcesWithoutExpression() {
    preProcessor.getReferencedResources( metaData, null, null, null, null );
  }

  @Test( expected = NullPointerException.class )
  public void testGetReferencedResourcesWithoutReportElement() {
    preProcessor.getReferencedResources( metaData, expression, null, null, null );
  }

  @Test( expected = NullPointerException.class )
  public void testGetReferencedResourcesWithoutResourceManager() {
    preProcessor.getReferencedResources( metaData, expression, null, mock( Element.class ), null );
  }

  @Test
  public void testGetReferencedResources() throws ResourceKeyCreationException {
    Element elem = mock( Element.class );
    ResourceManagerBackend resourceManagerBackend = mock( ResourceManagerBackend.class );
    ResourceManager resourceManager = new ResourceManager( resourceManagerBackend );

    ResourceKey contentBase = new ResourceKey( "contentBase_schema", "contentBase_id", null );
    ResourceKey elementSource = new ResourceKey( "elementSource_schema", "elementSource_id", null );
    ResourceKey attributeValue = new ResourceKey( "attributeValue_schema", "attributeValue_id", null );

    doReturn( "Content" ).when( metaData ).getPropertyRole();
    doReturn( contentBase ).when( elem ).getContentBase();
    doReturn( elementSource ).when( elem ).getDefinitionSource();
    doReturn( elementSource ).when( resourceManagerBackend ).deriveKey( contentBase, "attr_0", null );
    doReturn( elementSource ).when( resourceManagerBackend ).createKey( "attr_0", null );

    ResourceReference[] result =
        preProcessor.getReferencedResources( metaData, expression, attributeValue, elem, resourceManager );
    assertThat( result, is( notNullValue() ) );
    assertThat( result.length, is( equalTo( 1 ) ) );
    assertThat( result[0].getPath(), is( equalTo( attributeValue ) ) );
    assertThat( result[0].isLinked(), is( equalTo( false ) ) );

    result = preProcessor.getReferencedResources( metaData, expression, "attr_0", elem, resourceManager );
    assertThat( result, is( notNullValue() ) );
    assertThat( result.length, is( equalTo( 1 ) ) );
    assertThat( result[0].getPath(), is( equalTo( elementSource ) ) );
    assertThat( result[0].isLinked(), is( equalTo( true ) ) );

    doReturn( null ).when( elem ).getContentBase();
    result = preProcessor.getReferencedResources( metaData, expression, "attr_0", elem, resourceManager );
    assertThat( result, is( notNullValue() ) );
    assertThat( result.length, is( equalTo( 1 ) ) );
    assertThat( result[0].getPath(), is( equalTo( elementSource ) ) );
    assertThat( result[0].isLinked(), is( equalTo( true ) ) );

    result = preProcessor.getReferencedResources( metaData, expression, null, elem, resourceManager );
    assertThat( result, is( emptyArray() ) );

    doReturn( "incorrect" ).when( metaData ).getPropertyRole();
    result = preProcessor.getReferencedResources( metaData, expression, "attr_0", elem, resourceManager );
    assertThat( result, is( emptyArray() ) );
  }

  @Test
  public void testGetExtraCalculationFields() {
    assertThat( preProcessor.getExtraCalculationFields( null ), is( emptyArray() ) );
  }
}
