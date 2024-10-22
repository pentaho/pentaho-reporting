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

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import junit.framework.TestCase;
import org.apache.commons.vfs2.FileSystemException;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileTreeDto;

import jakarta.ws.rs.core.MediaType;
import java.awt.*;
import java.util.List;

public class JCRRepositoryTest extends TestCase {
  public JCRRepositoryTest() {
  }

  public JCRRepositoryTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    LibPensolBoot.getInstance().start();
  }

  public void testJCRRepository() throws FileSystemException {
    if ( GraphicsEnvironment.isHeadless() ) {
      return;
    }

    String url = "http://localhost:8080/pentaho";

    ClientConfig config = new ClientConfig();
    config.property( ClientProperties.FOLLOW_REDIRECTS , true );

    HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic( "joe", "password" );

    Client client = ClientBuilder.newClient( config );
    client.register( feature );

    WebTarget target = client.target( url + "/api/repo/files/children?depth=-1&filter=*" );
    RepositoryFileTreeDto tree = target.request( MediaType.APPLICATION_XML_TYPE ).get( RepositoryFileTreeDto.class );
    printDebugInfo( tree );

    final List<RepositoryFileTreeDto> children = tree.getChildren();
    for ( int i = 0; i < children.size(); i++ ) {
      final RepositoryFileTreeDto child = children.get( i );
      printDebugInfo( child );
    }

/*
    final FileSystemOptions fileSystemOptions = new FileSystemOptions();
    final DefaultFileSystemConfigBuilder configBuilder = new DefaultFileSystemConfigBuilder();
    configBuilder.setUserAuthenticator(fileSystemOptions, new StaticUserAuthenticator(url, "joe", "password"));
    FileObject fileObject = VFS.getManager().resolveFile(url, fileSystemOptions);

    System.out.println(fileObject);
    FileObject inventoryReport = fileObject.resolveFile("public/steel-wheels/reports/Inventory.prpt");
    System.out.println(inventoryReport);
    System.out.println(inventoryReport.exists());
    final FileContent content = inventoryReport.getContent();
    System.out.println(content.getAttribute("param-service-url"));
    */
  }

  private void printDebugInfo( final RepositoryFileTreeDto tree ) {
    System.out.println( "FileTreeDto: " + tree );
    System.out.println( "  - childs: " + tree.getChildren().size() );
    System.out.println( "FileDto: " + tree.getFile() );
    System.out.println( "  - Name: " + tree.getFile().getName() );
    System.out.println( "  - Last-Modified-Date: " + tree.getFile().getLastModifiedDate() );
    System.out.println( "  - Description: " + tree.getFile().getDescription() );
    System.out.println( "  - Title: " + tree.getFile().getTitle() );
  }
}
