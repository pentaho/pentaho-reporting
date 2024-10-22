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


package org.pentaho.reporting.libraries.pensol.resources;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.platform.api.engine.PentahoAccessControlException;
import org.pentaho.platform.web.http.api.resources.RepositoryPublishResourceRevealer;
import org.pentaho.platform.web.http.api.resources.services.RepositoryPublishService;

import jakarta.ws.rs.Path;
import java.io.InputStream;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * @author Andrey Khayrutdinov
 */
@Path( "/repo/publish" )
public class TestRepositoryPublishResource extends RepositoryPublishResourceRevealer implements Answer<Void> {

  public static final String RETURN_200 = "Return200";
  public static final String RETURN_401 = "Return403";
  public static final String RETURN_422 = "Return\n422";
  public static final String RETURN_500 = "Return500";

  public TestRepositoryPublishResource() throws Exception {
    repositoryPublishService = mock( RepositoryPublishService.class );
    doAnswer( this ).when( repositoryPublishService )
      .publishFile( anyString(), any( InputStream.class ), anyBoolean() );
  }


  @Override
  public Void answer( InvocationOnMock invocation ) throws Throwable {
    Object[] arguments = invocation.getArguments();
    // path starts with / --> exclude the first char
    String path = ( (String) arguments[ 0 ] ).substring( 1 );
    switch( path ) {
      case RETURN_401:
        throw new PentahoAccessControlException();
      case RETURN_500:
        throw new RuntimeException();
    }
    return null;
  }
}
