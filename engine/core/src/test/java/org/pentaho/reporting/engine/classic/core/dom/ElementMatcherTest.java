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
