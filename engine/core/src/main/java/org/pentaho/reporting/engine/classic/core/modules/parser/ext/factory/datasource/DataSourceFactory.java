/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource;

import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectDescription;

import java.util.Iterator;

/**
 * A data source factory.
 *
 * @author Thomas Morgner
 */
public interface DataSourceFactory extends ClassFactory {
  /**
   * Returns a data source description.
   *
   * @param name
   *          the name.
   * @return The description.
   */
  public ObjectDescription getDataSourceDescription( String name );

  /**
   * Returns a data source name.
   *
   * @param od
   *          the description.
   * @return The name.
   */
  public String getDataSourceName( ObjectDescription od );

  /**
   * Returns the names of all registered datasources as iterator.
   *
   * @return the registered names.
   */
  public Iterator getRegisteredNames();
}
