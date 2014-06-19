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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.layout.process.valign;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.text.ExtendedBaselineInfo;


/**
 * Todo: We should select a baseline (and not be limited to the first one)
 *
 * @author Thomas Morgner
 */
public final class InlineBlockAlignContext extends AlignContext
{
  private long[] baselines;
  private long baselineShift;

  public InlineBlockAlignContext(final RenderBox box)
  {
    super(box);
    final ExtendedBaselineInfo baselineInfo = box.getBaselineInfo();
    this.baselines = baselineInfo.getBaselines();
    setDominantBaseline(baselineInfo.getDominantBaseline());
  }

  public long getBaselineDistance(final int baseline)
  {
    return (baselines[baseline] - baselines[getDominantBaseline()]) + baselineShift;
  }

  public void shift(final long delta)
  {
    baselineShift += delta;
  }

  public long getAfterEdge()
  {
    return this.baselines[ExtendedBaselineInfo.AFTER_EDGE] + baselineShift;
  }

  public long getBeforeEdge()
  {
    return this.baselines[ExtendedBaselineInfo.BEFORE_EDGE] + baselineShift;
  }
}
