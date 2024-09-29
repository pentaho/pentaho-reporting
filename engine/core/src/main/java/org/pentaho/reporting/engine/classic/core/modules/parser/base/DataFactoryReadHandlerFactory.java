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


package org.pentaho.reporting.engine.classic.core.modules.parser.base;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractReadHandlerFactory;

public class DataFactoryReadHandlerFactory extends AbstractReadHandlerFactory<DataFactoryReadHandler> {
  private static final String PREFIX_SELECTOR =
      "org.pentaho.reporting.engine.classic.core.modules.parser.data-factory-prefix.";

  private static DataFactoryReadHandlerFactory readHandlerFactory;

  public static synchronized DataFactoryReadHandlerFactory getInstance() {
    if ( DataFactoryReadHandlerFactory.readHandlerFactory == null ) {
      readHandlerFactory = new DataFactoryReadHandlerFactory();
      readHandlerFactory.configureGlobal( ClassicEngineBoot.getInstance().getGlobalConfig(), PREFIX_SELECTOR );
    }
    return DataFactoryReadHandlerFactory.readHandlerFactory;
  }

  private DataFactoryReadHandlerFactory() {
  }

  protected Class<DataFactoryReadHandler> getTargetClass() {
    return DataFactoryReadHandler.class;
  }
}
