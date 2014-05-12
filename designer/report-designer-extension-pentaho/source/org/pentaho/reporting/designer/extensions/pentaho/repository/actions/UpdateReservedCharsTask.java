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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.AuthenticationHelper;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishException;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;

public class UpdateReservedCharsTask implements AuthenticatedServerTask
{
  private AuthenticationData loginData;

  public UpdateReservedCharsTask(final AuthenticationData loginData)
  {
    this.loginData = loginData;
  }

  public void setLoginData (AuthenticationData loginData, boolean storeUpdates)
  {
    this.loginData = loginData;
  }

  private HttpClient createHttpClient()
  {
    final HttpClient client = new HttpClient();
    client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    client.getParams().setSoTimeout(WorkspaceSettings.getInstance().getConnectionTimeout() * 1000);
    client.getParams().setAuthenticationPreemptive(true);
    client.getState().setCredentials(AuthScope.ANY,
        AuthenticationHelper.getCredentials(loginData.getUsername(), loginData.getPassword()));
    return client;
  }  
  
  private boolean checkResult(int result) throws PublishException {
    return (result == HttpStatus.SC_OK);
  }
  
  /**
   * When an object implementing interface <code>Runnable</code> is used
   * to create a thread, starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may
   * take any action whatsoever.
   *
   * @see Thread#run()
   */
  public void run()
  {
    HttpClient client = createHttpClient();
    final GetMethod reservedCharactersMethod = new GetMethod(loginData.getUrl() + "/api/repo/files/reservedCharacters");
    reservedCharactersMethod.setFollowRedirects(false);

    final GetMethod reservedCharactersDisplayMethod = new GetMethod(loginData.getUrl() + "/api/repo/files/reservedCharactersDisplay");
    reservedCharactersDisplayMethod.setFollowRedirects(false);
    
    try {
      final int result = client.executeMethod(reservedCharactersMethod);
      if (!checkResult(result))
      {
        throw new PublishException(1);
      }
      PublishUtil.setReservedChars(reservedCharactersMethod.getResponseBodyAsString());
    } 
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
    
    try {
      final int result = client.executeMethod(reservedCharactersDisplayMethod);
      if (!checkResult(result))
      {
        throw new PublishException(1);
      }
      PublishUtil.setReservedCharsDisplay(reservedCharactersDisplayMethod.getResponseBodyAsString());
    } 
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
    
  }
}