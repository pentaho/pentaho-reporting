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
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ReportElement;

public class AnyNodeMatcherTest {

  @Test
  public void testMatches() {
    MatcherContext context = mock( MatcherContext.class );
    ReportElement node = mock( ReportElement.class );

    AnyNodeMatcher matcher = new AnyNodeMatcher();
    assertThat( matcher.matches( context, node ), is( equalTo( true ) ) );

  }
}
