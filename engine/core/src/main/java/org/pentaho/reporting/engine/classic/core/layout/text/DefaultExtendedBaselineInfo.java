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


package org.pentaho.reporting.engine.classic.core.layout.text;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.libraries.fonts.registry.BaselineInfo;

/**
 * Creation-Date: 24.07.2006, 17:35:25
 *
 * @author Thomas Morgner
 */
public final class DefaultExtendedBaselineInfo implements ExtendedBaselineInfo {
  private long[] baselines;
  private int dominantBaseline;
  private long underlinePosition;
  private long strikethroughPosition;

  public DefaultExtendedBaselineInfo( final int dominantBaseline, final BaselineInfo baselines, final long beforeEdge,
      final long textBeforeEdge, final long textAfterEdge, final long afterEdge, final long underlinePosition,
      final long strikethroughPosition ) {
    if ( baselines == null ) {
      throw new NullPointerException();
    }
    final long[] rawbaselines = baselines.getBaselines();
    this.baselines = new long[ExtendedBaselineInfo.BASELINE_COUNT];
    this.baselines[ExtendedBaselineInfo.BEFORE_EDGE] = RenderableText.convert( beforeEdge );
    this.baselines[ExtendedBaselineInfo.TEXT_BEFORE_EDGE] = RenderableText.convert( textBeforeEdge );
    this.baselines[ExtendedBaselineInfo.HANGING] = RenderableText.convert( rawbaselines[BaselineInfo.HANGING] );
    this.baselines[ExtendedBaselineInfo.CENTRAL] = RenderableText.convert( rawbaselines[BaselineInfo.CENTRAL] );
    this.baselines[ExtendedBaselineInfo.MIDDLE] = RenderableText.convert( rawbaselines[BaselineInfo.MIDDLE] );
    this.baselines[ExtendedBaselineInfo.MATHEMATICAL] =
        RenderableText.convert( rawbaselines[BaselineInfo.MATHEMATICAL] );
    this.baselines[ExtendedBaselineInfo.ALPHABETHIC] = RenderableText.convert( rawbaselines[BaselineInfo.ALPHABETIC] );
    this.baselines[ExtendedBaselineInfo.IDEOGRAPHIC] = RenderableText.convert( rawbaselines[BaselineInfo.IDEOGRAPHIC] );
    this.baselines[ExtendedBaselineInfo.TEXT_AFTER_EDGE] = RenderableText.convert( textAfterEdge );
    this.baselines[ExtendedBaselineInfo.AFTER_EDGE] = RenderableText.convert( afterEdge );

    this.strikethroughPosition = RenderableText.convert( strikethroughPosition );
    this.underlinePosition = RenderableText.convert( underlinePosition );
    // this.baselines = baselines;
    this.dominantBaseline = dominantBaseline;
  }

  public long getUnderlinePosition() {
    return underlinePosition;
  }

  public long getStrikethroughPosition() {
    return strikethroughPosition;
  }

  public int getDominantBaseline() {
    return dominantBaseline;
  }

  public long[] getBaselines() {
    return baselines.clone();
  }

  // public void setBaselines(final long[] baselines)
  // {
  // if (baselines.length != ExtendedBaselineInfo.BASELINE_COUNT)
  // {
  // throw new IllegalArgumentException();
  // }
  // System.arraycopy(baselines, 0, this.baselines, 0, ExtendedBaselineInfo.BASELINE_COUNT);
  // }

  public long getBaseline( final int baseline ) {
    return baselines[baseline];
  }

  public String toString() {
    final StringBuffer b = new StringBuffer( 100 );
    b.append( "DefaultExtendedBaselineInfo{" );
    final int length = ExtendedBaselineInfo.BASELINE_COUNT;
    for ( int i = 0; i < length; i++ ) {
      if ( i > 0 ) {
        b.append( ", " );
      }
      b.append( "baselines[" );
      b.append( String.valueOf( i ) );
      b.append( "]=" );
      b.append( getBaseline( i ) );

    }
    b.append( ", dominantBaseline=" );
    b.append( dominantBaseline );
    b.append( '}' );
    return b.toString();
  }
}
