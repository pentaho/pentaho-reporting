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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.util.StaticChunkWidthUpdate;
import org.pentaho.reporting.engine.classic.core.layout.process.util.StaticChunkWidthUpdatePool;
import org.pentaho.reporting.engine.classic.core.layout.process.util.StaticRootChunkWidthUpdate;
import org.pentaho.reporting.engine.classic.core.layout.text.ExtendedBaselineInfo;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

/**
 * Computes the width for all elements. This uses the CSS algorithm, percentages are resolved against the parent's
 * already known width.
 *
 * @author Thomas Morgner
 */
public final class ComputeStaticPropertiesProcessStep extends IterateSimpleStructureProcessStep
{

  // Set the maximum height to an incredibly high value. This is now 2^43 micropoints or more than
  // 3000 kilometers. Please call me directly at any time if you need more space for printing.
  public static final long MAX_AUTO = StrictGeomUtility.toInternalValue(0x80000000000L);
  private static final StaticRootChunkWidthUpdate ROOT = new StaticRootChunkWidthUpdate();

  private OutputProcessorMetaData metaData;
  private boolean overflowXSupported;
  private boolean overflowYSupported;
  private boolean widowsEnabled;
  private StaticChunkWidthUpdate chunkWidthUpdate;
  private StaticChunkWidthUpdatePool chunkWidthUpdatePool;
  private boolean widowOrphanDefinitionsEncountered;

  public ComputeStaticPropertiesProcessStep()
  {
    chunkWidthUpdatePool = new StaticChunkWidthUpdatePool();
  }

  public void initialize(final OutputProcessorMetaData metaData,
                         final ProcessingContext processingContext)
  {
    this.metaData = metaData;
    this.overflowXSupported = metaData.isFeatureSupported(OutputProcessorFeature.ASSUME_OVERFLOW_X);
    this.overflowYSupported = metaData.isFeatureSupported(OutputProcessorFeature.ASSUME_OVERFLOW_Y);
    this.widowsEnabled = !ClassicEngineBoot.isEnforceCompatibilityFor(processingContext.getCompatibilityLevel(), 3, 8);
    this.widowOrphanDefinitionsEncountered = false;
  }

  public boolean isWidowOrphanDefinitionsEncountered()
  {
    return widowOrphanDefinitionsEncountered;
  }

  public void compute(final LogicalPageBox root)
  {
    this.chunkWidthUpdate = ROOT;
    startProcessing(root);
    this.chunkWidthUpdate = null;
  }

  protected boolean startBox(final RenderBox box)
  {
    final long changeTracker = box.getChangeTracker();
    final long age = box.getStaticBoxPropertiesAge();
    if (changeTracker == age)
    {
      return false;
    }

    this.chunkWidthUpdate = createChunkWidthUpdate(box);

    updateStaticProperties(box);
    computeWidowOrphanIndicator(box);

    if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
    {
      processParagraphChilds((ParagraphRenderBox) box);
      return false;
    }

    return true;
  }

  protected void finishBox(final RenderBox box)
  {
    final long changeTracker = box.getChangeTracker();
    final long age = box.getStaticBoxPropertiesAge();
    if (changeTracker == age)
    {
      return;
    }

    updateMinimumChunkWidth(box);
  }

  protected void processOtherNode(final RenderNode node)
  {
    chunkWidthUpdate.update(node.getMinimumChunkWidth());
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    final ExtendedBaselineInfo extendedBaselineInfo = box.getStaticBoxLayoutProperties().getNominalBaselineInfo();
    if (extendedBaselineInfo == null)
    {
      throw new IllegalStateException("Baseline info must not be null at this point");
    }
    final StyleSheet styleSheet = box.getNodeLayoutProperties().getStyleSheet();
    final double value = styleSheet.getDoubleStyleProperty(TextStyleKeys.LINEHEIGHT, 0);
    final long afterEdge = extendedBaselineInfo.getBaseline(ExtendedBaselineInfo.AFTER_EDGE);
    if (value <= 0)
    {
      box.getPool().setLineHeight(afterEdge);
    }
    else
    {
      box.getPool().setLineHeight(RenderLength.resolveLength(afterEdge, value));
    }
    startProcessing(box.getPool());
  }

  private void computeBreakIndicator(final RenderBox box)
  {

    final StyleSheet styleSheet = box.getStyleSheet();
    final RenderBox parent = box.getParent();
    if (parent != null)
    {
      final boolean breakBefore = styleSheet.getBooleanStyleProperty(BandStyleKeys.PAGEBREAK_BEFORE);
      final boolean breakAfter = box.isBreakAfter();
      final int nodeType = parent.getLayoutNodeType();
      if ((breakBefore) && (nodeType != LayoutNodeTypes.TYPE_BOX_PARAGRAPH))
      {
        box.setManualBreakIndicator(RenderBox.BreakIndicator.DIRECT_MANUAL_BREAK);
        applyIndirectManualBreakIndicator(parent);
        return;
      }
      if (breakAfter && (nodeType != LayoutNodeTypes.TYPE_BOX_PARAGRAPH))
      {
        applyIndirectManualBreakIndicator(parent);
      }
    }

    final boolean fixedPosition = RenderLength.AUTO.equals
        (styleSheet.getStyleProperty(BandStyleKeys.FIXED_POSITION, RenderLength.AUTO)) == false;
    if (fixedPosition)
    {
      applyIndirectManualBreakIndicator(box);
    }
    else
    {
      box.setManualBreakIndicator(RenderBox.BreakIndicator.NO_MANUAL_BREAK);
    }
  }

  private void applyIndirectManualBreakIndicator(RenderBox node)
  {
    while (node != null)
    {
      if (node.getManualBreakIndicator() != RenderBox.BreakIndicator.NO_MANUAL_BREAK)
      {
        return;
      }
      node.setManualBreakIndicator(RenderBox.BreakIndicator.INDIRECT_MANUAL_BREAK);
      node = node.getParent();
    }
  }

  /**
   * Collects and possibly computes the static properties according to the CSS layouting model. The classic JFreeReport
   * layout model does not know anything about margins or borders, so in that case resolving against the CSS model is
   * ok.
   *
   * @param box the box that should be processed.
   * @return true if the box is new, false otherwise
   */
  private void updateStaticProperties(final RenderBox box)
  {
    final BoxDefinition boxDefinition = box.getBoxDefinition();
    final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();
    if (sblp.isBaselineCalculated())
    {
      // mark box as seen ..
      return;
    }

    computeMarginsAndBorders(box, boxDefinition, sblp);
    computeResolvedStyleProperties(box, sblp);
    computeBreakIndicator(box);
  }

  private void computeWidowOrphanIndicator(final RenderBox box)
  {
    final RenderBox parent = box.getParent();
    if (parent == null)
    {
      box.setParentWidowContexts(0);
      return;
    }

    if (widowsEnabled == false)
    {
      return;
    }

    final StaticBoxLayoutProperties sblp = parent.getStaticBoxLayoutProperties();
    if (sblp.getOrphans() > 0 || sblp.getWidows() > 0 || sblp.isAvoidPagebreakInside())
    {
      widowOrphanDefinitionsEncountered = true;
      box.setParentWidowContexts(parent.getParentWidowContexts() + 1);
    }
    else
    {
      box.setParentWidowContexts(parent.getParentWidowContexts());
    }

  }

  private void computeResolvedStyleProperties(final RenderBox box, final StaticBoxLayoutProperties sblp)
  {
    final StyleSheet style = box.getStyleSheet();
    final int nodeType = box.getLayoutNodeType();
    if (nodeType == LayoutNodeTypes.TYPE_BOX_LINEBOX)
    {
      sblp.setAvoidPagebreakInside(true);
    }
    else if (nodeType == LayoutNodeTypes.TYPE_BOX_TABLE_ROW)
    {
      sblp.setAvoidPagebreakInside(style.getBooleanStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, true));
    }
    else
    {
      sblp.setAvoidPagebreakInside(style.getBooleanStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false));
    }

    sblp.setDominantBaseline(-1);

    if (widowsEnabled)
    {
      sblp.setOrphans(style.getIntStyleProperty(ElementStyleKeys.ORPHANS, 0));
      sblp.setWidows(style.getIntStyleProperty(ElementStyleKeys.WIDOWS, 0));

      final boolean orphanOptOut = style.getBooleanStyleProperty(ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT, true);
      sblp.setWidowOrphanOptOut(orphanOptOut);
    }
    final ExtendedBaselineInfo baselineInfo = metaData.getBaselineInfo('x', style);
    if (baselineInfo == null)
    {
      throw new IllegalStateException();
    }
    sblp.setNominalBaselineInfo(baselineInfo);
    sblp.setFontFamily(metaData.getNormalizedFontFamilyName((String) style.getStyleProperty(TextStyleKeys.FONT)));

    final Object collapse = style.getStyleProperty(TextStyleKeys.WHITE_SPACE_COLLAPSE);
    sblp.setPreserveSpace(WhitespaceCollapse.PRESERVE.equals(collapse));
    sblp.setOverflowX(style.getBooleanStyleProperty(ElementStyleKeys.OVERFLOW_X, overflowXSupported));
    sblp.setOverflowY(style.getBooleanStyleProperty(ElementStyleKeys.OVERFLOW_Y, overflowYSupported));
    sblp.setInvisibleConsumesSpace(style.getBooleanStyleProperty
        (ElementStyleKeys.INVISIBLE_CONSUMES_SPACE, nodeType == LayoutNodeTypes.TYPE_BOX_ROWBOX));
    sblp.setVisible(style.getBooleanStyleProperty(ElementStyleKeys.VISIBLE));

    if (box.getParent() != null &&
        style.getDoubleStyleProperty(ElementStyleKeys.MIN_WIDTH, 0) == 0 &&
        style.getDoubleStyleProperty(ElementStyleKeys.WIDTH, 0) == 0)
    {
      // only a box with a parent can try to inherit a context ..
      sblp.setUndefinedWidth(true);
    }
    else
    {
      sblp.setUndefinedWidth(false);
    }
  }

  private void computeMarginsAndBorders(final RenderBox box,
                                        final BoxDefinition boxDefinition,
                                        final StaticBoxLayoutProperties sblp)
  {
    final int nodeType = box.getLayoutNodeType();
    if (nodeType == LayoutNodeTypes.TYPE_BOX_TABLE_ROW || nodeType == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION)
    {
      sblp.setBorderBottom(0);
      sblp.setBorderTop(0);
      sblp.setBorderRight(0);
      sblp.setBorderLeft(0);
    }
    else
    {
      final Border border = boxDefinition.getBorder();
      sblp.setBorderTop(border.getTop().getWidth());
      sblp.setBorderLeft(border.getLeft().getWidth());
      sblp.setBorderBottom(border.getBottom().getWidth());
      sblp.setBorderRight(border.getRight().getWidth());
    }
  }

  private StaticChunkWidthUpdate createChunkWidthUpdate(final RenderBox box)
  {
    if (chunkWidthUpdate.isInline())
    {
      return chunkWidthUpdatePool.createInline(chunkWidthUpdate, box);
    }

    final int nodeType = box.getLayoutNodeType();
    if (nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
    {
      return chunkWidthUpdatePool.createParagraph(chunkWidthUpdate, (ParagraphRenderBox) box);
    }
    if (nodeType == LayoutNodeTypes.TYPE_BOX_ROWBOX ||
        nodeType == LayoutNodeTypes.TYPE_BOX_TABLE_ROW)
    {
      return chunkWidthUpdatePool.createHorizontal(chunkWidthUpdate, box);
    }
    return chunkWidthUpdatePool.createVertical(chunkWidthUpdate, box);
  }

  protected void updateMinimumChunkWidth(final RenderBox box)
  {
    final long changeTracker = box.getChangeTracker();
    final long age = box.getStaticBoxPropertiesAge();
    if (changeTracker == age)
    {
      // update the parent
      chunkWidthUpdate.update(box.getMinimumChunkWidth());
      return;
    }

    box.setStaticBoxPropertiesAge(box.getChangeTracker());

    final StaticChunkWidthUpdate boxUpdate = chunkWidthUpdate;
    boxUpdate.finish();

    chunkWidthUpdate = chunkWidthUpdate.pop();
    chunkWidthUpdate.update(box.getMinimumChunkWidth());
  }


}
