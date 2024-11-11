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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportElementReadHandler;

/**
 * A tagging interface to mark XmlReadHandler which return an report-element.
 *
 * @author Thomas Morgner
 */
public interface ElementReadHandler extends ReportElementReadHandler {
  public Element getElement();
}
