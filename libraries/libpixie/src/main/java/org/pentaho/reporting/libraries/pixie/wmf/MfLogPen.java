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

package org.pentaho.reporting.libraries.pixie.wmf;

import java.awt.*;

/**
 * A Windows metafile logical pen object.
 */
public class MfLogPen implements WmfObject {

  /**
   * ___ ___ ___
   */
  private static final float[] DASH_DASH =
    {
      6f, 2f
    };

  /**
   * _ _ _ _ _ _
   */
  private static final float[] DASH_DOT =
    {
      2f, 2f
    };

  /**
   * ___ _ ___ _
   */
  private static final float[] DASH_DASHDOT =
    {
      6f, 2f, 2f, 2f
    };

  /**
   * ___ _ _ ___
   */
  private static final float[] DASH_DASHDOTDOT =
    {
      6f, 2f, 2f, 2f, 2f, 2f
    };
  private int style;
  private int endCap;
  private int joinType;
  private int width;
  private Color color;


  /**
   * The default pen for a new DC.
   */
  public MfLogPen() {
    style = PenConstants.PS_SOLID;
    width = 0;
    color = Color.black;
  }

  /**
   * Return one of the PS_ styles.
   */
  public int getStyle() {
    return style;
  }

  public void setStyle( final int style ) {
    this.style = style & 0x000000FF;
    this.endCap = style & 0x00000F00;
    this.joinType = style & 0x0000F000;
  }

  /**
   * Return width.
   */
  public int getWidth() {
    return width;
  }

  public void setWidth( final int width ) {
    this.width = width;
  }

  /**
   * Return color of the current pen, or null.
   */
  public Color getColor() {
    return color;
  }

  public void setColor( final Color color ) {
    this.color = color;
  }

  /**
   * True if  not a dashed or dotted style.
   */
  public boolean isSimpleStyle() {
    switch( style ) {
      case PenConstants.PS_SOLID:
      case PenConstants.PS_NULL:
      case PenConstants.PS_INSIDEFRAME:
        return true;
      default:
        return false;
    }
  }

  public boolean isVisible() {
    return getStyle() != PenConstants.PS_NULL;
  }

  public int getType() {
    return OBJ_PEN;
  }

  public Stroke getStroke() {
    if ( isSimpleStyle() ) {
      return new BasicStroke( getWidth(), getEndCap(), getJoinType(), 0 );
    }
    return new BasicStroke( getWidth(), getEndCap(), getJoinType(), 0, getDashes(), 0 );
  }

  private int getJoinType() {
    switch( joinType ) {
      case PenConstants.PS_JOIN_ROUND:
        return BasicStroke.JOIN_ROUND;
      case PenConstants.PS_JOIN_BEVEL:
        return BasicStroke.JOIN_BEVEL;
      case PenConstants.PS_JOIN_MITER:
        return BasicStroke.JOIN_MITER;
      default:
        return BasicStroke.JOIN_ROUND;
    }
  }

  private int getEndCap() {
    switch( endCap ) {
      case PenConstants.PS_ENDCAP_ROUND:
        return BasicStroke.CAP_ROUND;
      case PenConstants.PS_ENDCAP_SQUARE:
        return BasicStroke.CAP_SQUARE;
      case PenConstants.PS_ENDCAP_FLAT:
        return BasicStroke.CAP_BUTT;
      default:
        return BasicStroke.CAP_ROUND;
    }
  }

  private float[] getDashes() {
    switch( getStyle() ) {
      case PenConstants.PS_DASH:
        return DASH_DASH;
      case PenConstants.PS_DOT:
        return DASH_DOT;
      case PenConstants.PS_DASHDOT:
        return DASH_DASHDOT;
      case PenConstants.PS_DASHDOTDOT:
        return DASH_DASHDOTDOT;
      default:
        throw new IllegalStateException( "Illegal Pen defined" );
    }
  }

  public String toString() {
    final StringBuffer b = new StringBuffer( 100 );
    b.append( "MfLogPen:=" );
    b.append( " width=" );
    b.append( getWidth() );
    b.append( " style=" );
    b.append( getStyle() );
    b.append( " color=" );
    b.append( getColor() );
    return b.toString();
  }

}
