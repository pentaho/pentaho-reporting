///*
// * This program is free software; you can redistribute it and/or modify it under the
// *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
// *  Foundation.
// *
// *  You should have received a copy of the GNU Lesser General Public License along with this
// *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
// *  or from the Free Software Foundation, Inc.,
// *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
// *
// *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
// *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// *  See the GNU Lesser General Public License for more details.
// *
// *  Copyright (c) 2006 - 2024 Hitachi Vantara..  All rights reserved.
// */
//
//package org.pentaho.reporting.libraries.pensol;
//
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import org.glassfish.jersey.media.multipart.MultiPartFeature;
//import org.glassfish.jersey.test.JerseyTest;
//import org.glassfish.jersey.test.spi.TestContainerFactory;
//import org.glassfish.jersey.jackson.JacksonFeature;
//import org.glassfish.jersey.server.ResourceConfig;
//import org.junit.Assert;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.pentaho.reporting.libraries.pensol.resources.TestRepositoryPublishResource;
//
//import java.io.ByteArrayInputStream;
//import java.net.URI;
//
///**
// * @author Andrey Khayrutdinov
// */
//public class PublishRestUtilTestIT extends JerseyTest {
//
//  @Override
//  protected AppDescriptor configure() {
//    ClientConfig config = new DefaultClientConfig();
//    config.getClasses().add( MultiPartWriter.class );
//    config.getClasses().add( TestRepositoryPublishResource.class );
//    config.getFeatures().put( JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE );
//
//    return new WebAppDescriptor.Builder( "org.pentaho.reporting.libraries.pensol.resources" )
//      .contextPath( "api" )
//      .clientConfig( config )
//      .build();
//  }
//
//  @Test
//  public void publishedSuccessfully() throws Exception {
//    testPublish( TestRepositoryPublishResource.RETURN_200, 200 );
//  }
//
//  @Ignore
//  @Test
//  public void publishUnauthorized() throws Exception {
//    testPublish( TestRepositoryPublishResource.RETURN_401, 401 );
//  }
//
//  @Test
//  public void publishInvalid() throws Exception {
//    testPublish( TestRepositoryPublishResource.RETURN_422, 422 );
//  }
//
//  @Ignore
//  @Test
//  public void publishCrashingServer() throws Exception {
//    testPublish( TestRepositoryPublishResource.RETURN_500, 500 );
//  }
//
//  private void testPublish( String filename, int expectedCode ) throws Exception {
//    URI uri = resource().getURI();
//    String baseUrl = "http://" + uri.getHost() + ":" + uri.getPort();
//
//    PublishRestUtil util = new PublishRestUtil( baseUrl, "", "" );
//    int code = util.publishFile( "", filename, new ByteArrayInputStream( new byte[ 0 ] ), true );
//
//    Assert.assertEquals( expectedCode, code );
//  }
//}
