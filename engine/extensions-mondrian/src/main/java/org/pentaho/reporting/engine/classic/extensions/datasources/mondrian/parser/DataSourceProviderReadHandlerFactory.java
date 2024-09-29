/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractReadHandlerFactory;

public class DataSourceProviderReadHandlerFactory extends AbstractReadHandlerFactory<DataSourceProviderReadHandler> {
  private static final String PREFIX_SELECTOR =
    "org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.datasource-factory-prefix.";

  private static DataSourceProviderReadHandlerFactory readHandlerFactory;

  public DataSourceProviderReadHandlerFactory() {
  }

  protected Class<DataSourceProviderReadHandler> getTargetClass() {
    return DataSourceProviderReadHandler.class;
  }

  public static synchronized DataSourceProviderReadHandlerFactory getInstance() {
    if ( readHandlerFactory == null ) {
      readHandlerFactory = new DataSourceProviderReadHandlerFactory();
      readHandlerFactory.configureGlobal( ClassicEngineBoot.getInstance().getGlobalConfig(), PREFIX_SELECTOR );
    }
    return readHandlerFactory;
  }

}
