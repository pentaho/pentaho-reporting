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


package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractReadHandlerFactory;

public class MondrianConnectionReadHandlerFactory extends AbstractReadHandlerFactory<CubeFileProviderReadHandler> {
  private static final String PREFIX_SELECTOR =
    "org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.cube-factory-prefix.";

  private static MondrianConnectionReadHandlerFactory readHandlerFactory;

  public MondrianConnectionReadHandlerFactory() {
  }

  protected Class<CubeFileProviderReadHandler> getTargetClass() {
    return CubeFileProviderReadHandler.class;
  }

  public static synchronized MondrianConnectionReadHandlerFactory getInstance() {
    if ( readHandlerFactory == null ) {
      readHandlerFactory = new MondrianConnectionReadHandlerFactory();
      readHandlerFactory.configureGlobal( ClassicEngineBoot.getInstance().getGlobalConfig(), PREFIX_SELECTOR );
    }
    return readHandlerFactory;
  }

}
