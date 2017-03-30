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
* Copyright (c) 2017 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;


@RunWith( Parameterized.class )
public class ValidateLoginTaskFastTest {


  public ValidateLoginTaskFastTest( String url, String username, String password, boolean result ) {
    this.url = url;
    this.username = username;
    this.password = password;
    this.result = result;
  }

  private String url;
  private String username;
  private String password;
  private boolean result;
  private String authStab;


  @Parameterized.Parameters
  public static Collection<Object[]> parameters() {
    return Arrays.asList( new Object[][] {
      { "|/sad", "admin", "password", false },
      { "http://localhost:8080/pentaho/", "admin", "password", true },
      { "http://localhost:8080/pentaho", "admin", "password", true },
      { "http://localhost:8080/pentaho", "tiffany", "漢字", true }
    } );
  }


  @Test
  public void validateLoginDataFast() throws Exception {
    final LoginTask loginTask = Mockito.mock( LoginTask.class );
    final AuthenticationData authenticationData = Mockito.mock( AuthenticationData.class );
    Mockito.when( authenticationData.getUrl() ).thenReturn( url );
    Mockito.when( authenticationData.getUsername() ).thenReturn( username );
    Mockito.when( authenticationData.getPassword() ).thenReturn( password );
    Mockito.when( loginTask.getLoginData() ).thenReturn( authenticationData );
    ValidateLoginTask validateLoginTask = Mockito.spy( new ValidateLoginTask( loginTask ) );
    final HttpClient httpClient = Mockito.mock( HttpClient.class );
    final HttpState[] state = new HttpState[ 1 ];
    Mockito.doAnswer( new Answer() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        state[ 0 ] = (HttpState) invocation.getArguments()[ 0 ];
        return null;
      }
    } ).when( httpClient ).setState( Mockito.any( HttpState.class ) );
    Mockito.when( httpClient.executeMethod( Mockito.any( HttpMethod.class ) ) ).then( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        final HttpMethod method = (HttpMethod) invocation.getArguments()[ 0 ];
        UsernamePasswordCredentials credentials =
          (UsernamePasswordCredentials) state[ 0 ].getCredentials( AuthScope.ANY );
        if ( !username.equals( credentials.getUserName() ) || !password.equals( credentials.getPassword() ) ) {
          return 401;
        }
        return 200;
      }
    } );
    Mockito.when( validateLoginTask.getHttpClient() ).thenReturn( httpClient );
    assertEquals( result, validateLoginTask.validateLoginDataFast() );
  }

}
