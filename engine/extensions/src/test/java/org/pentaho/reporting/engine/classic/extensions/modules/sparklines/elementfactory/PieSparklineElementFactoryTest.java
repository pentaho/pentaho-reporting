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
 * Copyright (c) 2005 - 2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.sparklines.elementfactory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineAttributeNames.NAMESPACE;

import java.awt.Color;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.PieSparklineType;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineAttributeNames;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineStyleKeys;

public class PieSparklineElementFactoryTest {

  private PieSparklineElementFactory elemFactory;

  @BeforeClass
  public static void init() {
    ClassicEngineBoot.getInstance().start();
  }

  @Before
  public void setUp() {
    elemFactory = new PieSparklineElementFactory();
  }

  @Test
  public void testApplyStyle() {
    ElementStyleSheet style = mock( ElementStyleSheet.class );
    elemFactory.applyStyle( style );
    verify( style, never() ).setStyleProperty( any( StyleKey.class ), any() );

    elemFactory.setHighColor( Color.BLUE );
    elemFactory.setLowColor( Color.RED );
    elemFactory.setMediumColor( Color.YELLOW );

    elemFactory.applyStyle( style );
    verify( style ).setStyleProperty( SparklineStyleKeys.HIGH_COLOR, elemFactory.getHighColor() );
    verify( style ).setStyleProperty( SparklineStyleKeys.LOW_COLOR, elemFactory.getLowColor() );
    verify( style ).setStyleProperty( SparklineStyleKeys.MEDIUM_COLOR, elemFactory.getMediumColor() );
  }

  @Test
  public void testCreateElement() {
    Element elem = elemFactory.createElement();
    assertThat( elem, is( notNullValue() ) );
    assertThat( elem.getName(), is( equalTo( StringUtils.EMPTY ) ) );
    assertThat( elem.getElementType(), is( instanceOf( PieSparklineType.class ) ) );
    assertThat( elem.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE ), is( nullValue() ) );
    assertThat( elem.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD ), is( nullValue() ) );
    assertThat( elem.getAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE ),
        is( nullValue() ) );
    assertThat( elem.getAttribute( NAMESPACE, SparklineAttributeNames.START_ANGLE ), is( nullValue() ) );
    assertThat( elem.getAttribute( NAMESPACE, SparklineAttributeNames.LOW_SLICE ), is( nullValue() ) );
    assertThat( elem.getAttribute( NAMESPACE, SparklineAttributeNames.MEDIUM_SLICE ), is( nullValue() ) );
    assertThat( elem.getAttribute( NAMESPACE, SparklineAttributeNames.HIGH_SLICE ), is( nullValue() ) );
    assertThat( elem.getAttribute( NAMESPACE, SparklineAttributeNames.COUNTER_CLOCKWISE ), is( nullValue() ) );

    elemFactory.setName( "elem_name" );
    elemFactory.setContent( "elem_content" );
    elemFactory.setFieldname( "field_name" );
    elemFactory.setFormula( "test_formula" );
    elemFactory.setStartAngle( 100 );
    elemFactory.setLowSlice( 5.5 );
    elemFactory.setMediumSlice( 10.5 );
    elemFactory.setHighSlice( 20.5 );
    elemFactory.setCounterClockwise( true );

    elem = elemFactory.createElement();
    assertThat( elem, is( notNullValue() ) );
    assertThat( elem.getName(), is( equalTo( elemFactory.getName() ) ) );
    assertThat( elem.getElementType(), is( instanceOf( PieSparklineType.class ) ) );
    assertThat( elem.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE ), is( equalTo( elemFactory
        .getContent() ) ) );
    assertThat( (String) elem.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD ),
        is( equalTo( elemFactory.getFieldname() ) ) );
    assertThat( (Integer) elem.getAttribute( NAMESPACE, SparklineAttributeNames.START_ANGLE ), is( equalTo( elemFactory
        .getStartAngle() ) ) );
    assertThat( (Double) elem.getAttribute( NAMESPACE, SparklineAttributeNames.LOW_SLICE ), is( equalTo( elemFactory
        .getLowSlice() ) ) );
    assertThat( (Double) elem.getAttribute( NAMESPACE, SparklineAttributeNames.MEDIUM_SLICE ), is( equalTo( elemFactory
        .getMediumSlice() ) ) );
    assertThat( (Double) elem.getAttribute( NAMESPACE, SparklineAttributeNames.HIGH_SLICE ), is( equalTo( elemFactory
        .getHighSlice() ) ) );
    assertThat( (Boolean) elem.getAttribute( NAMESPACE, SparklineAttributeNames.COUNTER_CLOCKWISE ),
        is( equalTo( elemFactory.getCounterClockwise() ) ) );
    Object expressionObj = elem.getAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
    assertThat( expressionObj, is( notNullValue() ) );
    assertThat( expressionObj, is( instanceOf( FormulaExpression.class ) ) );
    assertThat( ( (FormulaExpression) expressionObj ).getFormula(), is( equalTo( elemFactory.getFormula() ) ) );
  }
}
