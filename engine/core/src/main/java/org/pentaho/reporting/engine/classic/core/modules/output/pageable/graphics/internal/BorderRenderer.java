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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

/**
 * Creation-Date: 28.10.2007, 15:52:19
 *
 * @author Thomas Morgner
 */
public class BorderRenderer {
  private static final AffineTransform scaleInstance;

  static {
    final long conversionFactor = StrictGeomUtility.toInternalValue( 1 );
    scaleInstance = AffineTransform.getScaleInstance( 1.0 / conversionFactor, 1.0 / conversionFactor );
  }

  private boolean sameForAllSides;
  private Color backgroundColor;

  private Shape borderShape;
  private Shape borderShapeTop;
  private Shape borderShapeLeft;
  private Shape borderShapeBottom;
  private Shape borderShapeRight;

  private Arc2D reusableArc;

  // private StyleSheet styleSheet;
  private BoxDefinition boxDefinition;
  private long x;
  private long y;
  private long width;
  private long height;
  private StaticBoxLayoutProperties staticBoxLayoutProperties;

  public BorderRenderer() {
    reusableArc = new Arc2D.Double();
  }

  private void initialize( final RenderBox box ) {
    initialize( box.getStaticBoxLayoutProperties(), box.getBoxDefinition(), box.getStyleSheet(), box.getX(),
        box.getY(), box.getWidth(), box.getHeight() );
  }

  private void initialize( final StaticBoxLayoutProperties staticBoxLayoutProperties,
      final BoxDefinition boxDefinition, final StyleSheet styleSheet, final long x, final long y, final long width,
      final long height ) {
    this.staticBoxLayoutProperties = staticBoxLayoutProperties;
    this.boxDefinition = boxDefinition;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;

    this.sameForAllSides = boxDefinition.getBorder().isSameForAllSides();
    this.backgroundColor = (Color) styleSheet.getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR );
    this.borderShape = null;
    this.borderShapeTop = null;
    this.borderShapeLeft = null;
    this.borderShapeBottom = null;
    this.borderShapeRight = null;
  }

  private BasicStroke createStroke( final BorderEdge edge, final long internalWidth ) {
    final float effectiveWidth = (float) StrictGeomUtility.toExternalValue( internalWidth );
    if ( BorderStyle.HIDDEN.equals( edge.getBorderStyle() ) ) {
      return null;
    }

    if ( BorderStyle.DASHED.equals( edge.getBorderStyle() ) ) {
      return new BasicStroke( effectiveWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] {
        6 * effectiveWidth, 6 * effectiveWidth }, 0.0f );
    }
    if ( BorderStyle.DOTTED.equals( edge.getBorderStyle() ) ) {
      return new BasicStroke( effectiveWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 5.0f, new float[] { 0.0f,
        2 * effectiveWidth }, 0.0f );
    }
    if ( BorderStyle.DOT_DASH.equals( edge.getBorderStyle() ) ) {
      return new BasicStroke( effectiveWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] { 0,
        2 * effectiveWidth, 6 * effectiveWidth, 2 * effectiveWidth }, 0.0f );
    }
    if ( BorderStyle.DOT_DOT_DASH.equals( edge.getBorderStyle() ) ) {
      return new BasicStroke( effectiveWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] { 0,
        2 * effectiveWidth, 0, 2 * effectiveWidth, 6 * effectiveWidth, 2 * effectiveWidth }, 0.0f );
    }
    return new BasicStroke( effectiveWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
  }

  public void paintBackgroundAndBorder( final RenderBox box, final Graphics2D g2d ) {
    initialize( box );
    paint( g2d );
  }

  public void paintBackgroundAndBorder( final StaticBoxLayoutProperties staticBoxLayoutProperties,
      final BoxDefinition boxDefinition, final StyleSheet styleSheet, final long x, final long y, final long width,
      final long height, final Graphics2D g2d ) {
    initialize( staticBoxLayoutProperties, boxDefinition, styleSheet, x, y, width, height );
    paint( g2d );
  }

  private void paint( final Graphics2D g2d ) {
    final Border border = boxDefinition.getBorder();
    if ( backgroundColor == null && border.isEmpty() ) {
      return;
    }

    final Color oldColor = g2d.getColor();
    final Stroke oldStroke = g2d.getStroke();

    if ( isSameForAllSides() ) {
      final Shape borderShape = getBorderShape();

      if ( backgroundColor != null ) {
        g2d.setColor( backgroundColor );
        g2d.fill( borderShape );
      }

      if ( staticBoxLayoutProperties.getBorderTop() > 0 ) {
        final BorderEdge borderEdge = border.getTop();

        final BasicStroke basicStroke = createStroke( borderEdge, staticBoxLayoutProperties.getBorderTop() );
        if ( basicStroke != null ) {
          g2d.setColor( borderEdge.getColor() );
          g2d.setStroke( basicStroke );
          g2d.draw( borderShape );
        }
      }
      g2d.setColor( oldColor );
      g2d.setStroke( oldStroke );
      return;
    }

    if ( backgroundColor != null ) {
      final Shape borderShape = getBorderShape();
      g2d.setColor( backgroundColor );
      g2d.fill( borderShape );
    }

    final StaticBoxLayoutProperties sblp = this.staticBoxLayoutProperties;
    if ( sblp.getBorderTop() > 0 ) {
      final BorderEdge borderEdge = border.getTop();
      final BasicStroke basicStroke = createStroke( borderEdge, staticBoxLayoutProperties.getBorderTop() );
      if ( basicStroke != null ) {
        g2d.setColor( borderEdge.getColor() );
        g2d.setStroke( basicStroke );
        g2d.draw( getBorderTopShape() );
      }
    }

    if ( sblp.getBorderRight() > 0 ) {
      final BorderEdge borderEdge = border.getRight();
      final BasicStroke basicStroke = createStroke( borderEdge, staticBoxLayoutProperties.getBorderRight() );
      if ( basicStroke != null ) {
        g2d.setColor( borderEdge.getColor() );
        g2d.setStroke( basicStroke );
        g2d.draw( getBorderRightShape() );
      }
    }

    if ( sblp.getBorderBottom() > 0 ) {
      final BorderEdge borderEdge = border.getBottom();
      final BasicStroke basicStroke = createStroke( borderEdge, staticBoxLayoutProperties.getBorderBottom() );
      if ( basicStroke != null ) {
        g2d.setColor( borderEdge.getColor() );
        g2d.setStroke( basicStroke );
        g2d.draw( getBorderBottomShape() );
      }
    }

    if ( sblp.getBorderLeft() > 0 ) {
      final BorderEdge borderEdge = border.getLeft();
      final BasicStroke basicStroke = createStroke( borderEdge, staticBoxLayoutProperties.getBorderLeft() );
      if ( basicStroke != null ) {
        g2d.setColor( borderEdge.getColor() );
        g2d.setStroke( basicStroke );
        g2d.draw( getBorderLeftShape() );
      }
    }

    g2d.setColor( oldColor );
    g2d.setStroke( oldStroke );
  }

  private boolean isSameForAllSides() {
    return sameForAllSides;
  }

  private Arc2D configureArc( final double x, final double y, final double w, final double h, final double angSt,
      final double angExt, final int closure ) {
    reusableArc.setArc( x, y, w, h, angSt, angExt, closure );
    return reusableArc;
  }

  public Shape getBorderShape() {
    if ( borderShape != null ) {
      return borderShape;
    }

    final StaticBoxLayoutProperties sblp = this.staticBoxLayoutProperties;
    final long x = this.x + ( sblp.getBorderLeft() / 2 );
    final long y = this.y + ( sblp.getBorderTop() / 2 );
    final long w = this.width - ( ( sblp.getBorderLeft() + sblp.getBorderRight() ) / 2 );
    final long h = this.height - ( ( sblp.getBorderTop() + sblp.getBorderBottom() ) / 2 );

    final Border border = boxDefinition.getBorder();
    final long topLeftWidth = border.getTopLeft().getWidth();
    final long topLeftHeight = border.getTopLeft().getHeight();
    final long topRightWidth;
    final long topRightHeight;
    final long bottomLeftWidth;
    final long bottomLeftHeight;
    final long bottomRightWidth;
    final long bottomRightHeight;
    if ( isSameForAllSides() ) {
      topRightWidth = topLeftWidth;
      topRightHeight = topLeftHeight;
      bottomLeftWidth = topLeftWidth;
      bottomLeftHeight = topLeftHeight;
      bottomRightWidth = topLeftWidth;
      bottomRightHeight = topLeftHeight;
    } else {
      topRightWidth = border.getTopRight().getWidth();
      topRightHeight = border.getTopRight().getHeight();
      bottomLeftWidth = border.getBottomLeft().getWidth();
      bottomLeftHeight = border.getBottomLeft().getHeight();
      bottomRightWidth = border.getBottomRight().getWidth();
      bottomRightHeight = border.getBottomRight().getHeight();
    }

    if ( topLeftHeight == 0 && topRightHeight == 0 && topLeftWidth == 0 && topRightWidth == 0 && bottomLeftHeight == 0
        && bottomRightHeight == 0 && bottomLeftWidth == 0 && bottomRightWidth == 0 ) {
      borderShape =
          new Rectangle2D.Double( StrictGeomUtility.toExternalValue( x ), StrictGeomUtility.toExternalValue( y ),
              StrictGeomUtility.toExternalValue( w ), StrictGeomUtility.toExternalValue( h ) );
      return borderShape;
    }

    final GeneralPath generalPath = new GeneralPath( GeneralPath.WIND_NON_ZERO, 200 );
    generalPath.append( configureArc( x, y, 2 * topLeftWidth, 2 * topLeftHeight, -225, -45, Arc2D.OPEN ), true );
    generalPath.lineTo( (float) ( x + w - topRightWidth ), (float) y ); // 2
    generalPath.append( configureArc( x + w - 2 * topRightWidth, y, 2 * topRightWidth, 2 * topRightHeight, 90, -45,
        Arc2D.OPEN ), true );

    generalPath.append( configureArc( x + w - 2 * topRightWidth, y, 2 * topRightWidth, 2 * topRightHeight, 45, -45,
        Arc2D.OPEN ), true );
    generalPath.lineTo( (float) ( x + w ), (float) ( y + h - bottomRightHeight ) ); // 4
    generalPath.append( configureArc( x + w - 2 * bottomRightWidth, y + h - 2 * bottomRightHeight,
        2 * bottomRightWidth, 2 * bottomRightHeight, 0, -45, Arc2D.OPEN ), true );

    generalPath.append( configureArc( x + w - 2 * bottomRightWidth, y + h - 2 * bottomRightHeight,
        2 * bottomRightWidth, 2 * bottomRightHeight, -45, -45, Arc2D.OPEN ), true );
    generalPath.lineTo( (float) ( x + bottomLeftWidth ), (float) ( y + h ) ); // 6
    generalPath.append( configureArc( x, y + h - 2 * bottomLeftHeight, 2 * bottomLeftWidth, 2 * bottomLeftHeight, -90,
        -45, Arc2D.OPEN ), true );

    generalPath.append( configureArc( x, y + h - 2 * bottomLeftHeight, 2 * bottomLeftWidth, 2 * bottomLeftHeight, -135,
        -45, Arc2D.OPEN ), true );
    generalPath.lineTo( (float) x, (float) ( y + topLeftHeight ) ); // 8
    generalPath.append( configureArc( x, y, 2 * topLeftWidth, 2 * topLeftHeight, -180, -45, Arc2D.OPEN ), true );

    generalPath.closePath();
    generalPath.transform( BorderRenderer.scaleInstance );
    borderShape = generalPath;
    return generalPath;
  }

  public Shape getBorderTopShape() {
    if ( borderShapeTop != null ) {
      return borderShapeTop;
    }

    final StaticBoxLayoutProperties sblp = this.staticBoxLayoutProperties;
    final long halfBorderWidth = sblp.getBorderTop() / 2;
    final long x = this.x;
    final long y = this.y + halfBorderWidth;
    final long w = this.width;

    final Border border = boxDefinition.getBorder();
    final long topLeftWidth = border.getTopLeft().getWidth();
    final long topLeftHeight = border.getTopLeft().getHeight();
    final long topRightWidth = border.getTopRight().getWidth();
    final long topRightHeight = border.getTopRight().getHeight();

    if ( topLeftWidth == 0 && topRightWidth == 0 && topLeftHeight == 0 && topRightHeight == 0 ) {
      // Make a square corner
      final double lineX1 = StrictGeomUtility.toExternalValue( x );
      final double lineX2 = StrictGeomUtility.toExternalValue( x + w );
      final double lineY = StrictGeomUtility.toExternalValue( y );
      borderShapeTop = new Line2D.Double( lineX1, lineY, lineX2, lineY );
      return borderShapeTop;
    }

    // Make a rounded corner
    final GeneralPath generalPath = new GeneralPath( GeneralPath.WIND_NON_ZERO, 20 );
    generalPath.append( configureArc( x, y, 2 * topLeftWidth, 2 * topLeftHeight, -225, -45, Arc2D.OPEN ), true );
    generalPath.lineTo( (float) ( x + w - topRightWidth ), (float) y ); // 2
    generalPath.append( configureArc( x + w - 2 * topRightWidth, y, 2 * topRightWidth, 2 * topRightHeight, 90, -45,
        Arc2D.OPEN ), true );
    generalPath.transform( BorderRenderer.scaleInstance );
    borderShapeTop = generalPath;
    return generalPath;
  }

  public Shape getBorderBottomShape() {
    if ( borderShapeBottom != null ) {
      return borderShapeBottom;
    }

    final StaticBoxLayoutProperties sblp = this.staticBoxLayoutProperties;
    final long halfBorderWidth = sblp.getBorderBottom() / 2;
    final long x = this.x;
    final long y = this.y;
    final long w = this.width;
    final long h = this.height;

    final Border border = boxDefinition.getBorder();
    final long bottomLeftWidth = border.getBottomLeft().getWidth();
    final long bottomLeftHeight = border.getBottomLeft().getHeight();
    final long bottomRightWidth = border.getBottomRight().getWidth();
    final long bottomRightHeight = border.getBottomRight().getHeight();

    if ( bottomLeftWidth == 0 && bottomRightWidth == 0 && bottomLeftHeight == 0 && bottomRightHeight == 0 ) {
      // Make a square corner
      final double lineX1 = StrictGeomUtility.toExternalValue( x );
      final double lineX2 = StrictGeomUtility.toExternalValue( x + w );
      final double lineY = StrictGeomUtility.toExternalValue( y + h - halfBorderWidth );
      borderShapeBottom = new Line2D.Double( lineX1, lineY, lineX2, lineY );
      return borderShapeBottom;
    }

    // Make a rounded corner
    final GeneralPath generalPath = new GeneralPath( GeneralPath.WIND_NON_ZERO, 20 );
    generalPath.append( configureArc( x + w - 2 * bottomRightWidth, y + h - 2 * bottomRightHeight,
        2 * bottomRightWidth, 2 * bottomRightHeight, -45, -45, Arc2D.OPEN ), true );
    generalPath.lineTo( (float) ( x + bottomLeftWidth ), (float) ( y + h ) ); // 6
    generalPath.append( configureArc( x, y + h - 2 * bottomLeftHeight, 2 * bottomLeftWidth, 2 * bottomLeftHeight, -90,
        -45, Arc2D.OPEN ), true );
    generalPath.transform( BorderRenderer.scaleInstance );
    borderShapeBottom = generalPath;
    return generalPath;
  }

  public Shape getBorderLeftShape() {
    if ( borderShapeLeft != null ) {
      return borderShapeLeft;
    }

    final StaticBoxLayoutProperties sblp = this.staticBoxLayoutProperties;
    final long halfBorderWidth = sblp.getBorderLeft() / 2;
    final long x = this.x;
    final long y = this.y;
    final long h = this.height;

    final Border border = boxDefinition.getBorder();
    final long topLeftWidth = border.getTopLeft().getWidth();
    final long topLeftHeight = border.getTopLeft().getHeight();
    final long bottomLeftWidth = border.getBottomLeft().getWidth();
    final long bottomLeftHeight = border.getBottomLeft().getHeight();

    if ( bottomLeftWidth == 0 && topLeftWidth == 0 && bottomLeftHeight == 0 && topLeftHeight == 0 ) {
      // Make a square corner
      final double lineX = StrictGeomUtility.toExternalValue( x + halfBorderWidth );
      final double lineY1 = StrictGeomUtility.toExternalValue( y );
      final double lineY2 = StrictGeomUtility.toExternalValue( y + h );
      borderShapeLeft = new Line2D.Double( lineX, lineY1, lineX, lineY2 );
      return borderShapeLeft;
    }

    // Make a rounded corner
    final GeneralPath generalPath = new GeneralPath( GeneralPath.WIND_NON_ZERO, 20 );
    generalPath.append( configureArc( x, y + h - 2 * bottomLeftHeight, 2 * bottomLeftWidth, 2 * bottomLeftHeight, -135,
        -45, Arc2D.OPEN ), true );
    generalPath.lineTo( (float) x, (float) ( y + topLeftHeight ) ); // 8
    generalPath.append( configureArc( x, y, 2 * topLeftWidth, 2 * topLeftHeight, -180, -45, Arc2D.OPEN ), true );
    generalPath.transform( BorderRenderer.scaleInstance );
    borderShapeLeft = generalPath;
    return generalPath;
  }

  public Shape getBorderRightShape() {
    if ( borderShapeRight != null ) {
      return borderShapeRight;
    }

    final StaticBoxLayoutProperties sblp = this.staticBoxLayoutProperties;
    final long halfBorderWidth = sblp.getBorderRight() / 2;
    final long x = this.x;
    final long y = this.y;
    final long w = this.width;
    final long h = this.height;

    final Border border = boxDefinition.getBorder();
    final long topRightWidth = border.getTopRight().getWidth();
    final long topRightHeight = border.getTopRight().getHeight();
    final long bottomRightWidth = border.getBottomRight().getWidth();
    final long bottomRightHeight = border.getBottomRight().getHeight();

    if ( topRightWidth == 0 && bottomRightWidth == 0 && topRightHeight == 0 && bottomRightHeight == 0 ) {
      // Make a square corner
      final double lineX = StrictGeomUtility.toExternalValue( x + w - halfBorderWidth );
      final double lineY1 = StrictGeomUtility.toExternalValue( y );
      final double lineY2 = StrictGeomUtility.toExternalValue( y + h );
      borderShapeRight = new Line2D.Double( lineX, lineY1, lineX, lineY2 );
      return borderShapeRight;
    }

    // Make a rounded corner
    final GeneralPath generalPath = new GeneralPath( GeneralPath.WIND_NON_ZERO, 20 );
    generalPath.append( configureArc( x + w - 2 * topRightWidth, y, 2 * topRightWidth, 2 * topRightHeight, 45, -45,
        Arc2D.OPEN ), true );
    generalPath.lineTo( (float) ( x + w ), (float) ( y + h - bottomRightHeight ) ); // 4
    generalPath.append( configureArc( x + w - 2 * bottomRightWidth, y + h - 2 * bottomRightHeight,
        2 * bottomRightWidth, 2 * bottomRightHeight, 0, -45, Arc2D.OPEN ), true );
    generalPath.transform( BorderRenderer.scaleInstance );
    borderShapeRight = generalPath;
    return generalPath;
  }
}
