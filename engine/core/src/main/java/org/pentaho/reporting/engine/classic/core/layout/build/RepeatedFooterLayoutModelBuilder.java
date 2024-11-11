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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class RepeatedFooterLayoutModelBuilder extends LayoutModelBuilderWrapper {
  private static final Log logger = LogFactory.getLog( RepeatedFooterLayoutModelBuilder.class );

  private RenderBox parentBox;
  private int inBoxDepth;
  private OutputProcessorMetaData metaData;

  public RepeatedFooterLayoutModelBuilder( final LayoutModelBuilder backend ) {
    super( backend );
    backend.setLimitedSubReports( true );
  }

  public void initialize( final ProcessingContext metaData, final RenderBox parentBox,
      final RenderNodeFactory renderNodeFactory ) {
    this.parentBox = parentBox;
    getParent().initialize( metaData, parentBox, renderNodeFactory );
    this.metaData = metaData.getOutputProcessorMetaData();
  }

  public void setLimitedSubReports( final boolean limitedSubReports ) {

  }

  public InstanceID startBox( final ReportElement element ) {
    InstanceID instanceID = getParent().startBox( element );
    inBoxDepth += 1;
    return instanceID;
  }

  public void startSection( final ReportElement element, final int sectionSize ) {
    throw new UnsupportedOperationException( "Global sections cannot be started for page headers" );
  }

  public InlineSubreportMarker processSubReport( final SubReport element ) {
    return null;
  }

  public boolean finishBox() {
    inBoxDepth -= 1;
    return super.finishBox();
  }

  public void endSubFlow() {
    throw new UnsupportedOperationException( "SubReport sections cannot be started for page headers" );
  }

  public void addProgressMarkerBox() {
    if ( inBoxDepth != 0 ) {
      throw new IllegalStateException();
    }
    super.addProgressMarkerBox();
  }

  public void addManualPageBreakBox( final long range ) {
    throw new UnsupportedOperationException( "PageBreak sections cannot be started for page headers" );
  }

  public LayoutModelBuilder deriveForStorage( final RenderBox clonedContent ) {
    final RepeatedFooterLayoutModelBuilder clone =
        (RepeatedFooterLayoutModelBuilder) super.deriveForStorage( clonedContent );
    clone.parentBox = clonedContent;
    return clone;
  }

  public void startSection() {
    if ( inBoxDepth != 0 ) {
      throw new IllegalStateException();
    }

    parentBox.clear();
    super.startSection();
  }

  public void endSection() {
    if ( inBoxDepth != 0 ) {
      throw new IllegalStateException();
    }

    if ( metaData.isFeatureSupported( OutputProcessorFeature.STRICT_COMPATIBILITY ) ) {
      super.legacyFlagNotEmpty();
    }
    super.endSection();
  }

  public InstanceID createSubflowPlaceholder( final ReportElement element ) {
    throw new UnsupportedOperationException( "SubReport sections cannot be started for page headers" );
  }

  public void startSubFlow( final InstanceID insertationPoint ) {
    throw new UnsupportedOperationException( "SubReport sections cannot be started for page headers" );
  }

  public void startSubFlow( final ReportElement element ) {
    throw new UnsupportedOperationException( "SubReport sections cannot be started for page headers" );
  }

  public void suspendSubFlow() {
    throw new UnsupportedOperationException( "SubReport sections cannot be started for page headers" );
  }
}
