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

package org.pentaho.reporting.libraries.pensol;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.List;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import junit.framework.TestCase;
import org.apache.commons.vfs.FileSystemException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;
import org.pentaho.platform.repository2.unified.webservices.RepositoryFileTreeDto;

public class JCRRepositoryTest extends TestCase
{

  public static final String API_REPO_FILES_TREE = "/api/repo/files/tree?depth=-1&filter=*&showHidden=false&filter=*";
  public static final String FILES_CHILDREN = "/api/repo/files/children?depth=-1&filter=*&showHidden=false";
  public static final String url = "http://localhost:8080/pentaho";

  public JCRRepositoryTest()
  {
  }

  public JCRRepositoryTest(final String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    LibPensolBoot.getInstance().start();
  }

  public void testJCRRepository() throws FileSystemException, IOException
  {
    if (GraphicsEnvironment.isHeadless())
    {
      return;
    }


    Client client = getClient();

    final WebResource resource = client.resource(url + API_REPO_FILES_TREE);

    final RepositoryFileTreeDto tree = resource.path("").accept(MediaType.APPLICATION_XML_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).get(RepositoryFileTreeDto.class);

    printDebugInfo(tree);

    final List<RepositoryFileTreeDto> children = tree.getChildren();
    for (int i = 0; i < children.size(); i++)
    {
      final RepositoryFileTreeDto child = children.get(i);
      printDebugInfo(child);
    }
  }

  public void test2JCRRepository() throws FileSystemException, IOException
  {
    if (GraphicsEnvironment.isHeadless())
    {
      return;
    }


    Client client = getClient();

    final WebResource resource = client.resource(url + FILES_CHILDREN);

    final ClientResponse response = resource.path("").accept(MediaType.TEXT_PLAIN_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

    ObjectMapper mapper = new ObjectMapper().configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    //List<RepositoryFileTreeDto> tree = mapper.readValue(response.getEntityInputStream(), new TypeReference<List<RepositoryFileTreeDto>>(){});
    System.out.println(response.getEntity(String.class));
  }


  private Client getClient()
  {
    final ClientConfig config = new DefaultClientConfig();
    config.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
    Client client = Client.create(config);
    client.addFilter(new HTTPBasicAuthFilter("admin", "password"));
    return client;
  }

  private void printDebugInfo(final RepositoryFileTreeDto tree)
  {
    System.out.println("FileTreeDto: " + tree.getClass());
    System.out.println("  - childs: " + tree.getChildren().size());
    System.out.println("FileDto: " + tree.getFile());
    System.out.println("  - Name: " + tree.getFile().getName());
    System.out.println("  - Last-Modified-Date: " + tree.getFile().getLastModifiedDate());
    System.out.println("  - Description: " + tree.getFile().getDescription());
    System.out.println("  - Title: " + tree.getFile().getTitle());
  }
}
