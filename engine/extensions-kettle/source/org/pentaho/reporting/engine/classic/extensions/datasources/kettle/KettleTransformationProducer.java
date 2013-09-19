/*
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
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import java.io.Serializable;
import javax.swing.table.TableModel;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public interface KettleTransformationProducer extends Cloneable, Serializable
{
  public TableModel performQuery(final DataRow parameters, int queryLimit,
                                 final ResourceManager resourceManager,
                                 final ResourceKey resourceKey)
      throws KettleException, ReportDataFactoryException;

  public Object clone();

  public void cancelQuery();

  public Object getQueryHash(final ResourceManager resourceManager,
                             final ResourceKey resourceKey);

  public String[] getReferencedFields();

  public String getTransformationFile();

  public String getStepName();

  public String[] getDefinedArgumentNames();

  public ParameterMapping[] getDefinedVariableNames();

}
