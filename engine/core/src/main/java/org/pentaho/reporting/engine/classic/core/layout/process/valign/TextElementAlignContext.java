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


package org.pentaho.reporting.engine.classic.core.layout.process.valign;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.text.ExtendedBaselineInfo;

/**
 * Creation-Date: 13.10.2006, 22:26:50
 *
 * @author Thomas Morgner
 */
public final class TextElementAlignContext extends AlignContext {
  private long[] baselines;
  private long baselineShift;

  public TextElementAlignContext( final RenderableText text ) {
    super( text );
    final ExtendedBaselineInfo baselineInfo = text.getBaselineInfo();
    this.baselines = baselineInfo.getBaselines();
    setDominantBaseline( baselineInfo.getDominantBaseline() );
  }

  public boolean isSimpleNode() {
    return true;
  }

  public long getBaselineDistance( final int baseline ) {
    return ( baselines[baseline] - baselines[getDominantBaseline()] ) + baselineShift;
  }

  public void shift( final long delta ) {
    baselineShift += delta;
  }

  public long getAfterEdge() {
    return this.baselines[ExtendedBaselineInfo.AFTER_EDGE] + baselineShift;
  }

  public long getBeforeEdge() {
    return this.baselines[ExtendedBaselineInfo.BEFORE_EDGE] + baselineShift;
  }
}
