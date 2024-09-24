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

package org.pentaho.reporting.engine.classic.core.dom;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;

public class ElementMatcherTest {

  private static final String NAME = "test_name";

  @Test( expected = NullPointerException.class )
  public void testMatchesWithoutName() {
    String name = null;
    new ElementMatcher( name );
  }

  @Test( expected = NullPointerException.class )
  public void testMatchesWithoutNode() {
    ElementMatcher matcher = new ElementMatcher( NAME );
    matcher.matches( mock( MatcherContext.class ), null );
  }

  @Test
  public void testMatches() {
    ElementType type = mock( ElementType.class );
    ElementMetaData meta = mock( ElementMetaData.class );
    MatcherContext context = mock( MatcherContext.class );
    ReportElement node = mock( ReportElement.class );
    ElementType nodeElemType = mock( ElementType.class );
    ElementMetaData nodeMeta = mock( ElementMetaData.class );

    doReturn( meta ).when( type ).getMetaData();
    doReturn( NAME ).when( meta ).getName();
    doReturn( nodeElemType ).when( node ).getElementType();
    doReturn( nodeMeta ).when( nodeElemType ).getMetaData();
    doReturn( NAME + "_node" ).when( nodeMeta ).getName();

    ElementMatcher matcher = new ElementMatcher( type );
    assertThat( matcher.matches( context, node ), is( equalTo( false ) ) );

    doReturn( NAME ).when( nodeMeta ).getName();
    assertThat( matcher.matches( context, node ), is( equalTo( true ) ) );

    AttributeMatcher attrMatcher = mock( AttributeMatcher.class );
    doReturn( true ).when( attrMatcher ).matches( context, node );
    matcher.add( attrMatcher );
    assertThat( matcher.matches( context, node ), is( equalTo( true ) ) );

    doReturn( false ).when( attrMatcher ).matches( context, node );
    assertThat( matcher.matches( context, node ), is( equalTo( false ) ) );
  }
}
