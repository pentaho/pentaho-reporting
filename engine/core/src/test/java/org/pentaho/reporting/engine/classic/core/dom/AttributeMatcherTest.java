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


package org.pentaho.reporting.engine.classic.core.dom;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.ReportElement;

public class AttributeMatcherTest {

  private static final String NAME = "test_name";
  private static final String NAMESPACE = "test_namespace";

  @Test
  public void testMatches() {
    MatcherContext context = mock( MatcherContext.class );
    ReportElement node = mock( ReportElement.class );
    ReportAttributeMap<String> map = mock( ReportAttributeMap.class );

    doReturn( map ).when( node ).getAttributes();
    doReturn( null ).when( map ).getFirstAttribute( NAME );
    AttributeMatcher matcher = new AttributeMatcher( NAME );
    assertThat( matcher.matches( context, node ), is( equalTo( false ) ) );

    doReturn( NAME ).when( map ).getFirstAttribute( NAME );
    matcher = new AttributeMatcher( null, NAME, NAME );
    assertThat( matcher.matches( context, node ), is( equalTo( true ) ) );
  }

  @Test
  public void testMatchesWithNamespace() {
    MatcherContext context = mock( MatcherContext.class );
    ReportElement node = mock( ReportElement.class );
    ReportAttributeMap<String> map = mock( ReportAttributeMap.class );

    doReturn( map ).when( node ).getAttributes();
    doReturn( null ).when( map ).getAttribute( NAMESPACE, NAME );
    AttributeMatcher matcher = new AttributeMatcher( NAMESPACE, NAME );
    assertThat( matcher.matches( context, node ), is( equalTo( false ) ) );

    doReturn( NAME ).when( map ).getAttribute( NAMESPACE, NAME );
    matcher = new AttributeMatcher( NAMESPACE, NAME, NAME );
    assertThat( matcher.matches( context, node ), is( equalTo( true ) ) );
  }
}
