///*!
//* This program is free software; you can redistribute it and/or modify it under the
//* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
//* Foundation.
//*
//* You should have received a copy of the GNU Lesser General Public License along with this
//* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
//* or from the Free Software Foundation, Inc.,
//* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
//*
//* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
//* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//* See the GNU Lesser General Public License for more details.
//*
//* Copyright (c) 2002-2024 Hitachi Vantara.  All rights reserved.
//*/
//
//package org.pentaho.reporting.libraries.pensol;
//
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.WebTarget;
//import org.glassfish.jersey.client.ClientConfig;
//import org.glassfish.jersey.client.ClientProperties;
//import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
//import junit.framework.TestCase;
//import org.apache.commons.vfs2.FileSystemException;
//import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileTreeDto;
//
//import javax.ws.rs.core.MediaType;
//import java.awt.*;
//import java.util.List;
//
//public class JCRRepositoryTest extends TestCase {
//  public JCRRepositoryTest() {
//  }
//
//  public JCRRepositoryTest( final String name ) {
//    super( name );
//  }
//
//  protected void setUp() throws Exception {
//    LibPensolBoot.getInstance().start();
//  }
//
//  public void testJCRRepository() throws FileSystemException {
//    if ( GraphicsEnvironment.isHeadless() ) {
//      return;
//    }
//
//    String url = "http://localhost:8080/pentaho";
//
//    ClientConfig config = new ClientConfig();
//    config.property( ClientProperties.FOLLOW_REDIRECTS , true );
//
//    HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic( "joe", "password" );
//
//    Client client = ClientBuilder.newClient( config );
//    client.register( feature );
//
//    WebTarget target = client.target( url + "/api/repo/files/children?depth=-1&filter=*" );
//    RepositoryFileTreeDto tree = target.request( MediaType.APPLICATION_XML_TYPE ).get( RepositoryFileTreeDto.class );
//    printDebugInfo( tree );
//
//    final List<RepositoryFileTreeDto> children = tree.getChildren();
//    for ( int i = 0; i < children.size(); i++ ) {
//      final RepositoryFileTreeDto child = children.get( i );
//      printDebugInfo( child );
//    }
//
///*
//    final FileSystemOptions fileSystemOptions = new FileSystemOptions();
//    final DefaultFileSystemConfigBuilder configBuilder = new DefaultFileSystemConfigBuilder();
//    configBuilder.setUserAuthenticator(fileSystemOptions, new StaticUserAuthenticator(url, "joe", "password"));
//    FileObject fileObject = VFS.getManager().resolveFile(url, fileSystemOptions);
//
//    System.out.println(fileObject);
//    FileObject inventoryReport = fileObject.resolveFile("public/steel-wheels/reports/Inventory.prpt");
//    System.out.println(inventoryReport);
//    System.out.println(inventoryReport.exists());
//    final FileContent content = inventoryReport.getContent();
//    System.out.println(content.getAttribute("param-service-url"));
//    */
//  }
//
//  private void printDebugInfo( final RepositoryFileTreeDto tree ) {
//    System.out.println( "FileTreeDto: " + tree );
//    System.out.println( "  - childs: " + tree.getChildren().size() );
//    System.out.println( "FileDto: " + tree.getFile() );
//    System.out.println( "  - Name: " + tree.getFile().getName() );
//    System.out.println( "  - Last-Modified-Date: " + tree.getFile().getLastModifiedDate() );
//    System.out.println( "  - Description: " + tree.getFile().getDescription() );
//    System.out.println( "  - Title: " + tree.getFile().getTitle() );
//  }
//}
