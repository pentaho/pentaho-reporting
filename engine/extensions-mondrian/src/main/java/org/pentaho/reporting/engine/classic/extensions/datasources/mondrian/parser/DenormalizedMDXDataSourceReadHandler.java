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

import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DenormalizedMDXDataFactory;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class DenormalizedMDXDataSourceReadHandler extends AbstractNamedMDXDataSourceReadHandler {
  public DenormalizedMDXDataSourceReadHandler() {
  }

  protected AbstractMDXDataFactory createDataFactory() {
    return new DenormalizedMDXDataFactory();
  }

}
