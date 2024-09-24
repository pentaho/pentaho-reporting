/*!
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
 * Copyright (c) 2005-2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.mailer.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.apache.xerces.util.AttributesProxy;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.resourceloader.ParameterKey;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceBundleData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.ResourceManagerBackend;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.SAXException;

public class ReportReadHandlerTest {

  private static final String URI = "test/uri";
  private static final String ATTR_TYPE = "string";
  private static final String TARGET = "test-target";


  private ReportReadHandler handler;
  private RootXmlReadHandler rootHandler;
  private MasterReport report;
  private Resource resource;

  @Before
  public void setUp() throws Exception {
    handler = new ReportReadHandler();

    rootHandler = mock( RootXmlReadHandler.class );
    ResourceManagerBackend backend = mock( ResourceManagerBackend.class );
    ResourceManager resourceManager = new ResourceManager( backend );
    ResourceKey key = new ResourceKey( "schema", "identifier", null );
    resource = mock( Resource.class );
    report = mock( MasterReport.class );

    doReturn( resourceManager ).when( rootHandler ).getResourceManager();
    doReturn( key ).when( backend ).deriveKey( Mockito.<ResourceKey>any(), anyString(),
        ArgumentMatchers.any() );
    doReturn( resource ).when( backend ).create( Mockito.<ResourceManager>any(), Mockito.<ResourceBundleData>any(),
        Mockito.<ResourceKey>any(), Mockito.<Class[]>any() );
    doReturn( new ResourceKey[] {} ).when( resource ).getDependencies();
    doReturn( key ).when( resource ).getSource();
    doReturn( getClass() ).when( resource ).getTargetType();
    doReturn( report ).when( resource ).getResource();

    handler.init( rootHandler, URI, "tag" );
  }

  @Test
  public void testParsing() throws Exception {
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "target-type", null, URI ), ATTR_TYPE, TARGET );
    attrs.addAttribute( new QName( null, "href", null, URI ), ATTR_TYPE, "test-href" );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    handler.startParsing( fAttributesProxy );

    assertThat( handler.getReport(), is( equalTo( report ) ) );
    assertThat( handler.getTargetType(), is( equalTo( TARGET ) ) );
  }

  @Test( expected = ParseException.class )
  public void testParsingIncorrectTarget() throws Exception {
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "target-type", null, URI ), ATTR_TYPE, null );
    attrs.addAttribute( new QName( null, "href", null, URI ), ATTR_TYPE, null );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    handler.startParsing( fAttributesProxy );
  }

  @Test( expected = ParseException.class )
  public void testParsingIncorrectHref() throws Exception {
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "target-type", null, URI ), ATTR_TYPE, TARGET );
    attrs.addAttribute( new QName( null, "href", null, URI ), ATTR_TYPE, null );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    handler.startParsing( fAttributesProxy );
  }

  @Test( expected = ParseException.class )
  public void testParsingIncorrectResource() throws Exception {
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "target-type", null, URI ), ATTR_TYPE, TARGET );
    attrs.addAttribute( new QName( null, "href", null, URI ), ATTR_TYPE, "test-href" );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    doThrow( ResourceException.class ).when( resource ).getResource();

    handler.startParsing( fAttributesProxy );
  }

  @Test
  public void testGetObject() throws SAXException {
    assertThat( handler.getObject(), is( nullValue() ) );
  }
}
