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


package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.parser;

import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.SimpleBandedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;

public class SimpleBandedMDXDataSourceReadHandler extends AbstractMDXDataSourceReadHandler {

  public SimpleBandedMDXDataSourceReadHandler() {
  }

  protected AbstractMDXDataFactory createDataFactory( final OlapConnectionProvider connectionProvider ) {
    return new SimpleBandedMDXDataFactory( connectionProvider );
  }

}
