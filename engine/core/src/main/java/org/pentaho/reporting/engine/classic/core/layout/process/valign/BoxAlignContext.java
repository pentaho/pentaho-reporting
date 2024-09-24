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
