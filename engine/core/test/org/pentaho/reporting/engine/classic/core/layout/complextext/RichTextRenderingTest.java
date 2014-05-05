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

package org.pentaho.reporting.engine.classic.core.layout.complextext;

import java.awt.font.TextAttribute;
import java.net.URL;
import java.text.AttributedString;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.process.ParagraphLineBreakStep;
import org.pentaho.reporting.engine.classic.core.layout.process.text.RichTextSpec;
import org.pentaho.reporting.engine.classic.core.style.TextDirection;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class RichTextRenderingTest
{
  public RichTextRenderingTest()
  {
  }

  @Before
  public void setUp()
  {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testGraphics2D() throws Exception
  {
    URL resource = getClass().getResource("rich-text-sample1.prpt");
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly(resource, MasterReport.class).getResource();

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage(report, 0);
    RenderNode first = MatchFactory.findElementByName(logicalPageBox, "first");
    assertNotNull(first);
    assertTrue(first.getHeight() > StrictGeomUtility.toInternalValue(20));

    RenderNode[] elementsByNodeType = MatchFactory.findElementsByNodeType(first, LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT);
    assertEquals(1, elementsByNodeType.length);
    assertTrue(elementsByNodeType[0] instanceof RenderableComplexText);

    RenderableComplexText text = (RenderableComplexText) elementsByNodeType[0];
    RichTextSpec richText = text.getRichText();
    assertEquals(4, richText.getStyleChunks().size());
    assertEquals("Label@LabelLabel", richText.getText());

    RenderNode second = MatchFactory.findElementByName(logicalPageBox, "second");
    assertTrue(second instanceof ParagraphRenderBox);
    ParagraphRenderBox p = (ParagraphRenderBox) second;
    assertTrue(p.getPool().getFirstChild().getNext() instanceof RenderableReplacedContentBox);

    assertTrue(second.getHeight() > StrictGeomUtility.toInternalValue(20));
    RenderNode[] secondText = MatchFactory.findElementsByNodeType(second, LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT);
    assertEquals(12, secondText.length);
    assertTrue(secondText[0] instanceof RenderableComplexText);
  }

  @Test
  public void testLineBreaking() throws Exception
  {
    URL resource = getClass().getResource("rich-text-sample1.prpt");
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly(resource, MasterReport.class).getResource();
    report.getReportHeader().removeElement(report.getReportHeader().getElement(0));
    report.getReportHeader().getElement(0).getStyle().setStyleProperty(TextStyleKeys.DIRECTION, TextDirection.RTL);

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage(report, 0);

    RenderNode second = MatchFactory.findElementByName(logicalPageBox, "second");
    assertTrue(second instanceof ParagraphRenderBox);
    ParagraphRenderBox p = (ParagraphRenderBox) second;
    assertTrue(p.getPool().getFirstChild().getNext() instanceof RenderableReplacedContentBox);

    ParagraphLineBreakStep step = new ParagraphLineBreakStep();
    step.compute(logicalPageBox);

    RenderNode[] elementsByNodeType = MatchFactory.findElementsByNodeType(p, LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT);
    assertContainsImage(elementsByNodeType);
    RenderableComplexText t = (RenderableComplexText) elementsByNodeType[0];
    AttributedString attributedString = t.getRichText().getAttributedString();
    assertEquals(TextAttribute.RUN_DIRECTION_RTL, attributedString.getIterator().getAttribute(TextAttribute.RUN_DIRECTION));

  }

  private void assertContainsImage(RenderNode[] elementsByNodeType)
  {
    for (RenderNode renderNode : elementsByNodeType)
    {
      RenderableComplexText t = (RenderableComplexText) renderNode;
      RichTextSpec richText = t.getRichText();
      for (RichTextSpec.StyledChunk styledChunk : richText.getStyleChunks())
      {
        if (styledChunk.getAttributes().containsKey(TextAttribute.CHAR_REPLACEMENT))
        {
          return;
        }
      }

    }
    Assert.fail();
  }
}
