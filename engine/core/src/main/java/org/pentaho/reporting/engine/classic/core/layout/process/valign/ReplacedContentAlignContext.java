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

import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.process.util.ReplacedContentUtil;
import org.pentaho.reporting.engine.classic.core.layout.text.ExtendedBaselineInfo;

/**
 * A generic align context for images and other nodes. (Renderable-Content should have been aligned by the parent.
 *
 * @author Thomas Morgner
 */
public final class ReplacedContentAlignContext extends AlignContext {
  private long shift;
  private long height;

  public ReplacedContentAlignContext( final RenderableReplacedContentBox node, final long parentHeight ) {
    super( node );
    this.height = ReplacedContentUtil.computeHeight( node, parentHeight, node.getCachedWidth() );
  }

  public long getBaselineDistance( final int baseline ) {
    if ( baseline == ExtendedBaselineInfo.BEFORE_EDGE ) {
      return 0;
    }
    if ( baseline == ExtendedBaselineInfo.TEXT_BEFORE_EDGE ) {
      return 0;
    }
    // oh that's soooo primitive ..
    return height;
  }

  public void shift( final long delta ) {
    this.shift += delta;
  }

  public long getAfterEdge() {
    return shift + height;
  }

  public long getBeforeEdge() {
    return shift;
  }
}
