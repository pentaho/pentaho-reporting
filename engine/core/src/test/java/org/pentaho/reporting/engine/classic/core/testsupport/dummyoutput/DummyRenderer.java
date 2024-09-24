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

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.build.LayoutModelBuilder;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LayoutPagebreakHandler;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class DummyRenderer implements Renderer {
  private boolean finished;
  private ReportStateKey lastStateKey;
  private OutputProcessor outputProcessor;
  private LayoutModelBuilder modelBuilder;

  public DummyRenderer( final OutputProcessor outputProcessor ) {
    this.outputProcessor = outputProcessor;
  }

  public OutputProcessor getOutputProcessor() {
    return outputProcessor;
  }

  public LayoutModelBuilder getNormalFlowLayoutModelBuilder() {
    return modelBuilder;
  }

  public void startReport( final ReportDefinition pageDefinition, final ProcessingContext processingContext,
      final PerformanceMonitorContext performanceMonitorContext ) {
    modelBuilder = new DummyLayoutModelBuilder();
  }

  public void startSubReport( final ReportDefinition report, final InstanceID insertationPoint ) {

  }

  public void startGroup( final Group group, final Integer predictedStateCount ) {

  }

  public void startGroupBody( final GroupBody groupBody, final Integer predictedStateCount ) {

  }

  public void startSection( final SectionType type ) {

  }

  public InlineSubreportMarker[] endSection() {
    return new InlineSubreportMarker[0];
  }

  public void addProgressBox() throws ReportProcessingException {

  }

  public void addEmptyRootLevelBand() throws ReportProcessingException {

  }

  public void add( final Band band, final ExpressionRuntime runtime ) throws ReportProcessingException {

  }

  public void addToNormalFlow( final Band band, final ExpressionRuntime runtime ) throws ReportProcessingException {

  }

  public void endGroupBody() {

  }

  public void endGroup() {

  }

  public void endSubReport() {

  }

  public void endReport() {
    finished = true;
  }

  public LayoutResult validatePages() throws ContentProcessingException {
    if ( finished == false ) {
      return LayoutResult.LAYOUT_UNVALIDATABLE;
    }
    return LayoutResult.LAYOUT_PAGEBREAK;
  }

  public boolean processPage( final LayoutPagebreakHandler handler, final Object commitMarker,
      final boolean performOutput ) throws ContentProcessingException {
    if ( finished ) {
      outputProcessor.processingFinished();
      return true;
    }
    return false;
  }

  public void processIncrementalUpdate( final boolean performOutput ) throws ContentProcessingException {

  }

  public int getPagebreaks() {
    return 0;
  }

  public boolean isOpen() {
    return !finished;
  }

  public ReportStateKey getLastStateKey() {
    return lastStateKey;
  }

  public void addPagebreak() {

  }

  public boolean clearPendingPageStart( final LayoutPagebreakHandler layoutPagebreakHandler ) {
    return false;
  }

  public boolean isPageStartPending() {
    return false;
  }

  public boolean isCurrentPageEmpty() {
    return false;
  }

  public Renderer deriveForStorage() {
    return clone();
  }

  public Renderer deriveForPagebreak() {
    return clone();
  }

  public boolean isValid() {
    return false;
  }

  public void createRollbackInformation() {

  }

  public void applyRollbackInformation() {

  }

  public void rollback() {

  }

  public void setStateKey( final ReportStateKey stateKey ) {
    lastStateKey = stateKey;
  }

  public void applyAutoCommit() {

  }

  public boolean isPendingPageHack() {
    return false;
  }

  public boolean isSafeToStore() {
    return true;
  }

  public void print() {

  }

  public void newPageStarted() {

  }

  public int getPageCount() {
    return 0;
  }

  public Renderer clone() {
    try {
      return (Renderer) super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }
}
