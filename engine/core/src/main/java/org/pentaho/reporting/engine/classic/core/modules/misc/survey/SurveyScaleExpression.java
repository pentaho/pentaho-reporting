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

package org.pentaho.reporting.engine.classic.core.modules.misc.survey;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.libraries.serializer.SerializerHelper;

/**
 * An expression that takes values from one or more fields in the current row of the report, builds a
 * {@link SurveyScale} instance that will present those values, and returns that instance as the expression result. The
 * fields used by the expression are defined using properties named '0', '1', ... 'N', which need to be specified after
 * the expression is created. These fields should contain {@link Number} instances.The {@link SurveyScale} class
 * implements the Drawable interface, so it can be displayed using a
 * {@link org.pentaho.reporting.engine.classic.core.filter.types.ContentFieldType}.
 *
 * @noinspection UnusedDeclaration
 */
public class SurveyScaleExpression extends AbstractExpression {
  /**
   * The lowest value on the scale.
   */
  private int lowest;

  /**
   * The highest value on the scale.
   */
  private int highest;

  /**
   * An ordered list containing the fieldnames used in the expression.
   */
  private ArrayList<String> fieldList;

  /**
   * The name of the field containing the lower bound of the highlighted range.
   */
  private String rangeLowerBoundField;

  /**
   * The name of the field containing the upper bound of the highlighted range.
   */
  private String rangeUpperBoundField;

  /**
   * An optional shape that is used (if present) for the first data value.
   */
  private transient Shape overrideShape;

  /**
   * A flag that controls whether or not the override shape is filled or not filled.
   */
  private boolean overrideShapeFilled;

  /**
   * The font used to display the scale values.
   */
  private Font scaleValueFont;

  /**
   * The paint used to draw the scale values.
   */
  private Color scaleValuePaint;

  /**
   * The range paint.
   */
  private Color rangeColor;

  /**
   * The fill paint.
   */
  private Color fillPaint;

  /**
   * The tick mark paint.
   */
  private Color tickMarkPaint;

  private boolean autoConfigure;
  private double upperMargin;
  private double lowerMargin;

  private ArrayList<Boolean> fillShapes;
  private boolean drawTickMarks;
  private boolean drawScaleValues;
  private ArrayList<SurveyScaleShapeType> shapes;
  private SurveyScaleShapeType defaultShape;
  private transient BasicStroke outlineStroke;

  public SurveyScaleExpression() {
    this( 0, 1 );
  }

  /**
   * Creates a new expression.
   *
   * @param lowest
   *          the lowest value on the response scale.
   * @param highest
   *          the highest value on the response scale.
   */
  public SurveyScaleExpression( final int lowest, final int highest ) {
    this( lowest, highest, null, null, null );
  }

  /**
   * Creates a new expression.
   *
   * @param lowest
   *          the lowest value on the response scale.
   * @param highest
   *          the highest value on the response scale.
   * @param lowerBoundsField
   *          the name of the field containing the lower bound of the highlighted range (<code>null</code> permitted).
   * @param upperBoundsField
   *          the name of the field containing the upper bound of the highlighted range (<code>null</code> permitted).
   * @param shape
   *          a shape that will be used to override the shape displayed for the first series (<code>null</code>
   *          permitted).
   */
  public SurveyScaleExpression( final int lowest, final int highest, final String lowerBoundsField,
      final String upperBoundsField, final Shape shape ) {
    this.fillShapes = new ArrayList<Boolean>();
    this.shapes = new ArrayList<SurveyScaleShapeType>();
    this.lowest = lowest;
    this.highest = highest;
    this.fieldList = new ArrayList<String>();
    this.overrideShape = shape;
    this.overrideShapeFilled = false;
    this.rangeLowerBoundField = lowerBoundsField;
    this.rangeUpperBoundField = upperBoundsField;
    this.rangeColor = Color.lightGray;
    this.tickMarkPaint = Color.gray;
    this.scaleValuePaint = Color.black;
    this.upperMargin = 0.1;
    this.lowerMargin = 0.1;
    this.defaultShape = SurveyScaleShapeType.DownTriangle;
    this.outlineStroke = new BasicStroke( 0.5f );
    this.fillPaint = Color.BLACK;
  }

  public SurveyScaleShapeType getDefaultShape() {
    return defaultShape;
  }

  public void setDefaultShape( final SurveyScaleShapeType defaultShape ) {
    this.defaultShape = defaultShape;
  }

  public void setFillShapes( final int index, final boolean fill ) {
    if ( fillShapes.size() == index ) {
      fillShapes.add( fill );
    } else {
      fillShapes.set( index, fill );
    }
  }

  public boolean getFillShapes( final int index ) {
    return fillShapes.get( index );
  }

  public int getFillShapesCount() {
    return fillShapes.size();
  }

  public boolean[] getFillShapes() {
    final boolean[] retval = new boolean[fillShapes.size()];
    for ( int i = 0; i < retval.length; i++ ) {
      retval[i] = Boolean.TRUE.equals( fillShapes.get( i ) );

    }
    return retval;
  }

  public void setFillShapes( final boolean[] fields ) {
    this.fillShapes.clear();
    for ( int i = 0; i < fields.length; i++ ) {
      this.fillShapes.add( fields[i] );

    }
  }

  public void setShapes( final int index, final SurveyScaleShapeType fill ) {
    if ( shapes.size() == index ) {
      shapes.add( fill );
    } else {
      shapes.set( index, fill );
    }
  }

  public SurveyScaleShapeType getShapes( final int index ) {
    return shapes.get( index );
  }

  public int getShapesCount() {
    return shapes.size();
  }

  public SurveyScaleShapeType[] getShapes() {
    final SurveyScaleShapeType[] retval = new SurveyScaleShapeType[shapes.size()];
    for ( int i = 0; i < retval.length; i++ ) {
      retval[i] = shapes.get( i );
    }
    return retval;
  }

  public void setShapes( final SurveyScaleShapeType[] fields ) {
    this.shapes.clear();
    for ( int i = 0; i < fields.length; i++ ) {
      this.shapes.add( fields[i] );

    }
  }

  public boolean isAutoConfigure() {
    return autoConfigure;
  }

  public void setAutoConfigure( final boolean autoConfigure ) {
    this.autoConfigure = autoConfigure;
  }

  /**
   * Returns the name of the field containing the lower bound of the range that is to be highlighted on the scale.
   *
   * @return A string (possibly <code>null</code>).
   */
  public String getRangeLowerBoundField() {
    return this.rangeLowerBoundField;
  }

  /**
   * Sets the name of the field containing the lower bound of the range that is to be highlighted on the scale. Set this
   * to <code>null</code> if you have no range to highlight.
   *
   * @param field
   *          the field name (<code>null</code> permitted).
   */
  public void setRangeLowerBoundField( final String field ) {
    this.rangeLowerBoundField = field;
  }

  /**
   * Returns the name of the field containing the upper bound of the range that is to be highlighted on the scale.
   *
   * @return A string (possibly <code>null</code>).
   */
  public String getRangeUpperBoundField() {
    return this.rangeUpperBoundField;
  }

  /**
   * Sets the name of the field containing the upper bound of the range that is to be highlighted on the scale. Set this
   * to <code>null</code> if you have no range to highlight.
   *
   * @param field
   *          the field name (<code>null</code> permitted).
   */
  public void setRangeUpperBoundField( final String field ) {
    this.rangeUpperBoundField = field;
  }

  /**
   * Returns the override shape.
   *
   * @return The override shape (possibly <code>null</code>).
   */
  public Shape getOverrideShape() {
    return this.overrideShape;
  }

  /**
   * Sets the override shape. The {@link SurveyScale} is created with a set of default shapes, this method allows you to
   * replace the *first* shape if you need to (leave it as <code>null</code> otherwise).
   *
   * @param shape
   *          the shape (<code>null</code> permitted).
   */
  public void setOverrideShape( final Shape shape ) {
    this.overrideShape = shape;
  }

  public boolean isOverrideShapeFilled() {
    return overrideShapeFilled;
  }

  /**
   * Sets a flag that controls whether the override shape is filled or not.
   *
   * @param b
   *          the flag.
   */
  public void setOverrideShapeFilled( final boolean b ) {
    this.overrideShapeFilled = b;
  }

  public int getLowest() {
    return lowest;
  }

  public void setLowest( final int lowest ) {
    this.lowest = lowest;
  }

  public int getHighest() {
    return highest;
  }

  public void setHighest( final int highest ) {
    this.highest = highest;
  }

  public Font getScaleValueFont() {
    return scaleValueFont;
  }

  public void setScaleValueFont( final Font scaleValueFont ) {
    this.scaleValueFont = scaleValueFont;
  }

  public Color getScaleValuePaint() {
    return scaleValuePaint;
  }

  public void setScaleValuePaint( final Color scaleValuePaint ) {
    this.scaleValuePaint = scaleValuePaint;
  }

  public Color getRangeColor() {
    return rangeColor;
  }

  public void setRangeColor( final Color rangeColor ) {
    this.rangeColor = rangeColor;
  }

  public Color getFillPaint() {
    return fillPaint;
  }

  public void setFillPaint( final Color fillPaint ) {
    this.fillPaint = fillPaint;
  }

  public Color getTickMarkPaint() {
    return tickMarkPaint;
  }

  public void setTickMarkPaint( final Color tickMarkPaint ) {
    this.tickMarkPaint = tickMarkPaint;
  }

  public double getUpperMargin() {
    return upperMargin;
  }

  public void setUpperMargin( final double upperMargin ) {
    this.upperMargin = upperMargin;
  }

  public double getLowerMargin() {
    return lowerMargin;
  }

  public void setLowerMargin( final double lowerMargin ) {
    this.lowerMargin = lowerMargin;
  }

  public boolean isDrawTickMarks() {
    return drawTickMarks;
  }

  public void setDrawTickMarks( final boolean drawTickMarks ) {
    this.drawTickMarks = drawTickMarks;
  }

  public boolean isDrawScaleValues() {
    return drawScaleValues;
  }

  public void setDrawScaleValues( final boolean drawScaleValues ) {
    this.drawScaleValues = drawScaleValues;
  }

  public BasicStroke getOutlineStroke() {
    return outlineStroke;
  }

  public void setOutlineStroke( final BasicStroke outlineStroke ) {
    this.outlineStroke = outlineStroke;
  }

  /**
   * Returns a {@link SurveyScale} instance that is set up to display the values in the current row.
   *
   * @return a {@link SurveyScale} instance.
   */
  public Object getValue() {
    final SurveyScale result = new SurveyScale( this.lowest, this.highest, collectValues() );

    if ( this.rangeLowerBoundField != null && this.rangeUpperBoundField != null ) {
      final DataRow dataRow = getDataRow();
      final Object b0 = dataRow.get( this.rangeLowerBoundField );
      final Object b1 = dataRow.get( this.rangeUpperBoundField );
      if ( b0 instanceof Number ) {
        result.setRangeLowerBound( (Number) b0 );
      }
      if ( b1 instanceof Number ) {
        result.setRangeUpperBound( (Number) b1 );
      }
    }
    result.setRangePaint( this.rangeColor );
    result.setTickMarkPaint( this.tickMarkPaint );
    result.setFillPaint( this.fillPaint );
    result.setScaleValueFont( this.scaleValueFont );
    result.setScaleValuePaint( this.scaleValuePaint );
    result.setUpperMargin( this.upperMargin );
    result.setLowerMargin( this.lowerMargin );
    result.setDrawScaleValues( this.drawScaleValues );
    result.setDrawTickMarks( this.drawTickMarks );
    result.setDefaultShape( this.defaultShape );
    if ( this.overrideShape != null ) {
      result.setShape( 0, this.overrideShape );
      result.setShapeFilled( 0, this.overrideShapeFilled );
    }

    for ( int i = 0; i < fillShapes.size(); i++ ) {
      final Boolean fill = fillShapes.get( i );
      result.setShapeFilled( i, Boolean.TRUE.equals( fill ) );
    }

    return result;
  }

  /**
   * collects the values of all fields defined in the fieldList.
   *
   * @return an Objectarray containing all defined values from the datarow
   */
  private Number[] collectValues() {
    final Number[] retval = new Number[this.fieldList.size()];
    for ( int i = 0; i < this.fieldList.size(); i++ ) {
      final String field = this.fieldList.get( i );
      retval[i] = (Number) getDataRow().get( field );
    }
    return retval;
  }

  /**
   * Clones the expression.
   *
   * @return a copy of this expression.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public Object clone() throws CloneNotSupportedException {
    final SurveyScaleExpression fva = (SurveyScaleExpression) super.clone();
    fva.fieldList = (ArrayList<String>) this.fieldList.clone();
    fva.fillShapes = (ArrayList<Boolean>) this.fillShapes.clone();
    fva.shapes = (ArrayList<SurveyScaleShapeType>) this.shapes.clone();
    return fva;
  }

  public String[] getField() {
    return fieldList.toArray( new String[fieldList.size()] );
  }

  public void setField( final String[] fields ) {
    this.fieldList.clear();
    this.fieldList.addAll( Arrays.asList( fields ) );
  }

  public String getField( final int idx ) {
    return this.fieldList.get( idx );
  }

  public void setField( final int index, final String field ) {
    if ( fieldList.size() == index ) {
      fieldList.add( field );
    } else {
      fieldList.set( index, field );
    }
  }

  @Deprecated
  public Paint getRangePaint() {
    return rangeColor;
  }

  @Deprecated
  public void setRangePaint( final Paint rangePaint ) {
    if ( rangePaint == null ) {
      throw new NullPointerException();
    }
    if ( rangePaint instanceof Color ) {
      this.rangeColor = (Color) rangePaint;
    }
  }

  private void writeObject( final ObjectOutputStream out ) throws IOException {
    out.defaultWriteObject();
    final SerializerHelper helper = SerializerHelper.getInstance();
    helper.writeObject( outlineStroke, out );
  }

  private void readObject( final ObjectInputStream in ) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    final SerializerHelper helper = SerializerHelper.getInstance();
    outlineStroke = (BasicStroke) helper.readObject( in );
  }
}
