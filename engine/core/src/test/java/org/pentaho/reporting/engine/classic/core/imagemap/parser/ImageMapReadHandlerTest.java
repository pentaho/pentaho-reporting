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

package org.pentaho.reporting.engine.classic.core.imagemap.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.imagemap.RectangleImageMapEntry;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;

public class ImageMapReadHandlerTest {

  private static final String URI = "test_uri";
  private static final String TAG = "test_tag";

  private ImageMapReadHandler handler;
  private RootXmlReadHandler root;

  @Before
  public void setUp() throws Exception {
    root = mock( RootXmlReadHandler.class );
    handler = new ImageMapReadHandler();
    handler.init( root, URI, TAG );
  }

  @Test
  public void testStartParsing() throws Exception {
    Attributes attrs = mock( Attributes.class );

    doReturn( 6 ).when( attrs ).getLength();
    doReturn( "xmlns" ).when( attrs ).getQName( 0 );

    doReturn( "xmlns:test" ).when( attrs ).getQName( 1 );

    doReturn( "test_qname" ).when( attrs ).getQName( 2 );
    doReturn( ":test" ).when( attrs ).getLocalName( 2 );

    doReturn( "test_qname" ).when( attrs ).getQName( 3 );
    doReturn( "shape" ).when( attrs ).getLocalName( 3 );
    doReturn( URI ).when( attrs ).getURI( 3 );

    doReturn( "test_qname" ).when( attrs ).getQName( 4 );
    doReturn( "coords" ).when( attrs ).getLocalName( 4 );
    doReturn( URI ).when( attrs ).getURI( 4 );

    doReturn( "test_qname" ).when( attrs ).getQName( 5 );
    doReturn( "test_local_name" ).when( attrs ).getLocalName( 5 );
    doReturn( URI ).when( attrs ).getURI( 5 );
    doReturn( "test_val" ).when( attrs ).getValue( 5 );

    handler.startParsing( attrs );

    Object mapEntry = handler.getObject();
    assertThat( mapEntry, is( instanceOf( ImageMap.class ) ) );
    ImageMap entry = (ImageMap) mapEntry;
    assertThat( entry.getAttribute( URI, "test_local_name" ), is( equalTo( "test_val" ) ) );
  }

  @Test
  public void testGetHandlerForChild() throws Exception {
    Attributes attrs = mock( Attributes.class );
    XmlReadHandler result = handler.getHandlerForChild( "incorrect_uri", TAG, attrs );
    assertThat( result, is( nullValue() ) );

    result = handler.getHandlerForChild( URI, TAG, attrs );
    assertThat( result, is( nullValue() ) );

    result = handler.getHandlerForChild( URI, "area", attrs );
    assertThat( result, is( instanceOf( AreaReadHandler.class ) ) );

    result.init( root, URI, TAG );
    result.startElement( URI, TAG, createAreaAttrs() );

    handler.doneParsing();
    Object mapEntry = handler.getObject();
    assertThat( mapEntry, is( instanceOf( ImageMap.class ) ) );
    ImageMap entry = (ImageMap) mapEntry;
    assertThat( entry.getMapEntries().length, is( equalTo( 1 ) ) );
    assertThat( entry.getMapEntries()[0], is( instanceOf( RectangleImageMapEntry.class ) ) );
  }

  private Attributes createAreaAttrs() {
    Attributes attrs = mock( Attributes.class );

    doReturn( "rect" ).when( attrs ).getValue( URI, "shape" );
    doReturn( "5,30,25,50" ).when( attrs ).getValue( URI, "coords" );

    doReturn( 1 ).when( attrs ).getLength();
    doReturn( "test_qname" ).when( attrs ).getQName( 0 );
    doReturn( "test_local_name" ).when( attrs ).getLocalName( 0 );
    doReturn( URI + "_1" ).when( attrs ).getURI( 0 );
    doReturn( "test_val" ).when( attrs ).getValue( 0 );
    return attrs;
  }
}
