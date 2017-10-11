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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.builder.ExpressionPropertyMetaDataBuilder;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.SharedPropertyDescriptorProxy;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.BooleanPropertyEditor;

public class DefaultExpressionPropertyMetaDataTest {

  private static final String PROPERTY_ROLE = "propertyRole";
  private static final boolean COMPUTED = true;
  private static final boolean MANDATORY = true;

  private ExpressionPropertyMetaDataBuilder builder;
  private DefaultExpressionPropertyMetaData metaData;
  private SharedPropertyDescriptorProxy propertyDescriptor;
  private ExpressionPropertyCore expressionPropertyCore;

  private Expression element;

  @Before
  public void setUp() {
    element = mock( Expression.class );

    PropertyDescriptor propDescr = mock( PropertyDescriptor.class );
    propertyDescriptor = mock( SharedPropertyDescriptorProxy.class );
    expressionPropertyCore = mock( ExpressionPropertyCore.class );
    builder = mock( ExpressionPropertyMetaDataBuilder.class );

    doReturn( propertyDescriptor ).when( builder ).getDescriptor();
    doReturn( propDescr ).when( propertyDescriptor ).get();
    doReturn( PropertyEditor.class ).when( propDescr ).getPropertyType();
    doReturn( expressionPropertyCore ).when( builder ).getCore();
    doReturn( PropertyEditor.class ).when( builder ).getEditor();
    doReturn( PROPERTY_ROLE ).when( builder ).getValueRole();
    doReturn( COMPUTED ).when( builder ).isComputed();
    doReturn( MANDATORY ).when( builder ).isMandatory();
    doReturn( "test_name" ).when( builder ).getName();
    doReturn( "test_bundle" ).when( builder ).getBundleLocation();
    doReturn( "test_prefix" ).when( builder ).getKeyPrefix();

    metaData = new DefaultExpressionPropertyMetaData( builder );
  }

  @Test
  public void testIsComputed() {
    assertThat( metaData.isComputed(), is( equalTo( COMPUTED ) ) );
  }

  @Test
  public void testGetPropertyType() {
    assertThat( metaData.getPropertyType(), is( CoreMatchers.<Class<?>> equalTo( PropertyEditor.class ) ) );
  }

  @Test
  public void testGetPropertyRole() {
    assertThat( metaData.getPropertyRole(), is( equalTo( PROPERTY_ROLE ) ) );
  }

  @Test
  public void testIsMandatory() {
    assertThat( metaData.isMandatory(), is( equalTo( MANDATORY ) ) );
  }

  @Test
  public void testGetReferencedFields() {
    String[] expected = new String[] { "item" };
    doReturn( expected ).when( expressionPropertyCore ).getReferencedFields( metaData, element, null );
    String[] result = metaData.getReferencedFields( element, null );
    assertThat( result, is( equalTo( expected ) ) );
  }

  @Test
  public void testGetReferencedGroups() {
    String[] expected = new String[] { "item" };
    doReturn( expected ).when( expressionPropertyCore ).getReferencedGroups( metaData, element, null );
    String[] result = metaData.getReferencedGroups( element, null );
    assertThat( result, is( equalTo( expected ) ) );
  }

  @Test
  public void testGetReferencedElements() {
    String[] expected = new String[] { "item" };
    doReturn( expected ).when( expressionPropertyCore ).getReferencedElements( metaData, element, null );
    String[] result = metaData.getReferencedElements( element, null );
    assertThat( result, is( equalTo( expected ) ) );
  }

  @Test
  public void testGetReferencedResources() {
    Element elem = mock( Element.class );
    ResourceReference[] expected = new ResourceReference[] { mock( ResourceReference.class ) };
    doReturn( expected ).when( expressionPropertyCore ).getReferencedResources( metaData, element, null, elem, null );
    ResourceReference[] result = metaData.getReferencedResources( element, null, elem, null );
    assertThat( result, is( equalTo( expected ) ) );
  }

  @Test
  public void testGetEditor() {
    PropertyEditor result = metaData.getEditor();
    assertThat( result, is( nullValue() ) );

    doReturn( null ).when( builder ).getEditor();
    metaData = new DefaultExpressionPropertyMetaData( builder );
    result = metaData.getEditor();
    assertThat( result, is( nullValue() ) );

    doReturn( BooleanPropertyEditor.class ).when( builder ).getEditor();
    metaData = new DefaultExpressionPropertyMetaData( builder );
    result = metaData.getEditor();
    assertThat( result, is( instanceOf( BooleanPropertyEditor.class ) ) );
  }

  @Test
  public void testGetExtraCalculationFields() {
    String[] expected = new String[] { "item" };
    doReturn( expected ).when( expressionPropertyCore ).getExtraCalculationFields( metaData );
    String[] result = metaData.getExtraCalculationFields();
    assertThat( result, is( equalTo( expected ) ) );
  }
}
