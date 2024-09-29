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

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * A construction helper for layout models. Implementations of this interface are generally stateful working as
 * event-driven builders for the layout model.
 */
public interface LayoutModelBuilder extends Cloneable {
  public void initialize( final ProcessingContext processingContext, final RenderBox parentBox,
      final RenderNodeFactory renderNodeFactory );

  public void setLimitedSubReports( final boolean limitedSubReports );

  public void updateState( final ReportStateKey stateKey );

  public InstanceID startBox( ReportElement element );

  public void startSection();

  public void startSection( final ReportElement element, final int sectionSize );

  public void processContent( ReportElement element, Object computedValue, Object rawValue );

  public InstanceID createSubflowPlaceholder( final ReportElement element );

  public InlineSubreportMarker processSubReport( final SubReport element );

  public boolean finishBox();

  public void endSection();

  public boolean isEmptyElementsHaveSignificance();

  public boolean isEmpty();

  public void print();

  public void startSubFlow( InstanceID insertationPoint );

  public void startSubFlow( ReportElement element );

  public void suspendSubFlow();

  void endSubFlow();

  void addProgressMarkerBox();

  void addManualPageBreakBox( final long range );

  LayoutModelBuilder deriveForStorage( RenderBox clonedContent );

  LayoutModelBuilder deriveForPageBreak();

  void validateAfterCommit();

  public void performParanoidModelCheck( final RenderBox logicalPageBox );

  void restoreStateAfterRollback();

  void legacyAddPlaceholder( final ReportElement element );

  void legacyFlagNotEmpty();

  RenderNode dangerousRawAccess();

  void close();

  void setCollapseProgressMarker( boolean b );

  LayoutModelBuilder clone();
}
