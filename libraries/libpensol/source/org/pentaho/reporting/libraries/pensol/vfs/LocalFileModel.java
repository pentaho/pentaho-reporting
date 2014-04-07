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

package org.pentaho.reporting.libraries.pensol.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.MessageFormat;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileSystemException;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.pensol.LibPensolBoot;

public class LocalFileModel extends XmlSolutionFileModel
{
  private static final Log logger = LogFactory.getLog(LocalFileModel.class);

  private String url;
  private String username;
  private String password;
  private HttpClient client;

  public LocalFileModel(final String url,
                        final HttpClient client,
                        final String username,
                        final String password)
  {
    if (url == null)
    {
      throw new NullPointerException();
    }
    this.url = url;
    this.username = username;
    this.password = password;
    this.client = client;
    this.client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    this.client.getParams().setParameter(HttpClientParams.MAX_REDIRECTS, Integer.valueOf(10));
    this.client.getParams().setParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, Boolean.TRUE);
    this.client.getParams().setParameter(HttpClientParams.REJECT_RELATIVE_REDIRECT, Boolean.FALSE);
  }

  public void refresh() throws IOException
  {
    getDescriptionEntries().clear();

    final Configuration configuration = LibPensolBoot.getInstance().getGlobalConfig();
    final String service = configuration.getConfigProperty
        ("org.pentaho.reporting.libraries.pensol.web.LoadRepositoryDoc");

    final PostMethod filePost = new PostMethod(url + service);
    logger.debug("Connecting to '" + url + service + '\'');
    filePost.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    if (username != null)
    {
      filePost.addParameter("userid", username);
    }
    if (password != null)
    {
      filePost.addParameter("password", password);
    }

    final int lastStatus = client.executeMethod(filePost);
    if (lastStatus == HttpStatus.SC_UNAUTHORIZED)
    {
      throw new IOException("401: User authentication failed.");
    }
    else if (lastStatus == HttpStatus.SC_NOT_FOUND)
    {
      throw new IOException("404: Repository service not found on server.");
    }
    else if (lastStatus != HttpStatus.SC_OK)
    {
      throw new IOException("Server error: HTTP lastStatus code " + lastStatus);
    }

    final InputStream postResult = filePost.getResponseBodyAsStream();
    try
    {
      setRoot(performParse(postResult));
    }
    finally
    {
      postResult.close();
    }
  }

  /**
   * @noinspection ThrowCaughtLocally
   */
  protected byte[] getDataInternally(final FileInfo fileInfo) throws FileSystemException
  {
    final PostMethod filePost = new PostMethod(fileInfo.getUrl());
    filePost.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    if (username != null)
    {
      filePost.addParameter("userid", username);
    }
    if (password != null)
    {
      filePost.addParameter("password", password);
    }

    try
    {
      final int lastStatus = client.executeMethod(filePost);
      if (lastStatus == HttpStatus.SC_UNAUTHORIZED)
      {
        throw new FileSystemException("401: User authentication failed.");
      }
      else if (lastStatus == HttpStatus.SC_NOT_FOUND)
      {
        throw new FileSystemException("404: Repository service not found on server.");
      }
      else if (lastStatus != HttpStatus.SC_OK)
      {
        throw new FileSystemException("Server error: HTTP lastStatus code " + lastStatus);
      }

      final InputStream postResult = filePost.getResponseBodyAsStream();
      try
      {
        final MemoryByteArrayOutputStream bout = new MemoryByteArrayOutputStream();
        IOUtils.getInstance().copyStreams(postResult, bout);
        return bout.toByteArray();
      }
      finally
      {
        postResult.close();
      }
    }
    catch (FileSystemException ioe)
    {
      throw ioe;
    }
    catch (IOException ioe)
    {
      throw new FileSystemException("Failed", ioe);
    }
  }

  public void createFolder(final FileName file) throws FileSystemException
  {
    final String[] fileName = computeFileNames(file);

    if (fileName.length < 2)
    {
      throw new FileSystemException("Cannot create directory in the root.");
    }

    final String[] parentPath = new String[fileName.length - 1];
    System.arraycopy(fileName, 0, parentPath, 0, parentPath.length);
    final FileInfo fileInfo = lookupNode(parentPath);
    if (fileInfo == null)
    {
      throw new FileSystemException("Cannot locate parent directory.");
    }

    try
    {
      final String solution = fileName[0];
      final String path = buildPath(fileName, 1, fileName.length - 1);
      final String name = fileName[fileName.length - 1];
      String description = getDescriptionEntries().get(file);
      if (description == null)
      {
        description = "";
      }
      final Configuration config = LibPensolBoot.getInstance().getGlobalConfig();
      final String urlMessage =
          config.getConfigProperty("org.pentaho.reporting.libraries.pensol.web.CreateNewFolder");
      final MessageFormat fmt = new MessageFormat(urlMessage);
      final String fullpath = fmt.format(new Object[]{
          URLEncoder.encode(solution, "UTF-8"),
          URLEncoder.encode(path, "UTF-8"),
          URLEncoder.encode(name, "UTF-8"),
          URLEncoder.encode(description, "UTF-8")
      });
      final PostMethod filePost = new PostMethod(url + fullpath);
      if (username != null)
      {
        filePost.addParameter("user", username);
      }
      if (password != null)
      {
        filePost.addParameter("password", password);
      }
      filePost.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

      final int lastStatus = client.executeMethod(filePost);
      if (lastStatus != HttpStatus.SC_OK)
      {
        throw new FileSystemException("Server error: HTTP status code " + lastStatus);
      }
      if (name == null)
      {
        throw new FileSystemException("Error creating folder: Empty name");
      }

      new FileInfo(fileInfo, name, description);
    }
    catch (FileSystemException fse)
    {
      throw fse;
    }
    catch (IOException ioe)
    {
      throw new FileSystemException("Failed", ioe);
    }
  }

  private String buildPath(final String[] fileName, final int index, final int endIndex)
  {
    final StringBuilder b = new StringBuilder(100);
    for (int i = index; i < endIndex; i++)
    {
      if (i != index)
      {
        b.append('/');
      }
      b.append(fileName[i]);
    }
    return b.toString();
  }

  public long getContentSize(final FileName name) throws FileSystemException
  {
    return 0;
  }

  protected void setDataInternally(final FileInfo fileInfo, final byte[] data) throws FileSystemException
  {
    throw new FileSystemException("Not supported");
  }
  
  @Override
  public boolean delete( FileName name ) throws FileSystemException {
	 throw new FileSystemException("Not supported");
  }
}
