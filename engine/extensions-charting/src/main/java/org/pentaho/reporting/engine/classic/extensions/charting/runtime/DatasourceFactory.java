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


package org.pentaho.reporting.engine.classic.extensions.charting.runtime;

/**
 * Exists for compatibility with CCC.
 *
 * @author pdpi
 */
public class DatasourceFactory {
  public DatasourceFactory() {
  }

  public Datasource createDatasource( final String type ) {
    return new DefaultDatasource();
  }
}
