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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DisplayAllFlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelTableContentProducer;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class PageableExcelOutputProcessor extends AbstractTableOutputProcessor
    implements PageableOutputProcessor
{
  private List<PhysicalPageKey> physicalPages;
  private OutputProcessorMetaData metaData;
  private ExcelPrinter printer;
  private FlowSelector flowSelector;

  public PageableExcelOutputProcessor(final Configuration configuration,
                                      final OutputStream outputStream,
                                      final ResourceManager resourceManager)
  {
    if (configuration == null)
    {
      throw new NullPointerException();
    }
    if (outputStream == null)
    {
      throw new NullPointerException();
    }
    if (resourceManager == null)
    {
      throw new NullPointerException();
    }

    this.physicalPages = new ArrayList<PhysicalPageKey>();
    this.metaData = new ExcelOutputProcessorMetaData(ExcelOutputProcessorMetaData.PAGINATION_FULL);
    this.flowSelector = new DisplayAllFlowSelector();

    this.printer = new ExcelPrinter(outputStream, resourceManager);
  }

  public boolean isUseXlsxFormat()
  {
    return printer.isUseXlsxFormat();
  }

  public void setUseXlsxFormat(final boolean useXlsxFormat)
  {
    printer.setUseXlsxFormat(useXlsxFormat);
  }

  public InputStream getTemplateInputStream()
  {
    return printer.getTemplateInputStream();
  }

  public void setTemplateInputStream(final InputStream templateInputStream)
  {
    printer.setTemplateInputStream(templateInputStream);
  }


  protected void processingPagesFinished()
  {
    super.processingPagesFinished();
    physicalPages = Collections.unmodifiableList(physicalPages);
  }

  public int getPhysicalPageCount()
  {
    return physicalPages.size();
  }

  public PhysicalPageKey getPhysicalPage(final int page)
  {
    if (isPaginationFinished() == false)
    {
      throw new IllegalStateException();
    }

    return (PhysicalPageKey) physicalPages.get(page);
  }

  protected LogicalPageKey createLogicalPage(final int width,
                                             final int height)
  {
    final LogicalPageKey key = super.createLogicalPage(width, height);
    for (int h = 0; h < key.getHeight(); h++)
    {
      for (int w = 0; w < key.getWidth(); w++)
      {
        physicalPages.add(key.getPage(w, h));
      }
    }
    return key;
  }

  public OutputProcessorMetaData getMetaData()
  {
    return metaData;
  }

  public FlowSelector getFlowSelector()
  {
    return flowSelector;
  }

  public void setFlowSelector(final FlowSelector flowSelector)
  {
    this.flowSelector = flowSelector;
  }

  protected void processTableContent(final LogicalPageKey logicalPageKey,
                                     final LogicalPageBox logicalPage,
                                     final TableContentProducer contentProducer)
      throws ContentProcessingException
  {
    if (!this.printer.isInitialized())
    {
      this.printer.init(metaData);
    }

    printer.print(logicalPageKey, logicalPage, contentProducer, false);
  }

  protected void processingContentFinished()
  {
    if (isContentGeneratable() == false)
    {
      return;
    }
    if (!this.printer.isInitialized())
    {
      this.printer.init(metaData);
    }

    this.metaData.commit();
    printer.close();
  }

  protected TableContentProducer createTableContentProducer(final SheetLayout layout)
  {
    return new ExcelTableContentProducer(layout, getMetaData());
  }
}
