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

package org.pentaho.reporting.engine.classic.core.bugs;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.OutputStream;
import java.net.URL;

import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PhysicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal.PdfDocumentWriter;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal.PdfLogicalPageDrawable;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal.PdfOutputProcessorMetaData;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class Prd4626Test extends TestCase
{
  public Prd4626Test()
  {
  }

  public void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }


  public void testBugExists() throws Exception
  {
    final URL resource = getClass().getResource("Prd-4626-2.prpt");
    assertNotNull(resource);

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource parsed = mgr.createDirectly(resource, MasterReport.class);
    final MasterReport report = (MasterReport) parsed.getResource();

    final PdfOutputProcessor outputProcessor =
        new TestPdfOutputProcessor(report.getConfiguration(), new NullOutputStream());
    final PageableReportProcessor reportProcessor = new PageableReportProcessor(report, outputProcessor);
    reportProcessor.processReport();
  }

  private static class TestPdfOutputProcessor extends PdfOutputProcessor
  {
    private TestPdfOutputProcessor(final Configuration configuration,
                                   final OutputStream outputStream)
    {
      super(configuration, outputStream);
    }

    protected void processLogicalPage(final LogicalPageKey key,
                                      final LogicalPageBox logicalPage) throws ContentProcessingException
    {
      if (key.getPosition() >= 4)
      {
        Assert.fail("More than 4 pages");
      }
      super.processLogicalPage(key, logicalPage);
    }

    protected PdfDocumentWriter createPdfDocumentWriter()
    {
      return new TestPdfDocumentWriter((PdfOutputProcessorMetaData) getMetaData(), getOutputStream(), getResourceManager());
    }
  }

  private static class TestPdfDocumentWriter extends PdfDocumentWriter
  {
    private TestPdfDocumentWriter(final PdfOutputProcessorMetaData metaData,
                                  final OutputStream out,
                                  final ResourceManager resourceManager)
    {
      super(metaData, out, resourceManager);
    }

    protected PdfLogicalPageDrawable createLogicalPageDrawable(final LogicalPageBox logicalPage,
                                                               final PhysicalPageBox page)
    {
      final PdfLogicalPageDrawable drawable = new TestPdfLogicalPageDrawable(getWriter(), getImageCache(), getVersion());
      drawable.init(logicalPage, getMetaData(), getResourceManager(), page);
      return drawable;
    }
  }

  private static class TestPdfLogicalPageDrawable extends PdfLogicalPageDrawable
  {
    private Rectangle2D area;

    private TestPdfLogicalPageDrawable(final PdfWriter writer,
                                       final LFUMap<ResourceKey, Image> imageCache, final char version)
    {
      super(writer, imageCache, version);
    }

    /**
     * Draws the object.
     *
     * @param graphics the graphics device.
     * @param area     the area inside which the object should be drawn.
     */
    public void draw(final Graphics2D graphics, final Rectangle2D area)
    {
      this.area = area;
      super.draw(graphics, area);
    }

    protected void drawText(final RenderableText renderableText, final long contentX2)
    {
      if (area.getY() > 0)
      {
        if (renderableText.getY() == 0)
        {
          Assert.fail("Not allowed to print text that is outside of the printable area.");
        }
      }
      super.drawText(renderableText, contentX2);
    }
  }
}
