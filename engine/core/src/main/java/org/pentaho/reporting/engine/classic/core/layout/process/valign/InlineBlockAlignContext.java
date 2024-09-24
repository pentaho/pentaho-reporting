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

package org.pentaho.reporting.engine.classic.core.layout.process.valign;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.text.ExtendedBaselineInfo;

/**
 * Todo: We should select a baseline (and not be limited to the first one)
 *
 * @author Thomas Morgner
 */
public final class InlineBlockAlignContext extends AlignContext {
  private long[] baselines;
  private long baselineShift;

  public InlineBlockAlignContext( final RenderBox box ) {
    super( box );
    final ExtendedBaselineInfo baselineInfo = box.getBaselineInfo();
    this.baselines = baselineInfo.getBaselines();
    setDominantBaseline( baselineInfo.getDominantBaseline() );
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
