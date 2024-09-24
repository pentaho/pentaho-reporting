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

import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

/**
 * Creation-Date: Dec 18, 2006, 1:03:58 PM
 *
 * @author Thomas Morgner
 */
public interface SubReportReadHandler extends XmlReadHandler {
  public SubReport getSubReport();

  public void setDisableRootTagWarning( final boolean disableWarning );
}
