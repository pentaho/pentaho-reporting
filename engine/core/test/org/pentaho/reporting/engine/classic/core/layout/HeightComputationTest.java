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

package org.pentaho.reporting.engine.classic.core.layout;

import java.awt.print.PageFormat;
import java.net.URL;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class HeightComputationTest extends TestCase
{
  public HeightComputationTest()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testNestedRows() throws Exception
  {
    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition(new SimplePageDefinition(new PageFormat()));
    basereport.getReportConfiguration().setConfigProperty(ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "false");

    final URL target = LayoutTest.class.getResource("layout-matrix.xml");
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly(target, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();
    report.getStyle().setStyleProperty(TextStyleKeys.WORDBREAK, true);

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand
        (basereport, report.getReportHeader(), true, false);
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    new ValidateRunner().startValidation(logicalPageBox);
  }

  public void testNestedRowsComplex() throws Exception
  {
    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition(new SimplePageDefinition(new PageFormat()));
    basereport.getReportConfiguration().setConfigProperty(ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true");

    final URL target = LayoutTest.class.getResource("layout-matrix.xml");
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly(target, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();
    report.getStyle().setStyleProperty(TextStyleKeys.WORDBREAK, true);

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand
        (basereport, report.getReportHeader(), true, false);
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    new ValidateRunner().startValidation(logicalPageBox);
  }

  private static class ValidateRunner extends IterateStructuralProcessStep
  {
    protected void processOtherNode(final RenderNode node)
    {
      if (node instanceof RenderableText)
      {
        assertEquals("Text height=8000", StrictGeomUtility.toInternalValue(8), node.getCachedHeight());
      }
      super.processOtherNode(node);
    }

    protected boolean startBox(final RenderBox box)
    {
      final String name = box.getName();
      if (name == null)
      {
        return true;
      }
      if (box instanceof ParagraphRenderBox)
      {
        assertNotNull("Have at most one child", box.getFirstChild());
        assertNotNull("Have at most one child", box.getLastChild());
        assertSame("Have at most one child", box.getFirstChild(), box.getLastChild());
      }

      return true;
    }

    protected boolean startCanvasBox(final CanvasRenderBox box)
    {
      return startBox(box);
    }

    protected boolean startBlockBox(final BlockRenderBox box)
    {
      return startBox(box);
    }

    protected boolean startInlineBox(final InlineRenderBox box)
    {
      return startBox(box);
    }

    protected boolean startRowBox(final RenderBox box)
    {
      return startBox(box);
    }

    protected void processParagraphChilds(final ParagraphRenderBox box)
    {
      processBoxChilds(box);
    }

    public void startValidation(final LogicalPageBox logicalPageBox)
    {
      startProcessing(logicalPageBox);
    }
  }


}
