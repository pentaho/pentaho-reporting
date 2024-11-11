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

import java.io.Serializable;
import java.sql.Connection;

public interface ParametrizationProvider extends Serializable {
  public String rewriteQueryForParametrization( Connection connection, String query, DataRow parameters );

  public String[] getPreparedParameterNames();
}
