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


package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.formula.parser.ParseException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.TableModel;
import java.io.Serializable;

public interface KettleTransformationProducer extends Cloneable, Serializable {
  public TableModel performQuery( final DataRow parameters,
                                  int queryLimit,
                                  final DataFactoryContext context )
    throws KettleException, ReportDataFactoryException;

  public Object clone();

  public void cancelQuery();

  public Object getQueryHash( final ResourceManager resourceManager,
                              final ResourceKey resourceKey );

  public String[] getReferencedFields() throws ParseException;

  public String getTransformationFile();

  public String getStepName();

  public TableModel queryDesignTimeStructure( DataRow parameter,
                                              DataFactoryContext dataFactoryContext )
    throws ReportDataFactoryException, KettleException;
}
