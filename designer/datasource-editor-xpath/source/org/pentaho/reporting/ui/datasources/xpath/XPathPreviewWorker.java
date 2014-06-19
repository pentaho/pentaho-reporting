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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.ui.datasources.xpath;

import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.extensions.datasources.xpath.XPathDataFactory;
import org.pentaho.reporting.libraries.designtime.swing.background.CancelEvent;
import org.pentaho.reporting.libraries.designtime.swing.background.PreviewWorker;

public class XPathPreviewWorker implements PreviewWorker
{
  private XPathDataFactory dataFactory;
  private TableModel resultTableModel;
  private ReportDataFactoryException exception;
  private String query;

  public XPathPreviewWorker(final XPathDataFactory dataFactory,
                            final String query)
  {
    if (dataFactory == null)
    {
      throw new NullPointerException();
    }

    this.query = query;
    this.dataFactory = dataFactory;
  }

  public ReportDataFactoryException getException()
  {
    return exception;
  }

  public TableModel getResultTableModel()
  {
    return resultTableModel;
  }

  public void close()
  {
  }

  /**
   * Requests that the thread stop processing as soon as possible.
   */
  public void cancelProcessing(final CancelEvent event)
  {
    dataFactory.cancelRunningQuery();
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used
   * to create a thread, starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may
   * take any action whatsoever.
   *
   * @see Thread#run()
   */
  public void run()
  {
    try
    {
      resultTableModel = dataFactory.queryData
          (query, new ParameterDataRow());
    }
    catch (ReportDataFactoryException e)
    {
      exception = e;
    }
    finally
    {
      dataFactory.close();
    }
  }
}
