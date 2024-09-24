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

package org.pentaho.reporting.engine.classic.core.testsupport.dummyoutput;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.build.LayoutModelBuilder;
import org.pentaho.reporting.engine.classic.core.layout.build.RenderNodeFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class DummyLayoutModelBuilder implements LayoutModelBuilder {
  public DummyLayoutModelBuilder() {
  }

  public void initialize( final ProcessingContext processingContext, final RenderBox parentBox,
      final RenderNodeFactory renderNodeFactory ) {

  }

  public void setLimitedSubReports( final boolean limitedSubReports ) {

  }

  public void updateState( final ReportStateKey stateKey ) {

  }

  public InstanceID startBox( final ReportElement element ) {
    return null;
  }

  public void setCollapseProgressMarker( final boolean b ) {

  }

  public void startSection() {

  }

  public void startSection( final ReportElement element, final int sectionSize ) {

  }

  public void processContent( final ReportElement element, final Object computedValue, final Object rawValue ) {

  }

  public InstanceID createSubflowPlaceholder( final ReportElement element ) {
    return null;
  }

  public InlineSubreportMarker processSubReport( final SubReport element ) {
    return null;
  }

  public boolean finishBox() {
    return false;
  }

  public void endSection() {

  }

  public boolean isEmptyElementsHaveSignificance() {
    return false;
  }

  public boolean isEmpty() {
    return true;
  }

  public void print() {

  }

  public void startSubFlow( final InstanceID insertationPoint ) {

  }

  public void startSubFlow( final ReportElement element ) {

  }

  public void suspendSubFlow() {

  }

  public void endSubFlow() {

  }

  public void addProgressMarkerBox() {

  }

  public void addManualPageBreakBox( final long range ) {

  }

  public LayoutModelBuilder deriveForStorage( final RenderBox clonedContent ) {
    return clone();
  }

  public LayoutModelBuilder deriveForPageBreak() {
    return clone();
  }

  public void validateAfterCommit() {

  }

  public void performParanoidModelCheck( final RenderBox logicalPageBox ) {

  }

  public void restoreStateAfterRollback() {

  }

  public void legacyAddPlaceholder( final ReportElement element ) {

  }

  public void legacyFlagNotEmpty() {

  }

  public RenderNode dangerousRawAccess() {
    return null;
  }

  public DummyLayoutModelBuilder clone() {
    try {
      return (DummyLayoutModelBuilder) super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  public void close() {
  }

}
