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
 * Copyright (c) 2000 - 2024 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.imagemap.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMapEntry;
import org.pentaho.reporting.libraries.xmlns.LibXmlInfo;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

public class ImageMapWriterTest {

  private static final double SCALE = 1;
  private static final String MAP_ENTRY_NAMESPACE = "namespace_0_0";
  private static final String MAP_ENTRY_NAME = "name_0_0";
  private static final String MAP_ENTRY_VALUE = "value_0_0";
  private static final String MAP_NAMESPACE = "namespace_0";
  private static final String MAP_NAME = "name_0";
  private static final String MAP_VALUE = "value_0";
  private static final String DEFAULT_SHAPE = "rect";

  @Test
  public void testWriteImageMap() throws Exception {
    XmlWriter writer = mock( XmlWriter.class );
    ImageMap imageMap = mock( ImageMap.class );

    ArgumentCaptor<String> namespaceCaptor = ArgumentCaptor.forClass( String.class );
    ArgumentCaptor<AttributeList> attrsCaptor = ArgumentCaptor.forClass( AttributeList.class );
    ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass( String.class );
    ArgumentCaptor<Boolean> statusCaptor = ArgumentCaptor.forClass( Boolean.class );
    doNothing().when( writer ).writeTag( namespaceCaptor.capture(), nameCaptor.capture(), attrsCaptor.capture(),
        statusCaptor.capture() );
    doReturn( new String[] {} ).when( imageMap ).getNameSpaces();
    doReturn( new ImageMapEntry[] {} ).when( imageMap ).getMapEntries();

    ImageMapWriter.writeImageMap( writer, imageMap, SCALE );

    verify( writer ).writeTag( anyString(), anyString(), any( AttributeList.class ), anyBoolean() );
    verify( writer ).writeCloseTag();

    assertThat( namespaceCaptor.getValue(), is( equalTo( LibXmlInfo.XHTML_NAMESPACE ) ) );

    assertThat( attrsCaptor.getValue(), is( notNullValue() ) );
    assertThat( attrsCaptor.getValue().getAttribute( AttributeList.XMLNS_NAMESPACE, StringUtils.EMPTY ),
        is( equalTo( LibXmlInfo.XHTML_NAMESPACE ) ) );
    assertThat( attrsCaptor.getValue().toArray().length, is( equalTo( 1 ) ) );

    assertThat( nameCaptor.getValue(), is( equalTo( "map" ) ) );
    assertThat( statusCaptor.getValue(), is( equalTo( XmlWriter.OPEN ) ) );
  }

  @Test
  public void testWriteImageMapWithNamespaces() throws Exception {
    XmlWriter writer = mock( XmlWriter.class );
    ImageMap imageMap = mockImageMap();

    ArgumentCaptor<String> namespaceCaptor = ArgumentCaptor.forClass( String.class );
    ArgumentCaptor<AttributeList> attrsCaptor = ArgumentCaptor.forClass( AttributeList.class );
    ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass( String.class );
    ArgumentCaptor<Boolean> statusCaptor = ArgumentCaptor.forClass( Boolean.class );
    doNothing().when( writer ).writeTag( namespaceCaptor.capture(), nameCaptor.capture(), attrsCaptor.capture(),
        statusCaptor.capture() );

    ImageMapWriter.writeImageMap( writer, imageMap, SCALE );

    verify( writer, times( 2 ) ).writeTag( anyString(), anyString(), any( AttributeList.class ), anyBoolean() );
    verify( writer ).writeCloseTag();

    assertThat( namespaceCaptor.getAllValues().get( 0 ), is( equalTo( LibXmlInfo.XHTML_NAMESPACE ) ) );
    assertThat( namespaceCaptor.getAllValues().get( 1 ), is( equalTo( LibXmlInfo.XHTML_NAMESPACE ) ) );

    assertThat( attrsCaptor.getAllValues(), is( notNullValue() ) );
    assertThat( attrsCaptor.getAllValues().get( 0 ).toArray().length, is( equalTo( 2 ) ) );
    assertThat( attrsCaptor.getAllValues().get( 0 ).getAttribute( AttributeList.XMLNS_NAMESPACE, StringUtils.EMPTY ),
        is( equalTo( LibXmlInfo.XHTML_NAMESPACE ) ) );
    assertThat( attrsCaptor.getAllValues().get( 0 ).getAttribute( MAP_NAMESPACE, MAP_NAME ), is( equalTo( MAP_VALUE ) ) );
    assertThat( attrsCaptor.getAllValues().get( 1 ).toArray().length, is( equalTo( 3 ) ) );
    assertThat( attrsCaptor.getAllValues().get( 1 ).getAttribute( MAP_ENTRY_NAMESPACE, MAP_ENTRY_NAME ),
        is( equalTo( MAP_ENTRY_VALUE ) ) );
    assertThat( attrsCaptor.getAllValues().get( 1 ).getAttribute( LibXmlInfo.XHTML_NAMESPACE, "shape" ),
        is( equalTo( DEFAULT_SHAPE ) ) );
    assertThat( attrsCaptor.getAllValues().get( 1 ).getAttribute( LibXmlInfo.XHTML_NAMESPACE, "coords" ),
        is( equalTo( "5.0,10.0,20.0,50.0" ) ) );

    assertThat( nameCaptor.getAllValues().get( 0 ), is( equalTo( "map" ) ) );
    assertThat( nameCaptor.getAllValues().get( 1 ), is( equalTo( "area" ) ) );

    assertThat( statusCaptor.getAllValues().get( 0 ), is( equalTo( XmlWriter.OPEN ) ) );
    assertThat( statusCaptor.getAllValues().get( 1 ), is( equalTo( XmlWriter.CLOSE ) ) );
  }

  @Test
  public void testWriteImageMapAsString() throws Exception {
    ImageMap imageMap = mockImageMap();

    StringBuilder expectedValue = new StringBuilder();
    expectedValue.append( "<map xmlns=\"" );
    expectedValue.append( LibXmlInfo.XHTML_NAMESPACE );
    expectedValue.append( "\" " );
    expectedValue.append( MAP_NAME );
    expectedValue.append( "=\"" );
    expectedValue.append( MAP_VALUE );
    expectedValue.append( "\"><area " );
    expectedValue.append( MAP_ENTRY_NAME );
    expectedValue.append( "=\"" );
    expectedValue.append( MAP_ENTRY_VALUE );
    expectedValue.append( "\" shape=\"" );
    expectedValue.append( DEFAULT_SHAPE );
    expectedValue.append( "\" coords=\"5.0,10.0,20.0,50.0\" /></map>" );
    expectedValue.append( org.pentaho.reporting.libraries.base.util.StringUtils.getLineSeparator() );

    String result = ImageMapWriter.writeImageMapAsString( imageMap );
    assertThat( result, is( equalTo( expectedValue.toString() ) ) );
  }

  private ImageMapEntry mockMapEntry() {
    ImageMapEntry mapEntry = mock( ImageMapEntry.class );
    doReturn( new String[] { MAP_ENTRY_NAMESPACE } ).when( mapEntry ).getNameSpaces();
    doReturn( new String[] { MAP_ENTRY_NAME } ).when( mapEntry ).getNames( MAP_ENTRY_NAMESPACE );
    doReturn( MAP_ENTRY_VALUE ).when( mapEntry ).getAttribute( MAP_ENTRY_NAMESPACE, MAP_ENTRY_NAME );
    doReturn( DEFAULT_SHAPE ).when( mapEntry ).getAreaType();
    doReturn( new float[] { 5, 10, 20, 50 } ).when( mapEntry ).getAreaCoordinates();
    return mapEntry;
  }

  private ImageMap mockImageMap() {
    ImageMap imageMap = mock( ImageMap.class );
    ImageMapEntry mapEntry = mockMapEntry();
    doReturn( new String[] { MAP_NAMESPACE } ).when( imageMap ).getNameSpaces();
    doReturn( new String[] { MAP_NAME } ).when( imageMap ).getNames( MAP_NAMESPACE );
    doReturn( MAP_VALUE ).when( imageMap ).getAttribute( MAP_NAMESPACE, MAP_NAME );
    doReturn( new ImageMapEntry[] { mapEntry } ).when( imageMap ).getMapEntries();
    return imageMap;
  }
}
