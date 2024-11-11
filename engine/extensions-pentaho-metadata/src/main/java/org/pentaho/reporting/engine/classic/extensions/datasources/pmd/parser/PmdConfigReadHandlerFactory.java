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


package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.parser;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractReadHandlerFactory;

/**
 * @author Michael D'Amour
 */
public class PmdConfigReadHandlerFactory extends AbstractReadHandlerFactory<IPmdConfigReadHandler> {
  private static final String PREFIX_SELECTOR =
    "org.pentaho.reporting.engine.classic.core.modules.parser.data.pmd.config-factory-prefix.";

  private static PmdConfigReadHandlerFactory readHandlerFactory;

  public PmdConfigReadHandlerFactory() {
  }

  protected Class<IPmdConfigReadHandler> getTargetClass() {
    return IPmdConfigReadHandler.class;
  }

  public static synchronized PmdConfigReadHandlerFactory getInstance() {
    if ( readHandlerFactory == null ) {
      readHandlerFactory = new PmdConfigReadHandlerFactory();
      readHandlerFactory.configureGlobal( ClassicEngineBoot.getInstance().getGlobalConfig(), PREFIX_SELECTOR );
    }
    return readHandlerFactory;
  }

}
