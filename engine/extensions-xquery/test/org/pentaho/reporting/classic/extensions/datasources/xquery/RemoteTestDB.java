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

import junit.framework.TestCase;
import org.exist.StandaloneServer;
import org.exist.storage.DBBroker;
import org.exist.xmldb.RemoteCollection;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.modules.CollectionManagementService;

/**
 * Created by IntelliJ IDEA. User: mimil Date: 15 déc. 2008 Time: 18:45:32 To change this template use File |
 * Settings | File Templates.
 *
 * Modification from exist database test classe
 */
public abstract class RemoteTestDB extends TestCase
{

  protected final static String URI = "xmldb:exist://localhost:8088/xmlrpc";
  //protected final static String CHILD_COLLECTION = "unit-testing-collection-Citt\u00E0";
  protected final static String CHILD_COLLECTION = "unit-testing";
  public final static String DB_DRIVER = "org.exist.xmldb.DatabaseImpl";

  private RemoteCollection collection = null;

  private static StandaloneServer server = null;

  protected RemoteTestDB()
  {
  }

  public RemoteTestDB(String name)
  {
    super(name);
  }

  protected void setUpRemoteDatabase()
  {
    try
    {
      //Connect to the DB
      Class cl = Class.forName(DB_DRIVER);
      Database database = (Database) cl.newInstance();
      assertNotNull(database);
      DatabaseManager.registerDatabase(database);
      //Get the root collection...
      Collection rootCollection = DatabaseManager.getCollection(URI + DBBroker.ROOT_COLLECTION, "admin", null);
      assertEquals("xmldb:exist://localhost:8088/xmlrpc" + DBBroker.ROOT_COLLECTION, ((org.exist.xmldb.CollectionImpl) rootCollection).getURI().toString());
      assertNotNull(rootCollection);
      CollectionManagementService cms = (CollectionManagementService) rootCollection.getService(
          "CollectionManagementService", "1.0");
      //Creates the child collection
      Collection childCollection = cms.createCollection(CHILD_COLLECTION);
      assertNotNull(childCollection);
      //... and work from it
      setCollection((RemoteCollection) childCollection);
      assertNotNull(childCollection);
    }
    catch (Exception e)
    {
      fail(e.getMessage());
    }
  }

  protected void initServer()
  {
    try
    {
      if (server == null)
      {
        server = new StandaloneServer();
        if (!server.isStarted())
        {
          try
          {
            System.out.println("Starting standalone server...");
            String[] args = {};
            server.run(args);
            while (!server.isStarted())
            {
              Thread.sleep(1000);
            }
          }
          catch (Exception e)
          {
            boolean rethrow = true;

            if (rethrow) throw e;
          }
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  protected void removeCollection()
  {
    try
    {
      Collection rootCollection = DatabaseManager.getCollection(URI + DBBroker.ROOT_COLLECTION, "admin", null);
      assertNotNull(rootCollection);
      CollectionManagementService cms = (CollectionManagementService) rootCollection.getService(
          "CollectionManagementService", "1.0");
      cms.removeCollection(CHILD_COLLECTION);
    }
    catch (Exception e)
    {
      fail(e.getMessage());
    }
  }

  public RemoteCollection getCollection()
  {
    return collection;
  }

  public void setCollection(RemoteCollection collection)
  {
    this.collection = collection;
  }

  protected String getTestCollectionName()
  {
    return CHILD_COLLECTION;
  }
}

