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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.pentaho.reporting.engine.classic.core.imagemap.CircleImageMapEntry;
import org.pentaho.reporting.engine.classic.core.imagemap.DefaultImageMapEntry;
import org.pentaho.reporting.engine.classic.core.imagemap.PolygonImageMapEntry;
import org.pentaho.reporting.engine.classic.core.imagemap.RectangleImageMapEntry;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.Attributes;

public class AreaReadHandlerTest {

  private static final String URI = "test_uri";

  private AreaReadHandler handler;

  @Before
  public void setUp() throws Exception {
    RootXmlReadHandler root = mock( RootXmlReadHandler.class );
    handler = new AreaReadHandler();
    handler.init( root, URI, "test_tag" );
  }

  @Test
  public void testDefaultParsing() throws Exception {
    Attributes attrs = mock( Attributes.class );

    doReturn( "default" ).when( attrs ).getValue( URI, "shape" );
    doReturn( "" ).when( attrs ).getValue( URI, "coords" );

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
    assertThat( mapEntry, is( instanceOf( DefaultImageMapEntry.class ) ) );
    DefaultImageMapEntry entry = (DefaultImageMapEntry) mapEntry;
    assertThat( entry.getAttribute( URI, "test_local_name" ), is( equalTo( "test_val" ) ) );
  }

  @Test( expected = ParseException.class )
  public void testParseCoordsException() throws Exception {

    Attributes attrs = mock( Attributes.class );

    doReturn( "rect" ).when( attrs ).getValue( URI, "shape" );
    doReturn( "string" ).when( attrs ).getValue( URI, "coords" );

    handler.startParsing( attrs );
  }

  @Test( expected =  ParseException.class )
  public void testRectParsingException() throws Exception {

    Attributes attrs = mock( Attributes.class );

    doReturn( "rect" ).when( attrs ).getValue( URI, "shape" );
    doReturn( "5,30,25" ).when( attrs ).getValue( URI, "coords" );

    handler.startParsing( attrs );
  }

  @Test
  public void testRectParsing() throws Exception {
    Attributes attrs = mock( Attributes.class );

    doReturn( "rect" ).when( attrs ).getValue( URI, "shape" );
    doReturn( "5,30,25,50" ).when( attrs ).getValue( URI, "coords" );

    doReturn( 1 ).when( attrs ).getLength();
    doReturn( "test_qname" ).when( attrs ).getQName( 0 );
    doReturn( "test_local_name" ).when( attrs ).getLocalName( 0 );
    doReturn( URI + "_1" ).when( attrs ).getURI( 0 );
    doReturn( "test_val" ).when( attrs ).getValue( 0 );

    handler.startParsing( attrs );

    Object mapEntry = handler.getObject();
    assertThat( mapEntry, is( instanceOf( RectangleImageMapEntry.class ) ) );
    RectangleImageMapEntry entry = (RectangleImageMapEntry) mapEntry;
    assertThat( entry.getAttribute( URI + "_1", "test_local_name" ), is( equalTo( "test_val" ) ) );
  }

  @Test( expected = ParseException.class )
  public void testDefaultRectParsingException() throws Exception {

    Attributes attrs = mock( Attributes.class );

    doReturn( "_rect_" ).when( attrs ).getValue( URI, "shape" );
    doReturn( "5,30,25" ).when( attrs ).getValue( URI, "coords" );

    handler.startParsing( attrs );
  }

  @Test
  public void testDefaultRectParsing() throws Exception {
    Attributes attrs = mock( Attributes.class );

    doReturn( "_rect_" ).when( attrs ).getValue( URI, "shape" );
    doReturn( "5,30,25,50" ).when( attrs ).getValue( URI, "coords" );

    doReturn( 1 ).when( attrs ).getLength();
    doReturn( "test_qname" ).when( attrs ).getQName( 0 );
    doReturn( "test_local_name" ).when( attrs ).getLocalName( 0 );
    doReturn( URI ).when( attrs ).getURI( 0 );
    doReturn( "test_val" ).when( attrs ).getValue( 0 );

    handler.startParsing( attrs );

    Object mapEntry = handler.getObject();
    assertThat( mapEntry, is( instanceOf( RectangleImageMapEntry.class ) ) );
    RectangleImageMapEntry entry = (RectangleImageMapEntry) mapEntry;
    assertThat( entry.getAttribute( URI, "test_local_name" ), is( equalTo( "test_val" ) ) );
  }

  @Test( expected = ParseException.class )
  public void testCircleParsingException() throws Exception {

    Attributes attrs = mock( Attributes.class );

    doReturn( "circle" ).when( attrs ).getValue( URI, "shape" );
    doReturn( "5,30" ).when( attrs ).getValue( URI, "coords" );

    handler.startParsing( attrs );
  }

  @Test
  public void testCircleParsing() throws Exception {
    Attributes attrs = mock( Attributes.class );

    doReturn( "circle" ).when( attrs ).getValue( URI, "shape" );
    doReturn( "5,30,25" ).when( attrs ).getValue( URI, "coords" );

    doReturn( 1 ).when( attrs ).getLength();
    doReturn( "test_qname" ).when( attrs ).getQName( 0 );
    doReturn( "test_local_name" ).when( attrs ).getLocalName( 0 );
    doReturn( URI ).when( attrs ).getURI( 0 );
    doReturn( "test_val" ).when( attrs ).getValue( 0 );

    handler.startParsing( attrs );

    Object mapEntry = handler.getObject();
    assertThat( mapEntry, is( instanceOf( CircleImageMapEntry.class ) ) );
    CircleImageMapEntry entry = (CircleImageMapEntry) mapEntry;
    assertThat( entry.getAttribute( URI, "test_local_name" ), is( equalTo( "test_val" ) ) );
  }

  @Test( expected = ParseException.class )
  public void testPolyParsingException() throws Exception {

    Attributes attrs = mock( Attributes.class );

    doReturn( "poly" ).when( attrs ).getValue( URI, "shape" );
    doReturn( "5,30,20" ).when( attrs ).getValue( URI, "coords" );

    handler.startParsing( attrs );
  }

  @Test
  public void testPolyParsing() throws Exception {
    Attributes attrs = mock( Attributes.class );

    doReturn( "poly" ).when( attrs ).getValue( URI, "shape" );
    doReturn( "5,30,25,50" ).when( attrs ).getValue( URI, "coords" );

    doReturn( 1 ).when( attrs ).getLength();
    doReturn( "test_qname" ).when( attrs ).getQName( 0 );
    doReturn( "test_local_name" ).when( attrs ).getLocalName( 0 );
    doReturn( URI ).when( attrs ).getURI( 0 );
    doReturn( "test_val" ).when( attrs ).getValue( 0 );

    handler.startParsing( attrs );

    Object mapEntry = handler.getObject();
    assertThat( mapEntry, is( instanceOf( PolygonImageMapEntry.class ) ) );
    PolygonImageMapEntry entry = (PolygonImageMapEntry) mapEntry;
    assertThat( entry.getAttribute( URI, "test_local_name" ), is( equalTo( "test_val" ) ) );
  }
}
