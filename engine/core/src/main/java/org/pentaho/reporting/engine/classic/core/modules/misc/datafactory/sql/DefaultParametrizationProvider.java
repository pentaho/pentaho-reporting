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


package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import org.pentaho.reporting.engine.classic.core.DataRow;

import java.sql.Connection;

public class DefaultParametrizationProvider implements ParametrizationProvider {
  private SQLParameterLookupParser parser;

  public DefaultParametrizationProvider() {
  }

  public String rewriteQueryForParametrization( final Connection connection, final String query, final DataRow dataRow ) {
    parser = new SQLParameterLookupParser( SimpleSQLReportDataFactory.isExpandArrayParameterNeeded( query ) );
    return parser.translateAndLookup( query, dataRow );
  }

  public String[] getPreparedParameterNames() {
    return parser.getFields();
  }
}
