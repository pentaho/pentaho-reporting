/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.libraries.pensol.resources;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.platform.api.engine.PentahoAccessControlException;
import org.pentaho.platform.web.http.api.resources.RepositoryPublishResourceRevealer;
import org.pentaho.platform.web.http.api.resources.services.RepositoryPublishService;

import javax.ws.rs.Path;
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
