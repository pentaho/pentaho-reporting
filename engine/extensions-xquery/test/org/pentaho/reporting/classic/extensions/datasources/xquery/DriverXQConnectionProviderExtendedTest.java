/*
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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.classic.extensions.datasources.xquery;

import java.util.Properties;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQItem;
import javax.xml.xquery.XQResultSequence;

import org.pentaho.reporting.engine.classic.extensions.datasources.xquery.DriverXQConnectionProvider;

/**
 *
 */
public class DriverXQConnectionProviderExtendedTest extends RemoteTestDB
{

  private Properties validProperties;
  private static String EXIST_URI = "exist://localhost:8088/db/unit-testing/testresource";

  private final static String TEST_DOC =
      "<?xml version=\'1.0\'?> <test>"
          + "<item id='1' type='alphanum'><price>5.6</price><stock>22</stock></item>"
          + "<item id='2'><price>7.4</price><stock>43</stock></item>"
          + "<item id='3'><price>18.4</price><stock>5</stock></item>"
          + "<item id='4'><price>65.54</price><stock>16</stock></item>"
          + "</test>";

  public void setUp()
  {
    // "ServerType","ServerName","CollectionPath","ServerPort","Username","Password","MaxConnections"
    validProperties = new Properties();
    //validProperties.setProperty("Username", "guest");
    //validProperties.setProperty("Password", "guest");
    validProperties.setProperty("ServerType", "REMOTE");
    validProperties.setProperty("ServerName", "localhost");
    validProperties.setProperty("ServerPort", "8088");
    validProperties.setProperty("MaxConnections", "100");
    validProperties.setProperty("CollectionPath", "/db/unit-testing/testresource");

    try
    {
      //Don't worry about closing the server : the shutdown hook will do the job
      initServer();
      setUpRemoteDatabase();
/*
      //create content
      Resource resource = getCollection().createResource("testresource", "XMLResource");
      assertNotNull(resource);
      assertEquals(getCollection(), resource.getParentCollection());
      resource.setContent(TEST_DOC);
      getCollection().storeResource(resource);
*/
    }
    catch (Exception e)
    {
      fail(e.getMessage());
    }

  }

  public void tearDown()
  {
    try
    {
      removeCollection();
    }
    catch (Exception e)
    {
      fail(e.getMessage());
    }
  }


  public void testConnection()
  {
    final DriverXQConnectionProvider provider = new DriverXQConnectionProvider();
    provider.setXqdatasource("org.exist.xqj.EXistXQDataSource");
    provider.setProperties(validProperties);
    try
    {
      final XQConnection xq = provider.getConnection();
      assertNotNull("Unexpected null XQJ connection", xq);
      //TODO more test
    }
    catch (XQException e)
    {
      e.printStackTrace();
      fail("Failed to connect to a valid XQJ connection");
    }
  }

  public void testSimpleExpression()
  {
    final DriverXQConnectionProvider provider = new DriverXQConnectionProvider();
    provider.setXqdatasource("org.exist.xqj.EXistXQDataSource");
    provider.setProperties(validProperties);
    try
    {
      final XQConnection xq = provider.getConnection();
      assertNotNull("Unexpected null XQJ connection", xq);
      XQExpression expr = xq.createExpression();
      String q = "for $price in //price " +
          "return $price";
      XQResultSequence res = expr.executeQuery(q);
      assertNotNull("Result sequence should not be null", res);
      int i = 0;
      while (res.next())
      {
        i++;
        XQItem it = res.getItem();
        System.out.println("ItemType: " + it.getItemType().getTypeName());
        System.out.println("ItemValue: " + it.getAtomicValue());
      }
      assertEquals("Result should be 4 itmes", 4, i);
    }
    catch (XQException e)
    {
      e.printStackTrace();
      fail("Failed to connect to a valid XQJ connection: " + e.getMessage());
    }

  }

  //TODO  bug in exist
  /*public void testConnectionWrongCredentials()
  {
    final DriverXQConnectionProvider provider = new DriverXQConnectionProvider();
    provider.setXqdatasource("org.exist.xqj.EXistXQDataSource");
    validProperties.setProperty("Password", "haha");
    validProperties.setProperty("Username", "haha");
    provider.setProperties(validProperties);
    try
    {
      final XQConnection xq = provider.getConnection();
      final XQExpression expression = xq.createExpression();
      expression.executeQuery("test");
      fail("Should not me allowed to retrieve connection");
    }
    catch (XQException e)
    {
      e.printStackTrace();
      // success
    }
  }  */
}
