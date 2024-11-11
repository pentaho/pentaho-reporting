/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.layout;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.build.LayoutModelBuilder;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LayoutPagebreakHandler;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public interface Renderer extends Cloneable {
  public static enum LayoutResult {
    LAYOUT_UNVALIDATABLE, LAYOUT_NO_PAGEBREAK, LAYOUT_PAGEBREAK
  }

  public static enum SectionType {
    NORMALFLOW, HEADER, FOOTER, REPEAT_FOOTER, WATERMARK
  }

  public OutputProcessor getOutputProcessor();

  public LayoutModelBuilder getNormalFlowLayoutModelBuilder();

  public void startReport( final ReportDefinition pageDefinition, final ProcessingContext processingContext,
      final PerformanceMonitorContext performanceMonitorContext );

  public void startSubReport( final ReportDefinition report, final InstanceID insertationPoint );

  public void startGroup( final Group group, final Integer predictedStateCount );

  public void startGroupBody( final GroupBody groupBody, final Integer predictedStateCount );

  public void startSection( SectionType type );

  public InlineSubreportMarker[] endSection();

  public void addProgressBox() throws ReportProcessingException;

  public void addEmptyRootLevelBand() throws ReportProcessingException;

  public void add( Band band, ExpressionRuntime runtime ) throws ReportProcessingException;

  public void endGroupBody();

  public void endGroup();

  public void endSubReport();

  public void endReport();

  public LayoutResult validatePages() throws ContentProcessingException;

  public boolean processPage( final LayoutPagebreakHandler handler, final Object commitMarker,
      final boolean performOutput ) throws ContentProcessingException;

  public void processIncrementalUpdate( final boolean performOutput ) throws ContentProcessingException;

  public int getPagebreaks();

  public boolean isOpen();

  public Object clone() throws CloneNotSupportedException;

  public ReportStateKey getLastStateKey();

  public void addPagebreak();

  public boolean clearPendingPageStart( final LayoutPagebreakHandler layoutPagebreakHandler );

  public boolean isPageStartPending();

  public boolean isCurrentPageEmpty();

  public Renderer deriveForStorage();

  public Renderer deriveForPagebreak();

  public boolean isValid();

  public void createRollbackInformation();

  public void applyRollbackInformation();

  public void rollback();

  public void setStateKey( ReportStateKey stateKey );

  public void applyAutoCommit();

  public boolean isPendingPageHack();

  public boolean isSafeToStore();

  void print();

  void newPageStarted();

  public int getPageCount();

}
