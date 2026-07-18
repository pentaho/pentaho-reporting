/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.core.dom;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ReportElement;

public class OrMatcherTest {

  @Test
  public void testMatches() {
    NodeMatcher left = mock( NodeMatcher.class );
    NodeMatcher right = mock( NodeMatcher.class );
    MatcherContext context = mock( MatcherContext.class );
    ReportElement node = mock( ReportElement.class );

    OrMatcher matcher = new OrMatcher();
    assertThat( matcher.matches( context, node ), is( equalTo( false ) ) );

    doReturn( false ).when( left ).matches( context, node );
    doReturn( true ).when( right ).matches( context, node );
    matcher = new OrMatcher( left, right );
    assertThat( matcher.matches( context, node ), is( equalTo( true ) ) );

  }
}
