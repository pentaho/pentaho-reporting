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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.CrosstabElement;
import org.pentaho.reporting.engine.classic.core.filter.types.CrosstabElementType;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.SubReportReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

/**
 * Implementation for crosstab element read handler.
 *
 * @author Sulaiman Karmali
 */
public class CrosstabElementReadHandler extends SubReportReadHandler {
  public CrosstabElementReadHandler() throws ParseException {
    super( CrosstabElementType.INSTANCE, CrosstabElement.class );
  }
}
