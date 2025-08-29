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


package org.pentaho.reporting.libraries.pensol;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.UriBuilder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.pentaho.reporting.libraries.pensol.resources.TestRepositoryPublishResource;

import java.io.ByteArrayInputStream;
import java.net.URI;

/**
 * @author Andrey Khayrutdinov
 */
public class PublishRestUtilTestIT extends JerseyTest {

  @Override
  protected Application configure() {
    return new ResourceConfig()
            .register( MultiPartFeature.class)
            .register( TestRepositoryPublishResource.class )
            .property("jersey.config.server.pojo.mapping.feature", true);
  }

  @Test
  public void publishedSuccessfully() throws Exception {
    testPublish( TestRepositoryPublishResource.RETURN_200, 200 );
  }

  @Ignore
  @Test
  public void publishUnauthorized() throws Exception {
    testPublish( TestRepositoryPublishResource.RETURN_401, 401 );
  }

  @Test
  public void publishInvalid() throws Exception {
    testPublish( TestRepositoryPublishResource.RETURN_422, 422 );
  }

  @Ignore
  @Test
  public void publishCrashingServer() throws Exception {
    testPublish( TestRepositoryPublishResource.RETURN_500, 500 );
  }

  private void testPublish(String filename, int expectedCode) throws Exception {
    String baseUrl = getBaseUri().toString();
    if (baseUrl.endsWith("api")){
      baseUrl = baseUrl.replace("api", "");
    }

    PublishRestUtil util = new PublishRestUtil(baseUrl, "", "");
    int code = util.publishFile("", filename, new ByteArrayInputStream(new byte[0]), true);

    Assert.assertEquals(expectedCode, code);
  }

  @Override
  protected URI getBaseUri() {
    return UriBuilder.fromUri("http://localhost/api").port( this.getPort() ).build(new Object[0]);
  }
}
