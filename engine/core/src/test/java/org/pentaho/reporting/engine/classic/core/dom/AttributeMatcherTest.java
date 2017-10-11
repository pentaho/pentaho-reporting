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
