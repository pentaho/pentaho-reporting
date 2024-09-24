/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
