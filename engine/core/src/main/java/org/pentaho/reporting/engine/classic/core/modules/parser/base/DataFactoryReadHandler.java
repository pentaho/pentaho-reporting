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


package org.pentaho.reporting.engine.classic.core.modules.parser.base;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

/**
 * Creation-Date: Jan 15, 2007, 6:51:41 PM
 *
 * @author Thomas Morgner
 */
public interface DataFactoryReadHandler extends XmlReadHandler {
  public DataFactory getDataFactory();

}
