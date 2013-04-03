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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public abstract class AbstractDataFactory implements DataFactory, Cloneable
{
  private transient Configuration configuration;
  private transient ResourceManager resourceManager;
  private transient ResourceKey contextKey;
  private transient ResourceBundleFactory resourceBundleFactory;
  private transient DataFactoryContext dataFactoryContext;
  private transient Locale locale;

  public AbstractDataFactory()
  {
    resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    locale = Locale.getDefault();
  }

  public void cancelRunningQuery()
  {

  }

  protected int calculateQueryLimit(final DataRow parameters)
  {
    final Object queryLimit = parameters.get(DataFactory.QUERY_LIMIT);
    if (queryLimit instanceof Number)
    {
      final Number i = (Number) queryLimit;
      return i.intValue();
    }
    return -1;
  }

  protected int calculateQueryTimeOut(final DataRow parameters)
  {
    final Object queryTimeOut = parameters.get(DataFactory.QUERY_TIMEOUT);
    if (queryTimeOut instanceof Number)
    {
      final Number i = (Number) queryTimeOut;
      return i.intValue();
    }
    return -1;
  }

  public void initialize(final DataFactoryContext dataFactoryContext) throws ReportDataFactoryException
  {
    if (dataFactoryContext == null)
    {
      throw new NullPointerException();
    }
    this.dataFactoryContext = dataFactoryContext;
    this.configuration = dataFactoryContext.getConfiguration();
    this.resourceBundleFactory = dataFactoryContext.getResourceBundleFactory();
    this.resourceManager = dataFactoryContext.getResourceManager();
    this.contextKey = dataFactoryContext.getContextKey();
    this.locale = resourceBundleFactory.getLocale();
    if (locale == null)
    {
      locale = Locale.getDefault();
    }
  }

  public Locale getLocale()
  {
    return locale;
  }

  public DataFactoryContext getDataFactoryContext()
  {
    return dataFactoryContext;
  }

  public Configuration getConfiguration()
  {
    return configuration;
  }

  public ResourceManager getResourceManager()
  {
    return resourceManager;
  }

  public ResourceKey getContextKey()
  {
    return contextKey;
  }

  public ResourceBundleFactory getResourceBundleFactory()
  {
    return resourceBundleFactory;
  }

  public DataFactory clone()
  {
    try
    {
      return (DataFactory) super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public DataFactory derive()
  {
    return clone();
  }
  
  public DataFactoryMetaData getMetaData()
  {
    return DataFactoryRegistry.getInstance().getMetaData(getClass().getName());
  }
}
