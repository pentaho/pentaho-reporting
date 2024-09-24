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

public abstract class LayoutModelBuilderWrapper implements LayoutModelBuilder {
  private LayoutModelBuilder parent;

  protected LayoutModelBuilderWrapper( final LayoutModelBuilder parent ) {
    this.parent = parent;
  }

  public void initialize( final ProcessingContext processingContext, final RenderBox parentBox,
      final RenderNodeFactory renderNodeFactory ) {
    parent.initialize( processingContext, parentBox, renderNodeFactory );
  }

  public void setLimitedSubReports( final boolean limitedSubReports ) {
    parent.setLimitedSubReports( limitedSubReports );
  }

  public void updateState( final ReportStateKey stateKey ) {
    parent.updateState( stateKey );
  }

  public InstanceID startBox( final ReportElement element ) {
    return parent.startBox( element );
  }

  public void startSection() {
    parent.startSection();
  }

  public void startSection( final ReportElement element, final int sectionSize ) {
    parent.startSection( element, sectionSize );
  }

  public void processContent( final ReportElement element, final Object computedValue, final Object rawValue ) {
    parent.processContent( element, computedValue, rawValue );
  }

  public InstanceID createSubflowPlaceholder( final ReportElement element ) {
    return parent.createSubflowPlaceholder( element );
  }

  public InlineSubreportMarker processSubReport( final SubReport element ) {
    return parent.processSubReport( element );
  }

  public boolean finishBox() {
    return parent.finishBox();
  }

  public void endSection() {
    parent.endSection();
  }

  public boolean isEmptyElementsHaveSignificance() {
    return parent.isEmptyElementsHaveSignificance();
  }

  public boolean isEmpty() {
    return parent.isEmpty();
  }

  public void print() {
    parent.print();
  }

  public void startSubFlow( final InstanceID insertationPoint ) {
    parent.startSubFlow( insertationPoint );
  }

  public void startSubFlow( final ReportElement element ) {
    parent.startSubFlow( element );
  }

  public void suspendSubFlow() {
    parent.suspendSubFlow();
  }

  public void endSubFlow() {
    parent.endSubFlow();
  }

  public void addProgressMarkerBox() {
    parent.addProgressMarkerBox();
  }

  public void addManualPageBreakBox( final long range ) {
    parent.addManualPageBreakBox( range );
  }

  protected LayoutModelBuilder getParent() {
    return parent;
  }

  public LayoutModelBuilder deriveForStorage( final RenderBox clonedContent ) {
    final LayoutModelBuilderWrapper clone = (LayoutModelBuilderWrapper) clone();
    clone.parent = parent.deriveForStorage( clonedContent );
    return clone;
  }

  public LayoutModelBuilder deriveForPageBreak() {
    final LayoutModelBuilderWrapper clone = (LayoutModelBuilderWrapper) clone();
    clone.parent = parent.deriveForPageBreak();
    return clone;
  }

  public void validateAfterCommit() {
    parent.validateAfterCommit();
  }

  public void performParanoidModelCheck( final RenderBox logicalPageBox ) {
    parent.performParanoidModelCheck( logicalPageBox );
  }

  public void restoreStateAfterRollback() {
    parent.restoreStateAfterRollback();
  }

  public void legacyAddPlaceholder( final ReportElement element ) {
    parent.legacyAddPlaceholder( element );
  }

  public void legacyFlagNotEmpty() {
    parent.legacyFlagNotEmpty();
  }

  public RenderNode dangerousRawAccess() {
    return parent.dangerousRawAccess();
  }

  public void close() {
    parent.close();
  }

  public void setCollapseProgressMarker( final boolean b ) {
    parent.setCollapseProgressMarker( b );
  }

  public LayoutModelBuilder clone() {
    try {
      final LayoutModelBuilderWrapper clone = (LayoutModelBuilderWrapper) super.clone();
      clone.parent = parent.clone();
      return clone;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException( e );
    }
  }
}
