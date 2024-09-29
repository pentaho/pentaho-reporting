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

/**
 * Creation-Date: Dec 18, 2006, 1:05:00 PM
 *
 * @author Thomas Morgner
 */
public class SubReportReadHandlerFactory extends AbstractReadHandlerFactory<SubReportReadHandler> {
  private static final String PREFIX_SELECTOR =
      "org.pentaho.reporting.engine.classic.core.modules.parser.sub-report-factory-prefix.";

  private static SubReportReadHandlerFactory readHandlerFactory;

  public static synchronized SubReportReadHandlerFactory getInstance() {
    if ( readHandlerFactory == null ) {
      readHandlerFactory = new SubReportReadHandlerFactory();
      readHandlerFactory.configureGlobal( ClassicEngineBoot.getInstance().getGlobalConfig(), PREFIX_SELECTOR );
    }
    return readHandlerFactory;
  }

  private SubReportReadHandlerFactory() {
  }

  protected Class<SubReportReadHandler> getTargetClass() {
    return SubReportReadHandler.class;
  }
}
