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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.apache.xerces.util.AttributesProxy;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.metadata.ReportProcessTaskMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportProcessTaskRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data.DataSourceElementHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data.MasterParameterDefinitionReadHandler;
import org.pentaho.reporting.engine.classic.extensions.modules.mailer.MailDefinition;
import org.pentaho.reporting.libraries.resourceloader.DependencyCollector;
import org.pentaho.reporting.libraries.resourceloader.ParameterKey;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceBundleData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.ResourceManagerBackend;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

public class MailDefinitionReadHandlerTest {

  private static final String URI = "test/uri";

  private MailDefinitionReadHandler handler;

  @Before
  public void setUp() throws SAXException {
    RootXmlReadHandler rootXmlReadHandler = mock( RootXmlReadHandler.class );
    handler = new MailDefinitionReadHandler();
    handler.init( rootXmlReadHandler, URI, "tag" );
  }

  @Test
  public void testDoneParsing() throws SAXException {
    Object objDefn = handler.getObject();
    assertThat( objDefn, is( nullValue() ) );

    handler.doneParsing();
    objDefn = handler.getObject();
    assertThat( objDefn, is( notNullValue() ) );
    assertThat( objDefn, is( instanceOf( MailDefinition.class ) ) );
    MailDefinition mailDefn = (MailDefinition) objDefn;
    assertThat( mailDefn.getAttachmentCount(), is( equalTo( 0 ) ) );
    assertThat( mailDefn.getBodyReport(), is( nullValue() ) );
    assertThat( mailDefn.getSessionProperties(), is( notNullValue() ) );
    assertThat( mailDefn.getSessionProperties().isEmpty(), is( equalTo( true ) ) );
    assertThat( mailDefn.getHeaderCount(), is( equalTo( 0 ) ) );
    assertThat( mailDefn.getBurstQuery(), is( nullValue() ) );
    assertThat( mailDefn.getParameterDefinition(), is( notNullValue() ) );
    assertThat( mailDefn.getParameterDefinition().getParameterCount(), is( equalTo( 0 ) ) );
    assertThat( mailDefn.getDataFactory(), is( notNullValue() ) );
  }

  @Test
  public void testParsingParameterDefinition() throws SAXException {
    XmlReadHandler childHandler =
        handler.getHandlerForChild( BundleNamespaces.DATADEFINITION, "parameter-definition", null );
    assertThat( childHandler, is( notNullValue() ) );
    assertThat( childHandler, is( instanceOf( MasterParameterDefinitionReadHandler.class ) ) );

    handler.doneParsing();

    Object objDefn = handler.getObject();
    assertThat( objDefn, is( notNullValue() ) );
    assertThat( objDefn, is( instanceOf( MailDefinition.class ) ) );
    MailDefinition mailDefn = (MailDefinition) objDefn;
    assertThat( mailDefn.getParameterDefinition().getParameterCount(), is( equalTo( 0 ) ) );
  }

  @Test
  public void testParsingDataSource() throws Exception {
    XmlReadHandler childHandler = handler.getHandlerForChild( BundleNamespaces.DATADEFINITION, "data-source", null );
    assertThat( childHandler, is( notNullValue() ) );
    assertThat( childHandler, is( instanceOf( DataSourceElementHandler.class ) ) );

    DataSourceElementHandler ds = (DataSourceElementHandler) childHandler;
    RootXmlReadHandler root = mock( RootXmlReadHandler.class );
    ResourceManagerBackend backend = mock( ResourceManagerBackend.class );
    ResourceManager resourceManager = new ResourceManager( backend );
    ResourceKey key = new ResourceKey( "schema", "identifier", null );
    Resource resource = mock( Resource.class );
    DataFactory df = mock( DataFactory.class );
    DependencyCollector collector = mock( DependencyCollector.class );

    doReturn( new String[] {} ).when( root ).getHelperObjectNames();
    doReturn( collector ).when( root ).getDependencyCollector();
    doReturn( resourceManager ).when( root ).getResourceManager();
    doReturn( key ).when( backend ).deriveKey( Mockito.<ResourceKey>any(), anyString(),
        ArgumentMatchers.any() );
    doReturn( resource ).when( backend ).create( Mockito.<ResourceManager>any(), Mockito.<ResourceBundleData>any(),
        Mockito.<ResourceKey>any(), Mockito.<Class[]>any() );
    doReturn( new ResourceKey[] {} ).when( resource ).getDependencies();
    doReturn( key ).when( resource ).getSource();
    doReturn( getClass() ).when( resource ).getTargetType();
    doReturn( df ).when( resource ).getResource();
    doReturn( df ).when( df ).derive();

    ds.init( root, BundleNamespaces.DATADEFINITION, "data-source" );
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "ref", null, BundleNamespaces.DATADEFINITION ), "string", "ttt" );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );
    ds.startElement( BundleNamespaces.DATADEFINITION, "data-source", fAttributesProxy );

    handler.doneParsing();

    Object objDefn = handler.getObject();
    assertThat( objDefn, is( notNullValue() ) );
    assertThat( objDefn, is( instanceOf( MailDefinition.class ) ) );
    MailDefinition mailDefn = (MailDefinition) objDefn;
    assertThat( mailDefn.getDataFactory(), is( notNullValue() ) );
  }

  @Test
  public void testParsingIncorrectParameters() throws SAXException {
    XmlReadHandler childHandler = handler.getHandlerForChild( BundleNamespaces.DATADEFINITION, "incorrect-tag", null );
    assertThat( childHandler, is( nullValue() ) );

    childHandler = handler.getHandlerForChild( "incorrectUri", "incorrect-tag", null );
    assertThat( childHandler, is( nullValue() ) );

    childHandler = handler.getHandlerForChild( URI, "incorrect-tag", null );
    assertThat( childHandler, is( nullValue() ) );
  }

  @Test
  public void testParsingHeader() throws SAXException {
    XmlReadHandler childHandler = handler.getHandlerForChild( URI, "header", null );
    assertThat( childHandler, is( notNullValue() ) );
    assertThat( childHandler, is( instanceOf( HeadersReadHandler.class ) ) );

    HeadersReadHandler headerHandler = (HeadersReadHandler) childHandler;
    headerHandler.init( mock( RootXmlReadHandler.class ), URI, "tag" );
    headerHandler.getHandlerForChild( URI, "formula-header", null );
    headerHandler.doneParsing();

    handler.doneParsing();

    Object objDefn = handler.getObject();
    assertThat( objDefn, is( notNullValue() ) );
    assertThat( objDefn, is( instanceOf( MailDefinition.class ) ) );
    MailDefinition mailDefn = (MailDefinition) objDefn;
    assertThat( mailDefn.getHeaderCount(), is( equalTo( 1 ) ) );
  }

  @Test
  public void testParsingSession() throws SAXException {
    XmlReadHandler childHandler = handler.getHandlerForChild( URI, "session", null );
    assertThat( childHandler, is( notNullValue() ) );
    assertThat( childHandler, is( instanceOf( SessionPropertiesReadHandler.class ) ) );

    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "name", null, URI ), "string", "test_name" );
    attrs.addAttribute( new QName( null, "value", null, URI ), "string", "test_value" );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    SessionPropertiesReadHandler sessionHandler = (SessionPropertiesReadHandler) childHandler;
    sessionHandler.init( mock( RootXmlReadHandler.class ), URI, "tag" );
    SessionPropertyReadHandler readHandler =
        (SessionPropertyReadHandler) sessionHandler.getHandlerForChild( URI, "property", null );
    readHandler.init( mock( RootXmlReadHandler.class ), URI, "tag" );
    readHandler.startParsing( fAttributesProxy );
    sessionHandler.doneParsing();

    handler.doneParsing();

    Object objDefn = handler.getObject();
    assertThat( objDefn, is( notNullValue() ) );
    assertThat( objDefn, is( instanceOf( MailDefinition.class ) ) );
    MailDefinition mailDefn = (MailDefinition) objDefn;
    assertThat( mailDefn.getSessionProperties(), is( notNullValue() ) );
    assertThat( mailDefn.getSessionProperties().getProperty( "test_name" ), is( equalTo( "test_value" ) ) );
  }

  @Test
  public void testParsingBurstQuery() throws SAXException {
    XmlReadHandler childHandler = handler.getHandlerForChild( URI, "burst-query", null );
    assertThat( childHandler, is( notNullValue() ) );
    assertThat( childHandler, is( instanceOf( StringReadHandler.class ) ) );

    XMLAttributesImpl attrs = new XMLAttributesImpl();
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    StringReadHandler stringHandler = (StringReadHandler) childHandler;
    stringHandler.init( mock( RootXmlReadHandler.class ), URI, "burst-query" );
    stringHandler.startElement( URI, "burst-query", fAttributesProxy );
    stringHandler.characters( new char[] { 'a' }, 0, 0 );
    stringHandler.endElement( URI, "burst-query" );

    handler.doneParsing();

    Object objDefn = handler.getObject();
    assertThat( objDefn, is( notNullValue() ) );
    assertThat( objDefn, is( instanceOf( MailDefinition.class ) ) );
    MailDefinition mailDefn = (MailDefinition) objDefn;
    assertThat( mailDefn.getBurstQuery(), is( notNullValue() ) );
  }

  @Test
  public void testParsingRecipientsQuery() throws SAXException {
    XmlReadHandler childHandler = handler.getHandlerForChild( URI, "recipients-query", null );
    assertThat( childHandler, is( notNullValue() ) );
    assertThat( childHandler, is( instanceOf( StringReadHandler.class ) ) );

    XMLAttributesImpl attrs = new XMLAttributesImpl();
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    StringReadHandler stringHandler = (StringReadHandler) childHandler;
    stringHandler.init( mock( RootXmlReadHandler.class ), URI, "burst-query" );
    stringHandler.startElement( URI, "burst-query", fAttributesProxy );
    stringHandler.characters( new char[] { 'a' }, 0, 0 );
    stringHandler.endElement( URI, "burst-query" );

    handler.doneParsing();

    Object objDefn = handler.getObject();
    assertThat( objDefn, is( notNullValue() ) );
    assertThat( objDefn, is( instanceOf( MailDefinition.class ) ) );
    MailDefinition mailDefn = (MailDefinition) objDefn;
    assertThat( mailDefn.getBurstQuery(), is( notNullValue() ) );
  }

  @Test
  public void testParsingBodyReport() throws Exception {
    XmlReadHandler childHandler = handler.getHandlerForChild( URI, "body-report", null );
    assertThat( childHandler, is( notNullValue() ) );
    assertThat( childHandler, is( instanceOf( ReportReadHandler.class ) ) );

    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "target-type", null, URI ), "string", "test-target" );
    attrs.addAttribute( new QName( null, "href", null, URI ), "string", "test-href" );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    RootXmlReadHandler rootXmlReadHandler = mock( RootXmlReadHandler.class );
    ResourceManagerBackend backend = mock( ResourceManagerBackend.class );
    ResourceManager resourceManager = new ResourceManager( backend );
    ResourceKey key = new ResourceKey( "schema", "identifier", null );
    Resource resource = mock( Resource.class );
    MasterReport report = mock( MasterReport.class );
    doReturn( resourceManager ).when( rootXmlReadHandler ).getResourceManager();
    doReturn( key ).when( backend ).deriveKey( Mockito.<ResourceKey>any(), anyString(),
        ArgumentMatchers.any() );
    doReturn( resource ).when( backend ).create( Mockito.<ResourceManager>any(), Mockito.<ResourceBundleData>any(),
        Mockito.<ResourceKey>any(), Mockito.<Class[]>any() );
    doReturn( new ResourceKey[] {} ).when( resource ).getDependencies();
    doReturn( key ).when( resource ).getSource();
    doReturn( getClass() ).when( resource ).getTargetType();
    doReturn( report ).when( resource ).getResource();

    ReportReadHandler reportHandler = (ReportReadHandler) childHandler;
    reportHandler.init( rootXmlReadHandler, URI, "body-report" );
    reportHandler.startParsing( fAttributesProxy );

    handler.doneParsing();

    Object objDefn = handler.getObject();
    assertThat( objDefn, is( notNullValue() ) );
    assertThat( objDefn, is( instanceOf( MailDefinition.class ) ) );
    MailDefinition mailDefn = (MailDefinition) objDefn;
    assertThat( mailDefn.getBodyReport(), is( equalTo( report ) ) );
  }

  @Test
  public void testParsingAttachmentReport() throws Exception {
    XmlReadHandler childHandler = handler.getHandlerForChild( URI, "attachment-report", null );
    assertThat( childHandler, is( notNullValue() ) );
    assertThat( childHandler, is( instanceOf( ReportReadHandler.class ) ) );

    ReportProcessTaskMetaData meta = mock( ReportProcessTaskMetaData.class );
    doReturn( "test-target" ).when( meta ).getName();
    ReportProcessTaskRegistry.getInstance().registerExportType( meta );

    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "target-type", null, URI ), "string", "test-target" );
    attrs.addAttribute( new QName( null, "href", null, URI ), "string", "test-href" );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    RootXmlReadHandler rootXmlReadHandler = mock( RootXmlReadHandler.class );
    ResourceManagerBackend backend = mock( ResourceManagerBackend.class );
    ResourceManager resourceManager = new ResourceManager( backend );
    ResourceKey key = new ResourceKey( "schema", "identifier", null );
    Resource resource = mock( Resource.class );
    MasterReport report = mock( MasterReport.class );
    doReturn( resourceManager ).when( rootXmlReadHandler ).getResourceManager();
    doReturn( key ).when( backend ).deriveKey( Mockito.<ResourceKey>any(), anyString(),
        ArgumentMatchers.any() );
    doReturn( resource ).when( backend ).create( Mockito.<ResourceManager>any(), Mockito.<ResourceBundleData>any(),
        Mockito.<ResourceKey>any(), Mockito.<Class[]>any() );
    doReturn( new ResourceKey[] {} ).when( resource ).getDependencies();
    doReturn( key ).when( resource ).getSource();
    doReturn( getClass() ).when( resource ).getTargetType();
    doReturn( report ).when( resource ).getResource();

    ReportReadHandler reportHandler = (ReportReadHandler) childHandler;
    reportHandler.init( rootXmlReadHandler, URI, "body-report" );
    reportHandler.startParsing( fAttributesProxy );

    handler.doneParsing();

    Object objDefn = handler.getObject();
    assertThat( objDefn, is( notNullValue() ) );
    assertThat( objDefn, is( instanceOf( MailDefinition.class ) ) );
    MailDefinition mailDefn = (MailDefinition) objDefn;
    assertThat( mailDefn.getAttachmentCount(), is( equalTo( 1 ) ) );
    assertThat( mailDefn.getAttachmentReport( 0 ), is( equalTo( report ) ) );
  }
}
