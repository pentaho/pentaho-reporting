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
import static org.pentaho.reporting.engine.classic.core.AttributeNames.Core.NAMESPACE;

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
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.BarSparklineType;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineAttributeNames;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineStyleKeys;

public class BarSparklineElementFactoryTest {

  private BarSparklineElementFactory elemFactory;

  @BeforeClass
  public static void init() {
    ClassicEngineBoot.getInstance().start();
  }

  @Before
  public void setUp() {
    elemFactory = new BarSparklineElementFactory();
  }

  @Test
  public void testApplyStyle() {
    ElementStyleSheet style = mock( ElementStyleSheet.class );
    elemFactory.applyStyle( style );
    verify( style, never() ).setStyleProperty( any( StyleKey.class ), any() );

    elemFactory.setHighColor( Color.BLUE );
    elemFactory.setLastColor( Color.RED );

    elemFactory.applyStyle( style );
    verify( style ).setStyleProperty( SparklineStyleKeys.HIGH_COLOR, elemFactory.getHighColor() );
    verify( style ).setStyleProperty( SparklineStyleKeys.LAST_COLOR, elemFactory.getLastColor() );
  }

  @Test
  public void testCreateElement() {
    Element elem = elemFactory.createElement();
    assertThat( elem, is( notNullValue() ) );
    assertThat( elem.getName(), is( equalTo( StringUtils.EMPTY ) ) );
    assertThat( elem.getElementType(), is( instanceOf( BarSparklineType.class ) ) );
    assertThat( elem.getAttribute( NAMESPACE, AttributeNames.Core.VALUE ), is( nullValue() ) );
    assertThat( elem.getAttribute( NAMESPACE, AttributeNames.Core.FIELD ), is( nullValue() ) );
    assertThat( elem.getAttribute( SparklineAttributeNames.NAMESPACE, SparklineAttributeNames.SPACING ),
        is( nullValue() ) );
    assertThat( elem.getAttributeExpression( NAMESPACE, AttributeNames.Core.VALUE ), is( nullValue() ) );

    elemFactory.setName( "elem_name" );
    elemFactory.setContent( "elem_content" );
    elemFactory.setFieldname( "field_name" );
    elemFactory.setFormula( "test_formula" );
    elemFactory.setSpacing( 20 );

    elem = elemFactory.createElement();
    assertThat( elem, is( notNullValue() ) );
    assertThat( elem.getName(), is( equalTo( elemFactory.getName() ) ) );
    assertThat( elem.getElementType(), is( instanceOf( BarSparklineType.class ) ) );
    assertThat( elem.getAttribute( NAMESPACE, AttributeNames.Core.VALUE ), is( equalTo( elemFactory.getContent() ) ) );
    assertThat( (String) elem.getAttribute( NAMESPACE, AttributeNames.Core.FIELD ), is( equalTo( elemFactory
        .getFieldname() ) ) );
    assertThat( (Integer) elem.getAttribute( SparklineAttributeNames.NAMESPACE, SparklineAttributeNames.SPACING ),
        is( equalTo( elemFactory.getSpacing() ) ) );
    Object expressionObj = elem.getAttributeExpression( NAMESPACE, AttributeNames.Core.VALUE );
    assertThat( expressionObj, is( notNullValue() ) );
    assertThat( expressionObj, is( instanceOf( FormulaExpression.class ) ) );
    assertThat( ( (FormulaExpression) expressionObj ).getFormula(), is( equalTo( elemFactory.getFormula() ) ) );
  }
}
