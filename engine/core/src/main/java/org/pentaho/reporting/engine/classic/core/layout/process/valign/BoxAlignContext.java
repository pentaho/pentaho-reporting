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

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.text.ExtendedBaselineInfo;

/**
 * Creation-Date: 13.10.2006, 22:22:10
 *
 * @author Thomas Morgner
 */
public final class BoxAlignContext extends AlignContext {
  private long insetsTop;
  private long insetsBottom;
  private long[] baselines;
  private AlignContext firstChild;
  private AlignContext lastChild;
  private boolean simpleContext;

  public BoxAlignContext( final RenderBox box ) {
    super( box );

    simpleContext = true;

    final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
    ExtendedBaselineInfo baselineInfo = box.getBaselineInfo();
    if ( baselineInfo == null ) {
      baselineInfo = blp.getNominalBaselineInfo();
    }
    if ( baselineInfo == null ) {
      throw new IllegalStateException( "A box that has no baseline info." );
    }
    final int dominantBaselineValue = blp.getDominantBaseline();
    if ( dominantBaselineValue == -1 ) {
      setDominantBaseline( baselineInfo.getDominantBaseline() );
    } else {
      setDominantBaseline( dominantBaselineValue );
    }

    final BoxDefinition bdef = box.getBoxDefinition();
    insetsTop = blp.getBorderTop() + bdef.getPaddingTop();
    insetsBottom = blp.getBorderBottom() + bdef.getPaddingBottom();

    baselines = baselineInfo.getBaselines();
    final int length = baselines.length;
    for ( int i = 1; i < length; i++ ) {
      baselines[i] += insetsTop;
    }
    final long afterEdge = baselines[ExtendedBaselineInfo.TEXT_AFTER_EDGE] + insetsBottom;
    baselines[ExtendedBaselineInfo.AFTER_EDGE] = afterEdge;
  }

  public boolean isSimpleNode() {
    return simpleContext;
  }

  public void addChild( final AlignContext context ) {
    if ( simpleContext == true && context.isSimpleNode() == false ) {
      simpleContext = false;
    }

    if ( lastChild == null ) {
      firstChild = context;
      lastChild = context;
      return;
    }
    lastChild.setNext( context );
    lastChild = context;
  }

  public AlignContext getFirstChild() {
    return firstChild;
  }

  public long getInsetsTop() {
    return insetsTop;
  }

  public long getInsetsBottom() {
    return insetsBottom;
  }

  public long getBaselineDistance( final int baseline ) {
    return baselines[baseline] - baselines[getDominantBaseline()];
  }

  public void shift( final long delta ) {
    final int length = baselines.length;
    for ( int i = 0; i < length; i++ ) {
      baselines[i] += delta;
    }

    AlignContext child = getFirstChild();
    while ( child != null ) {
      child.shift( delta );
      child = child.getNext();
    }
  }

  public long getAfterEdge() {
    return this.baselines[ExtendedBaselineInfo.AFTER_EDGE];
  }

  public long getBeforeEdge() {
    return this.baselines[ExtendedBaselineInfo.BEFORE_EDGE];
  }

  public void setBeforeEdge( final long offset ) {
    this.baselines[ExtendedBaselineInfo.BEFORE_EDGE] = offset;
  }

  public void setAfterEdge( final long offset ) {
    this.baselines[ExtendedBaselineInfo.AFTER_EDGE] = offset;
  }

  public void validate() {
    if ( simpleContext == false ) {
      return;
    }

    AlignContext child = getFirstChild();
    while ( child != null ) {
      if ( child.isSimpleNode() == false ) {
        simpleContext = false;
        return;
      }
      // validate that all baselines are equal ..
      if ( getAfterEdge() != child.getAfterEdge() ) {
        simpleContext = false;
        return;
      }
      if ( getBeforeEdge() != child.getBeforeEdge() ) {
        simpleContext = false;
        return;
      }
      final int dominantBaseline = getDominantBaseline();
      if ( dominantBaseline != child.getDominantBaseline() ) {
        simpleContext = false;
        return;
      }
      if ( getBaselineDistance( dominantBaseline ) != child.getBaselineDistance( dominantBaseline ) ) {
        simpleContext = false;
        return;
      }
      child = child.getNext();
    }
  }
}
