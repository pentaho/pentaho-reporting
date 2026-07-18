/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.parser;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractReadHandlerFactory;

/**
 * Creation-Date: Dec 17, 2006, 8:58:11 PM
 *
 * @author Thomas Morgner
 */
public class OlapConnectionReadHandlerFactory extends AbstractReadHandlerFactory<OlapConnectionReadHandler> {
  private static final String PREFIX_SELECTOR =
    "org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connection-factory-prefix.";

  private static OlapConnectionReadHandlerFactory readHandlerFactory;

  public OlapConnectionReadHandlerFactory() {
  }

  protected Class<OlapConnectionReadHandler> getTargetClass() {
    return OlapConnectionReadHandler.class;
  }

  public static synchronized OlapConnectionReadHandlerFactory getInstance() {
    if ( readHandlerFactory == null ) {
      readHandlerFactory = new OlapConnectionReadHandlerFactory();
      readHandlerFactory.configureGlobal( ClassicEngineBoot.getInstance().getGlobalConfig(), PREFIX_SELECTOR );
    }
    return readHandlerFactory;
  }

}
