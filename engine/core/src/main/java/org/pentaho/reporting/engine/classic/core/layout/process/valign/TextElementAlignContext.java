/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
