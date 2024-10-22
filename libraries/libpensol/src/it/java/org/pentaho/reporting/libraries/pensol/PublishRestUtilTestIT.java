///*! ******************************************************************************
// *
// * Pentaho
// *
// * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
// *
// * Use of this software is governed by the Business Source License included
// * in the LICENSE.TXT file.
// *
// * Change Date: 2029-07-20
// ******************************************************************************/
//
//
//package org.pentaho.reporting.libraries.pensol;
//
//import jakarta.ws.rs.client.Client;
//import jakarta.ws.rs.client.ClientBuilder;
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
