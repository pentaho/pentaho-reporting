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

package org.pentaho.reporting.engine.classic.core.layout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.build.LayoutModelBuilder;
import org.pentaho.reporting.engine.classic.core.layout.build.RenderModelBuilder;
import org.pentaho.reporting.engine.classic.core.layout.build.ReportRenderModelBuilder;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LayoutPagebreakHandler;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.ValidateSafeToStoreStateStep;
import org.pentaho.reporting.engine.classic.core.layout.process.ApplyAutoCommitStep;
import org.pentaho.reporting.engine.classic.core.layout.process.ApplyCachedValuesStep;
import org.pentaho.reporting.engine.classic.core.layout.process.ApplyCommitStep;
import org.pentaho.reporting.engine.classic.core.layout.process.CanvasMajorAxisLayoutStep;
import org.pentaho.reporting.engine.classic.core.layout.process.CanvasMinorAxisLayoutStep;
import org.pentaho.reporting.engine.classic.core.layout.process.CommitStep;
import org.pentaho.reporting.engine.classic.core.layout.process.ComputeStaticPropertiesProcessStep;
import org.pentaho.reporting.engine.classic.core.layout.process.InfiniteMajorAxisLayoutStep;
import org.pentaho.reporting.engine.classic.core.layout.process.InfiniteMinorAxisLayoutStep;
import org.pentaho.reporting.engine.classic.core.layout.process.ParagraphLineBreakStep;
import org.pentaho.reporting.engine.classic.core.layout.process.RevalidateAllAxisLayoutStep;
import org.pentaho.reporting.engine.classic.core.layout.process.RollbackStep;
import org.pentaho.reporting.engine.classic.core.layout.process.TableValidationStep;
import org.pentaho.reporting.engine.classic.core.layout.process.ValidateModelStep;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * The LayoutSystem is a simplified version of the LibLayout-rendering system.
 *
 * @author Thomas Morgner
 * @noinspection HardCodedStringLiteral
 */
public abstract class AbstractRenderer implements Renderer
{
  private static final Log logger = LogFactory.getLog(AbstractRenderer.class);

  private RenderModelBuilder renderModelBuilder;

  private ValidateModelStep validateModelStep;
  private ComputeStaticPropertiesProcessStep staticPropertiesStep;
  private ParagraphLineBreakStep paragraphLineBreakStep;
  private InfiniteMinorAxisLayoutStep minorAxisLayoutStep;
  private CanvasMinorAxisLayoutStep canvasMinorAxisLayoutStep;
  private InfiniteMajorAxisLayoutStep majorAxisLayoutStep;
  private CanvasMajorAxisLayoutStep canvasMajorAxisLayoutStep;
  private RevalidateAllAxisLayoutStep revalidateAllAxisLayoutStep;

  private ValidateSafeToStoreStateStep validateSafeToStoreStateStep;
  private TableValidationStep tableValidationStep;

  private CommitStep commitStep;
  private ApplyCommitStep applyCommitStep;
  private RollbackStep rollbackStep;
  private ApplyAutoCommitStep applyAutoCommitStep;

  private OutputProcessorMetaData metaData;
  private OutputProcessor outputProcessor;

  private int pagebreaks;
  private boolean dirty;
  private ReportStateKey lastStateKey;
  private ApplyCachedValuesStep applyCachedValuesStep;

  private boolean readOnly;
  private boolean paranoidChecks;
  private boolean wrapProgressMarkerInSection;
  private boolean clearedHeaderAndFooter;

  private LayoutResult lastValidateResult;

  protected AbstractRenderer(final OutputProcessor outputProcessor)
  {
    this.outputProcessor = outputProcessor;
    this.metaData = outputProcessor.getMetaData();

    this.validateModelStep = new ValidateModelStep();
    this.staticPropertiesStep = new ComputeStaticPropertiesProcessStep();
    this.paragraphLineBreakStep = new ParagraphLineBreakStep();
    this.minorAxisLayoutStep = new InfiniteMinorAxisLayoutStep();
    this.canvasMinorAxisLayoutStep = new CanvasMinorAxisLayoutStep();
    this.majorAxisLayoutStep = new InfiniteMajorAxisLayoutStep();
    this.canvasMajorAxisLayoutStep = new CanvasMajorAxisLayoutStep();
    this.revalidateAllAxisLayoutStep = new RevalidateAllAxisLayoutStep();
    this.validateSafeToStoreStateStep = new ValidateSafeToStoreStateStep();
    this.applyCachedValuesStep = new ApplyCachedValuesStep();
    this.commitStep = new CommitStep();
    this.applyAutoCommitStep = new ApplyAutoCommitStep();
    this.applyCommitStep = new ApplyCommitStep();
    this.rollbackStep = new RollbackStep();
    this.tableValidationStep = new TableValidationStep();
  }

  protected void initialize()
  {
    this.renderModelBuilder = createRenderModelBuilder();
  }

  protected ReportRenderModelBuilder createRenderModelBuilder()
  {
    return new ReportRenderModelBuilder(createComponentFactory());
  }

  protected RenderComponentFactory createComponentFactory()
  {
    return new DefaultRenderComponentFactory();
  }

  public LayoutModelBuilder getNormalFlowLayoutModelBuilder()
  {
    return renderModelBuilder.getNormalFlowLayoutModelBuilder();
  }

  public int getPageCount()
  {
    return 0;
  }

  protected RenderModelBuilder getRenderModelBuilder()
  {
    return renderModelBuilder;
  }

  protected LogicalPageBox getPageBox()
  {
    return renderModelBuilder.getPageBox();
  }

  public boolean isSafeToStore()
  {
    final LogicalPageBox pageBox = getPageBox();
    if (pageBox == null)
    {
      return true;
    }
    return validateSafeToStoreStateStep.isSafeToStore(pageBox);
  }

  protected OutputProcessorMetaData getMetaData()
  {
    return metaData;
  }

  public void setStateKey(final ReportStateKey stateKey)
  {
    renderModelBuilder.updateStateKey(stateKey);
  }

  public OutputProcessor getOutputProcessor()
  {
    return outputProcessor;
  }

  public void startReport(final ReportDefinition report, final ProcessingContext processingContext)
  {
    if (report == null)
    {
      throw new NullPointerException();
    }

    if (readOnly)
    {
      throw new IllegalStateException();
    }

    outputProcessor.processingStarted(report, processingContext);

    initializeRendererOnStartReport();

    renderModelBuilder.startReport(report, processingContext);
    markDirty();
  }

  protected void initializeRendererOnStartReport()
  {
    this.paranoidChecks = "true".equals(metaData.getConfiguration().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.layout.ParanoidChecks"));
    this.wrapProgressMarkerInSection= "true".equals(metaData.getConfiguration().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.legacy.WrapProgressMarkerInSection"));
    staticPropertiesStep.initialize(metaData);
    canvasMinorAxisLayoutStep.initialize(metaData);
    minorAxisLayoutStep.initialize(metaData);
    revalidateAllAxisLayoutStep.initialize(metaData);
  }

  public void startSubReport(final ReportDefinition report, final InstanceID insertationPoint)
  {
    if (readOnly)
    {
      throw new IllegalStateException("Renderer is marked read-only");
    }

    renderModelBuilder.startSubReport(report, insertationPoint);
  }

  public void startGroup(final Group group, final Integer predictedStateCount)
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    renderModelBuilder.startGroup(group, 5);
  }

  public void startGroupBody(final GroupBody groupBody, final Integer predictedStateCount)
  {
    if (logger.isDebugEnabled())
    {
      logger.debug("Group-Body: Predicted size: " + predictedStateCount);
    }
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    renderModelBuilder.startGroupBody(groupBody, predictedStateCount);
    markDirty();
  }

  public void startSection(final SectionType type)
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }
    renderModelBuilder.startSection(type);
  }

  public InlineSubreportMarker[] endSection()
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    // todo: Cheap hack for now. Convert this into a real check that figures out whether real changes have been done.
    final RenderModelBuilder.SectionResult result = renderModelBuilder.endSection();
    if (result.isEmpty() == false)
    {
      markDirty();
    }
    return result.getSubreportMarkers();
  }

  public void endGroupBody()
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    renderModelBuilder.endGroupBody();
    markDirty();
  }

  public void endGroup()
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    renderModelBuilder.endGroup();
    markDirty();
  }

  public void endSubReport()
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    renderModelBuilder.endSubReport();
    markDirty();
  }

  public void endReport()
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    renderModelBuilder.endReport();
    markDirty();
  }

  public void addEmptyRootLevelBand()
      throws ReportProcessingException
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    renderModelBuilder.addEmptyRootLevelBand();
  }

  public void addProgressBox()
      throws ReportProcessingException
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    if (wrapProgressMarkerInSection)
    {
      renderModelBuilder.startSection(SectionType.NORMALFLOW);
      renderModelBuilder.addProgressBox();
      renderModelBuilder.endSection();
    }
    else
    {
      renderModelBuilder.addProgressBox();
    }
  }

  public void add(final Band band, final ExpressionRuntime runtime)
      throws ReportProcessingException
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    renderModelBuilder.add(runtime, band);
  }

  public void addToNormalFlow(final Band band,
                              final ExpressionRuntime runtime) throws ReportProcessingException
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    renderModelBuilder.addToNormalFlow(runtime, band);
  }

  public LayoutResult validatePages()
      throws ContentProcessingException
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    final LogicalPageBox pageBox = getPageBox();
    if (pageBox == null)
    {
      // StartReport has not been called yet ..
      lastValidateResult = LayoutResult.LAYOUT_UNVALIDATABLE;
      return LayoutResult.LAYOUT_UNVALIDATABLE;
    }

    if (!dirty && lastValidateResult != null)
    {
      return lastValidateResult;
    }

    setLastStateKey(null);
    setPagebreaks(0);
    if (validateModelStep.isLayoutable(pageBox) == false) // STRUCT
    {
      if (logger.isDebugEnabled())
      {
        logger.debug("Content-Ref# " + pageBox.getContentRefCount());
      }
      lastValidateResult = LayoutResult.LAYOUT_UNVALIDATABLE;
      return LayoutResult.LAYOUT_UNVALIDATABLE;
    }

    // These structural processors will skip old nodes. These beasts cannot be cached otherwise.
    tableValidationStep.validate(pageBox); // STRUCT
    paragraphLineBreakStep.compute(pageBox); // STRUCT
    staticPropertiesStep.compute(pageBox); // STRUCT

    minorAxisLayoutStep.compute(pageBox); // VISUAL
    canvasMinorAxisLayoutStep.compute(pageBox); // VISUAL
    majorAxisLayoutStep.compute(pageBox); // VISUAL
    canvasMajorAxisLayoutStep.compute(pageBox); // VISUAL
    revalidateAllAxisLayoutStep.compute(pageBox); // VISUAL

    applyCachedValuesStep.compute(pageBox); // STRUCT
    if (isPageFinished())
    {
      lastValidateResult = LayoutResult.LAYOUT_PAGEBREAK;
      return LayoutResult.LAYOUT_PAGEBREAK;
    }
    else
    {
      lastValidateResult = LayoutResult.LAYOUT_NO_PAGEBREAK;
      return LayoutResult.LAYOUT_NO_PAGEBREAK;
    }
  }

  protected void clearDirty()
  {
    dirty = false;
  }

  protected abstract boolean isPageFinished();

  public void processIncrementalUpdate(final boolean performOutput) throws ContentProcessingException
  {
//    dirty = false;
  }

  public boolean processPage(final LayoutPagebreakHandler handler,
                             final Object commitMarker,
                             final boolean performOutput) throws ContentProcessingException
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    final LogicalPageBox pageBox = getPageBox();
    if (pageBox == null)
    {
      // StartReport has not been called yet ..
//      Log.debug ("PageBox null");
      return false;
    }

    if (dirty == false)
    {
//      Log.debug ("Not dirty");
      return false;
    }

    setLastStateKey(null);
    setPagebreaks(0);
    if (validateModelStep.isLayoutable(pageBox) == false)
    {
//      Log.debug ("Not layoutable");
      return false;
    }

    // processes the current page
    boolean repeat = true;
    while (repeat)
    {
      if (handler != null)
      {
        // make sure we generate an up-to-date page-footer. This also implies that there
        // are more page-finished than page-started events generated during the report processing.
        handler.pageFinished();
      }

      if (outputProcessor.getMetaData().isFeatureSupported(OutputProcessorFeature.PAGEBREAKS))
      {
        createRollbackInformation();
        applyRollbackInformation();
        performParanoidModelCheck();
      }

      tableValidationStep.validate(pageBox); // STRUCT
      paragraphLineBreakStep.compute(pageBox); // STRUCT
      staticPropertiesStep.compute(pageBox); // VISUAL

      minorAxisLayoutStep.compute(pageBox);
      canvasMinorAxisLayoutStep.compute(pageBox);
      majorAxisLayoutStep.compute(pageBox);
      canvasMajorAxisLayoutStep.compute(pageBox);
      revalidateAllAxisLayoutStep.compute(pageBox);

      applyCachedValuesStep.compute(pageBox);

      repeat = performPagination(handler, performOutput);
    }
    clearDirty();
    return (pagebreaks > 0);
  }

  protected abstract boolean performPagination(LayoutPagebreakHandler handler,
                                               final boolean performOutput)
      throws ContentProcessingException;

  /**
   * A hook to allow easier debugging.
   *
   * @param pageBox the current page box.
   * @noinspection NoopMethodInAbstractClass
   */
  protected void debugPrint(final LogicalPageBox pageBox)
  {

  }

  public ReportStateKey getLastStateKey()
  {
    return lastStateKey;
  }

  public void setLastStateKey(final ReportStateKey lastStateKey)
  {
    this.lastStateKey = lastStateKey;
  }

  protected void setPagebreaks(final int pagebreaks)
  {
    this.pagebreaks = pagebreaks;
  }

  public int getPagebreaks()
  {
    return pagebreaks;
  }

  public boolean isOpen()
  {
    final LogicalPageBox pageBox = getPageBox();
    if (pageBox == null)
    {
      return false;
    }
    return pageBox.isOpen();
  }

  public boolean isValid()
  {
    return readOnly == false;
  }

  public Renderer deriveForStorage()
  {
    try
    {
      final AbstractRenderer renderer = (AbstractRenderer) clone();
      renderer.readOnly = false;
      renderer.renderModelBuilder = renderModelBuilder.deriveForStorage();
      return renderer;
    }
    catch (CloneNotSupportedException cne)
    {
      throw new InvalidReportStateException("Failed to derive Renderer", cne);
    }
  }

  public Renderer deriveForPagebreak()
  {
    try
    {
      final AbstractRenderer renderer = (AbstractRenderer) clone();
      renderer.readOnly = true;
      renderer.renderModelBuilder = renderModelBuilder.deriveForPageBreak();
      return renderer;
    }
    catch (CloneNotSupportedException cne)
    {
      throw new InvalidReportStateException("Failed to derive Renderer", cne);
    }
  }

  public void performParanoidModelCheck()
  {
    if (paranoidChecks)
    {
      renderModelBuilder.performParanoidModelCheck();
    }
  }

  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  public void addPagebreak()
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    renderModelBuilder.addPageBreak();
  }

  public boolean clearPendingPageStart(final LayoutPagebreakHandler layoutPagebreakHandler)
  {
    // intentionally left empty.
    return false;
  }

  public boolean isCurrentPageEmpty()
  {
    return false;
  }

  public boolean isPageStartPending()
  {
    return false;
  }

  public boolean isDirty()
  {
    return dirty;
  }

  public void createRollbackInformation()
  {
    final LogicalPageBox pageBox = getPageBox();
    if (pageBox != null)
    {
      commitStep.compute(pageBox);
    }
  }

  public void applyRollbackInformation()
  {
    final LogicalPageBox pageBox = getPageBox();
    if (pageBox != null)
    {
      applyCommitStep.compute(pageBox);
    }
  }

  public void validateAfterCommit()
  {
    if (paranoidChecks)
    {
      renderModelBuilder.validateAfterCommit();
    }
  }

  public void rollback()
  {
    readOnly = false;
    final LogicalPageBox pageBox = getPageBox();
    if (pageBox != null)
    {
      rollbackStep.compute(pageBox);
      renderModelBuilder.restoreStateAfterRollback();
      validateAfterCommit();
    }
  }


  public void applyAutoCommit()
  {
    final LogicalPageBox pageBox = getPageBox();
    if (pageBox != null)
    {
      applyAutoCommitStep.compute(pageBox);
    }
  }

  public boolean isPendingPageHack()
  {
    return false;
  }

  protected void markDirty()
  {
    dirty = true;
    lastValidateResult = null;
  }

  public void print()
  {
    ModelPrinter.INSTANCE.print(renderModelBuilder.getPageBox());
  }

  public void newPageStarted()
  {
    if (logger.isDebugEnabled())
    {
      logger.debug("================================ CLEAR HEADER AND FOOTER ==================================: " + getPageCount());
    }

    final LogicalPageBox pageBox = getPageBox();
    pageBox.getFooterArea().clear();
    pageBox.getRepeatFooterArea().clear();
    pageBox.getHeaderArea().clear();
    pageBox.getWatermarkArea().clear();
  }
}
