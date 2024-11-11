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
package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

public final class DataBaseConnectionAttributes {
  private DataBaseConnectionAttributes() {

  }

  public static final String JDBC_POOL = "JDBC_POOL"; //$NON-NLS-1$
  public static final String JDBC_DATASOURCE = "DataSource"; //$NON-NLS-1$
  public static final String MAX_ACTIVE_KEY = "POOLING_maxActive";
  public static final String MAX_IDLE_KEY = "POOLING_maxIdle";
  public static final String MAX_WAIT_KEY = "POOLING_maxWait";
  public static final String QUERY_KEY = "query";

}
