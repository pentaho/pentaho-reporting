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

package org.pentaho.reporting.engine.classic.core;

import javax.swing.table.TableModel;

public interface CompoundDataFactorySupport extends DataFactory {
  public TableModel queryStatic( String query, DataRow parameters ) throws ReportDataFactoryException;

  public TableModel queryFreeForm( String query, DataRow parameter ) throws ReportDataFactoryException;

  public TableModel queryDesignTimeStructureStatic( String query, DataRow parameters )
    throws ReportDataFactoryException;

  public TableModel queryDesignTimeStructureFreeForm( String query, DataRow parameter )
    throws ReportDataFactoryException;

  public boolean isStaticQueryExecutable( String query, DataRow parameters );

  public boolean isFreeFormQueryExecutable( String query, DataRow parameter );

  public DataFactory getDataFactoryForQuery( final String queryName, final boolean freeform );

}
