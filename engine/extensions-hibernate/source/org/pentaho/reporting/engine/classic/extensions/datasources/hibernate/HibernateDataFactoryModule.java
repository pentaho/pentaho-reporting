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

package org.pentaho.reporting.engine.classic.extensions.datasources.hibernate;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryXmlResourceFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.parser.DefaultSessionProviderReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.parser.HibernateDataSourceReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.parser.HibernateResourceXmlFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.parser.SessionProviderReadHandlerFactory;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * Creation-Date: Jan 22, 2007, 2:10:39 PM
 *
 * @author Thomas Morgner
 */
public class HibernateDataFactoryModule extends AbstractModule
{
  public static final String NAMESPACE = "http://jfreereport.sourceforge.net/namespaces/datasources/hibernate";
  public static final String TAG_DEF_PREFIX = "org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.tag-def.";

  public HibernateDataFactoryModule() throws ModuleInitializeException
  {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup
   * operations. This method is called only once in a modules lifetime. If the
   * initializing cannot be completed, throw a ModuleInitializeException to
   * indicate the error,. The module will not be available to the system.
   *
   * @param subSystem the subSystem.
   * @throws ModuleInitializeException
   *          if an error ocurred while initializing the module.
   */
  public void initialize(final SubSystem subSystem) throws ModuleInitializeException
  {
    DataFactoryXmlResourceFactory.register(HibernateResourceXmlFactoryModule.class);

    DataFactoryReadHandlerFactory.getInstance().setElementHandler(NAMESPACE, "hibernate-datasource", HibernateDataSourceReadHandler.class);
    SessionProviderReadHandlerFactory.getInstance().setElementHandler(NAMESPACE, "connection", DefaultSessionProviderReadHandler.class);
    
    ElementMetaDataParser.initializeOptionalDataFactoryMetaData
        ("org/pentaho/reporting/engine/classic/extensions/datasources/hibernate/meta-datafactory.xml");
  }
}