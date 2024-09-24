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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
