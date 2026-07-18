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



package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser;

import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DataSourceProvider;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

/**
 * Todo: Document me!
 * <p/>
 * Date: 25.08.2009 Time: 10:06:56
 *
 * @author Thomas Morgner.
 */
public interface DataSourceProviderReadHandler extends XmlReadHandler {
  public DataSourceProvider getProvider();
}
