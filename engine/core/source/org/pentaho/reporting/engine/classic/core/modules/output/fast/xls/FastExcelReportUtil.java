/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast.xls;

import java.io.IOException;
import java.io.OutputStream;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.validator.ReportStructureValidator;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;

public class FastExcelReportUtil
{
  private FastExcelReportUtil()
  {
  }

  public static void processXls(MasterReport report, OutputStream out) throws ReportProcessingException, IOException
  {
    ReportStructureValidator validator = new ReportStructureValidator();
    if (validator.isValidForFastProcessing(report) == false)
    {
      ExcelReportUtil.createXLS(report, out);
      return;
    }

    final FastExcelExportProcessor reportProcessor = new FastExcelExportProcessor(report, out, false);
    reportProcessor.processReport();
    reportProcessor.close();
    out.flush();
  }

  public static void processXlsx(MasterReport report, OutputStream out) throws ReportProcessingException, IOException
  {
    ReportStructureValidator validator = new ReportStructureValidator();
    if (validator.isValidForFastProcessing(report) == false)
    {
      ExcelReportUtil.createXLS(report, out);
      return;
    }

    final FastExcelExportProcessor reportProcessor = new FastExcelExportProcessor(report, out, true);
    reportProcessor.processReport();
    reportProcessor.close();
    out.flush();
  }
}
