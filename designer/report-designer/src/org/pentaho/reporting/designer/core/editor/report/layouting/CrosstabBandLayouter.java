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

package org.pentaho.reporting.designer.core.editor.report.layouting;

import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.EmptyReportException;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchema;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * A class holding the current layouter state. This class acts as a single point of caching for all re-layouting
 * activities.
 *
 * @author Thomas Morgner
 */
public class CrosstabBandLayouter
{
  private OutputProcessorMetaData metaData;
  private DesignerOutputProcessor outputProcessor;
  private MasterReport report;
  private DesignerExpressionRuntime runtime;

  public CrosstabBandLayouter(final MasterReport report)
  {
    this.report = report;
    this.outputProcessor = new DesignerOutputProcessor();
    this.metaData = outputProcessor.getMetaData();

    final DefaultDataSchema schema = new DefaultDataSchema();
    final DataRow dataRow = new StaticDataRow();
    this.runtime = new DesignerExpressionRuntime(dataRow, schema, report);
  }

  public OutputProcessorMetaData getMetaData()
  {
    return metaData;
  }

  public LogicalPageBox doCrosstabLayout(final CrosstabGroup band)
      throws ReportProcessingException, ContentProcessingException
  {
    try
    {
      final MasterReport report = new MasterReport();
      report.setDataFactory(new TableDataFactory(report.getQuery(), new DefaultTableModel(1, 1)));
      report.setResourceManager(this.report.getResourceManager());
      report.setResourceBundleFactory(this.report.getResourceBundleFactory());
      report.setPageDefinition(this.report.getPageDefinition());
      report.setContentBase(this.report.getContentBase());
      report.setReportEnvironment(this.report.getReportEnvironment());
      final Group group = (Group) band.clone();
      report.setRootGroup(group);

      final DesignerOutputProcessor outputProcessor = new DesignerOutputProcessor();
      final DesignerReportProcessor processor = new DesignerReportProcessor(report, outputProcessor);
      processor.processReport();
      final LogicalPageBox box = outputProcessor.getLogicalPage();
//      ModelPrinter.print(box);
      return box;
    }
    catch (EmptyReportException er)
    {
      DebugLog.log("Empty report", er); // NON-NLS
      return null;
    }
  }
}
