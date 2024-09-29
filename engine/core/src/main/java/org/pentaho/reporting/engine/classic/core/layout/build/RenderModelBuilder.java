/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.layout.build;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public interface RenderModelBuilder {
  public static class SectionResult {
    private InlineSubreportMarker[] subreportMarkers;
    private boolean empty;

    public SectionResult( final InlineSubreportMarker[] subreportMarkers, final boolean empty ) {
      this.subreportMarkers = subreportMarkers;
      this.empty = empty;
    }

    public InlineSubreportMarker[] getSubreportMarkers() {
      return subreportMarkers;
    }

    public boolean isEmpty() {
      return empty;
    }
  }

  public void updateStateKey( ReportStateKey stateKey );

  public void startReport( final ReportDefinition pageDefinition, final ProcessingContext processingContext );

  public void startSubReport( final ReportDefinition report, final InstanceID insertationPoint );

  public void startGroup( final Group group, final Integer predictedStateCount );

  public void startGroupBody( final GroupBody groupBody, final Integer predictedStateCount );

  public void startSection( Renderer.SectionType type );

  public SectionResult endSection();

  public void addProgressBox() throws ReportProcessingException;

  public void addEmptyRootLevelBand() throws ReportProcessingException;

  public void addPageBreak();

  public void add( ExpressionRuntime runtime, Band band ) throws ReportProcessingException;

  public void addToNormalFlow( final ExpressionRuntime runtime, final Band band ) throws ReportProcessingException;

  public void endGroupBody();

  public void endGroup();

  public void endSubReport();

  public void endReport();

  public RenderModelBuilder deriveForStorage();

  public RenderModelBuilder deriveForPageBreak();

  public void performParanoidModelCheck();

  public void validateAfterCommit();

  public void restoreStateAfterRollback();

  public LayoutModelBuilder getNormalFlowLayoutModelBuilder();

  public LogicalPageBox getPageBox();
}
