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

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls;

import java.io.OutputStream;

import org.pentaho.reporting.engine.classic.core.AbstractReportProcessTask;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.NameGenerator;

public class StreamXExcelReportProcessTask extends AbstractReportProcessTask
{
  public StreamXExcelReportProcessTask()
  {
  }

  /**
   * @noinspection ThrowableInstanceNeverThrown
   */
  public void run()
  {
    if (isValid() == false)
    {
      setError(new ReportProcessingException("Error: The task is not configured properly."));
      return;
    }

    setError(null);
    try
    {
      final MasterReport masterReport = getReport();
      final Configuration configuration = masterReport.getConfiguration();

      final ContentLocation contentLocation = getBodyContentLocation();
      final NameGenerator nameGenerator = getBodyNameGenerator();
      final ContentItem contentItem =
          contentLocation.createItem(nameGenerator.generateName(null, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
      final OutputStream outputStream = contentItem.getOutputStream();

      try
      {
        final StreamExcelOutputProcessor outputProcessor =
            new StreamExcelOutputProcessor(configuration, outputStream, masterReport.getResourceManager());
        outputProcessor.setUseXlsxFormat(true);
        final StreamReportProcessor streamReportProcessor =
            new StreamReportProcessor(masterReport, outputProcessor);
        try
        {
          final ReportProgressListener[] progressListeners = getReportProgressListeners();
          for (int i = 0; i < progressListeners.length; i++)
          {
            final ReportProgressListener listener = progressListeners[i];
            streamReportProcessor.addReportProgressListener(listener);
          }
          streamReportProcessor.processReport();
        }
        finally
        {
          streamReportProcessor.close();
        }
      }
      finally
      {
        outputStream.close();
      }
    }
    catch (Throwable e)
    {
      setError(e);
    }
  }

  public String getReportMimeType()
  {
    return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  }
}
