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

package org.pentaho.reporting.engine.classic.extensions.datasources.cda;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.base.util.StringUtils;


/**
 * Class that implements CDA to be used over HTTP to be used in PRD application
 *
 * @author dduque
 */
public class HttpQueryBackend extends CdaQueryBackend
{
  private static final char DOMAIN_SEPARATOR = '\\';

  private transient HttpClient client;
  private transient volatile GetMethod httpCall;

  public TypedTableModel fetchData(final DataRow dataRow,
                                   final String method,
                                   final Map<String, String> extraParameter) throws ReportDataFactoryException
  {
    final String baseURL = getBaseUrl();
    if (StringUtils.isEmpty(baseURL, true))
    {
      throw new ReportDataFactoryException("Base URL is null");
    }
    final String url = createURL(method, extraParameter);
    try
    {
      final GetMethod httpCall = new GetMethod(url);
      this.httpCall = httpCall;
      final HttpClient client = getHttpClient();
      final int status = client.executeMethod(httpCall);
      if (status != 200)
      {
        throw new ReportDataFactoryException("Failed to retrieve data: " + httpCall.getStatusLine());
      }

      final InputStream responseBody = httpCall.getResponseBodyAsStream();
      return CdaResponseParser.performParse(responseBody);
    }
    catch (UnsupportedEncodingException use)
    {
      throw new ReportDataFactoryException("Failed to encode parameter", use);
    }
    catch (Exception e)
    {
      throw new ReportDataFactoryException("Failed to send request : " + url, e);
    }
    finally
    {
      httpCall = null;
    }
  }

  public void cancelRunningQuery()
  {
    final GetMethod method = this.httpCall;
    if (method != null)
    {
      method.abort();
    }
  }

  protected HttpClient getHttpClient()
  {
    if (client == null)
    {
      client = new HttpClient();
      client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
      client.getParams().setAuthenticationPreemptive(true);
      client.getState().setCredentials(AuthScope.ANY, getCredentials(getUsername(), getPassword()));
    }
    return client;
  }

  public static Credentials getCredentials(final String user,
                                           final String password)
  {
    if (StringUtils.isEmpty(user))
    {
      return null;
    }

    final int domainIdx = user.indexOf(DOMAIN_SEPARATOR);
    if (domainIdx == -1)
    {
      return new UsernamePasswordCredentials(user, password);
    }
    try
    {
      final String domain = user.substring(0, domainIdx);
      final String username = user.substring(domainIdx + 1);
      final String host = InetAddress.getLocalHost().getHostName();
      return new NTCredentials(username, password, host, domain);
    }
    catch (UnknownHostException uhe)
    {
      return new UsernamePasswordCredentials(user, password);
    }
  }

}
