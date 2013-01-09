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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.structuretree;

import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 */
public class MasterReportDataTreeModel extends AbstractReportDataTreeModel
{
  private MasterReport masterReportElement;
  private ReportParametersNode reportParametersNode;

  public MasterReportDataTreeModel(final ReportRenderContext masterReportElement)
  {
    super(masterReportElement);
    if (masterReportElement.getReportDefinition() instanceof MasterReport == false)
    {
      throw new NullPointerException();
    }
    this.masterReportElement = (MasterReport) masterReportElement.getReportDefinition();
    this.reportParametersNode = new ReportParametersNode();
  }

  protected ReportParametersNode getReportParametersNode()
  {
    return reportParametersNode;
  }

  public Object getRoot()
  {
    return masterReportElement;
  }

  public Object getChild(final Object parent, final int index)
  {
    if (parent == masterReportElement)
    {
      switch (index)
      {
        case 0:
          return masterReportElement.getDataFactory();
        case 1:
          return getReportFunctionNode();
        case 2:
          return getReportEnvironmentDataRow();
        case 3:
          return reportParametersNode;
        default:
          throw new IndexOutOfBoundsException();
      }
    }
    if (parent == reportParametersNode)
    {
      return masterReportElement.getParameterDefinition().getParameterDefinition(index);
    }
    return super.getChild(parent, index);
  }

  public int getChildCount(final Object parent)
  {
    if (parent == masterReportElement)
    {
      return 4;
    }
    if (parent == reportParametersNode)
    {
      return masterReportElement.getParameterDefinition().getParameterCount();
    }
    return super.getChildCount(parent);
  }

  public boolean isLeaf(final Object node)
  {
    if (node instanceof ParameterDefinitionEntry)
    {
      return true;
    }
    return super.isLeaf(node);
  }

  public int getIndexOfChild(final Object parent, final Object child)
  {
    if (parent == masterReportElement)
    {
      if (child == masterReportElement.getDataFactory())
      {
        return 0;
      }
      if (child == getReportFunctionNode())
      {
        return 1;
      }
      if (child == getReportEnvironmentDataRow())
      {
        return 2;
      }
      if (child == reportParametersNode)
      {
        return 3;
      }
      return -1;
    }
    if (parent == reportParametersNode)
    {
      final ReportParameterDefinition definition = masterReportElement.getParameterDefinition();

      for (int i = 0; i < definition.getParameterCount(); i++)
      {
        final ParameterDefinitionEntry dataFactory = definition.getParameterDefinition(i);
        if (dataFactory == child)
        {
          return i;
        }
      }
      return -1;
    }

    return super.getIndexOfChild(parent, child);
  }
}
