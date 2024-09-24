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

public class AndMatcherTest {

  @Test
  public void testMatches() {
    NodeMatcher left = mock( NodeMatcher.class );
    NodeMatcher right = mock( NodeMatcher.class );
    MatcherContext context = mock( MatcherContext.class );
    ReportElement node = mock( ReportElement.class );

    doReturn( false ).when( left ).matches( context, node );
    doReturn( false ).when( right ).matches( context, node );
    AndMatcher matcher = new AndMatcher( left, right );
    assertThat( matcher.matches( context, node ), is( equalTo( false ) ) );

    doReturn( true ).when( left ).matches( context, node );
    matcher = new AndMatcher( left, right );
    assertThat( matcher.matches( context, node ), is( equalTo( false ) ) );

    doReturn( true ).when( right ).matches( context, node );
    matcher = new AndMatcher( left, right );
    assertThat( matcher.matches( context, node ), is( equalTo( true ) ) );
  }
}
