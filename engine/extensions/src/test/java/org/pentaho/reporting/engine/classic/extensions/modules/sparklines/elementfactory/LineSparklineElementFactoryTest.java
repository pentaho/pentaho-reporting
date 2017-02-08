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
 * Copyright (c) 2005-2015 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.sparklines.elementfactory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.pentaho.reporting.engine.classic.core.AttributeNames.Core.NAMESPACE;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.LineSparklineType;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineAttributeNames;

public class LineSparklineElementFactoryTest {

  private LineSparklineElementFactory elemFactory;

  @BeforeClass
  public static void init() {
    ClassicEngineBoot.getInstance().start();
  }

  @Before
  public void setUp() {
    elemFactory = new LineSparklineElementFactory();
  }

  @Test
  public void testCreateElement() {
    Element elem = elemFactory.createElement();
    assertThat( elem, is( notNullValue() ) );
    assertThat( elem.getName(), is( equalTo( StringUtils.EMPTY ) ) );
    assertThat( elem.getElementType(), is( instanceOf( LineSparklineType.class ) ) );
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
    assertThat( elem.getElementType(), is( instanceOf( LineSparklineType.class ) ) );
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
