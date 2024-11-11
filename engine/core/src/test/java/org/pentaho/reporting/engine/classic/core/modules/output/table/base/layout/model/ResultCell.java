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


package org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model;

import junit.framework.Assert;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;

import java.awt.*;

/**
 * Creation-Date: 20.08.2007, 15:40:12
 *
 * @author Thomas Morgner
 */
public class ResultCell {
  private BorderEdge top;
  private BorderEdge left;
  private BorderEdge bottom;
  private BorderEdge right;

  private BorderCorner topLeft;
  private BorderCorner topRight;
  private BorderCorner bottomLeft;
  private BorderCorner bottomRight;

  private Color backgroundColor;
  private ElementAlignment verticalAlignment;

  private String name;
  private String anchor;

  public ResultCell() {
    topLeft = new BorderCorner( 0, 0 );
    topRight = new BorderCorner( 0, 0 );
    bottomLeft = new BorderCorner( 0, 0 );
    bottomRight = new BorderCorner( 0, 0 );

    right = new BorderEdge( BorderStyle.NONE, Color.BLACK, 0 );
    top = new BorderEdge( BorderStyle.NONE, Color.BLACK, 0 );
    left = new BorderEdge( BorderStyle.NONE, Color.BLACK, 0 );
    bottom = new BorderEdge( BorderStyle.NONE, Color.BLACK, 0 );
  }

  public BorderEdge getTop() {
    return top;
  }

  public void setTop( final BorderEdge top ) {
    this.top = top;
  }

  public BorderEdge getLeft() {
    return left;
  }

  public void setLeft( final BorderEdge left ) {
    this.left = left;
  }

  public BorderEdge getBottom() {
    return bottom;
  }

  public void setBottom( final BorderEdge bottom ) {
    this.bottom = bottom;
  }

  public BorderEdge getRight() {
    return right;
  }

  public void setRight( final BorderEdge right ) {
    this.right = right;
  }

  public BorderCorner getTopLeft() {
    return topLeft;
  }

  public void setTopLeft( final BorderCorner topLeft ) {
    this.topLeft = topLeft;
  }

  public BorderCorner getTopRight() {
    return topRight;
  }

  public void setTopRight( final BorderCorner topRight ) {
    this.topRight = topRight;
  }

  public BorderCorner getBottomLeft() {
    return bottomLeft;
  }

  public void setBottomLeft( final BorderCorner bottomLeft ) {
    this.bottomLeft = bottomLeft;
  }

  public BorderCorner getBottomRight() {
    return bottomRight;
  }

  public void setBottomRight( final BorderCorner bottomRight ) {
    this.bottomRight = bottomRight;
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor( final Color backgroundColor ) {
    this.backgroundColor = backgroundColor;
  }

  public ElementAlignment getVerticalAlignment() {
    return verticalAlignment;
  }

  public void setVerticalAlignment( final ElementAlignment verticalAlignment ) {
    this.verticalAlignment = verticalAlignment;
  }

  public String getName() {
    return name;
  }

  public void setName( final String name ) {
    this.name = name;
  }

  public String getAnchor() {
    return anchor;
  }

  public void setAnchor( final String anchor ) {
    this.anchor = anchor;
  }

  public void assertValidity( final CellBackground tcd ) {
    if ( tcd == null ) {
      Assert.assertNull( "If we have no background, name must be null: " + name, name );
    } else {
      Assert.assertNotNull( "Object is null", tcd );

      final String[] anchors = tcd.getAnchors();
      if ( anchors.length > 0 ) {
        final StringBuffer anchorText = new StringBuffer( 100 );
        for ( int i = 0; i < anchors.length; i++ ) {
          final String anchor = anchors[i];
          if ( i == 0 ) {
            anchorText.append( ' ' );
          }
          anchorText.append( anchor );
        }
        Assert.assertEquals( "Ancor", anchorText.toString(), anchor );
      }

      Assert.assertEquals( "backgroundColor", tcd.getBackgroundColor(), backgroundColor );
      Assert.assertEquals( "bottomLeft", tcd.getBottomLeft(), bottomLeft );
      Assert.assertEquals( "bottomRight", tcd.getBottomRight(), bottomRight );
      // Assert.assertEquals("Name", tcd.getName(), name);

      Assert.assertEquals( "bottom", tcd.getBottom(), bottom );
      Assert.assertEquals( "Left", tcd.getLeft(), left );
      Assert.assertEquals( "right", tcd.getRight(), right );
      Assert.assertEquals( "top", tcd.getTop(), top );

      Assert.assertEquals( "topLeft", tcd.getTopLeft(), topLeft );
      Assert.assertEquals( "topRight", tcd.getTopRight(), topRight );
      // Assert.assertEquals("verticalAlignment", tcd.getVerticalAlignment(), verticalAlignment);
    }
  }
}
