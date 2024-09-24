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

public class ReportElementReadHandlerFactory extends AbstractReadHandlerFactory<ReportElementReadHandler> {
  private static final String PREFIX_SELECTOR =
      "org.pentaho.reporting.engine.classic.core.modules.parser.report-element-factory-prefix.";

  private static ReportElementReadHandlerFactory readHandlerFactory;

  public static synchronized ReportElementReadHandlerFactory getInstance() {
    if ( readHandlerFactory == null ) {
      readHandlerFactory = new ReportElementReadHandlerFactory();
      readHandlerFactory.configureGlobal( ClassicEngineBoot.getInstance().getGlobalConfig(), PREFIX_SELECTOR );
    }
    return readHandlerFactory;
  }

  private ReportElementReadHandlerFactory() {
  }

  protected Class<ReportElementReadHandler> getTargetClass() {
    return ReportElementReadHandler.class;
  }
}
