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

package org.pentaho.reporting.engine.classic.extensions.modules.sparklines.elementfactory;

import java.awt.Color;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.elementfactory.AbstractContentElementFactory;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.PieSparklineType;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineAttributeNames;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineStyleKeys;

public class PieSparklineElementFactory extends AbstractContentElementFactory {
  private Object content;
  private String fieldname;
  private String formula;
  private Object nullValue;

  private Color highColor;
  private Color lowColor;
  private Color mediumColor;
  private Double highSlice;
  private Double mediumSlice;
  private Double lowSlice;
  private Integer startAngle;
  private Boolean counterClockwise;

  public PieSparklineElementFactory() {
  }

  public Object getContent() {
    return content;
  }

  public void setContent( final Object content ) {
    this.content = content;
  }

  public Object getNullValue() {
    return nullValue;
  }

  public void setNullValue( final Object nullValue ) {
    this.nullValue = nullValue;
  }

  public String getFormula() {
    return formula;
  }

  public void setFormula( final String formula ) {
    this.formula = formula;
  }

  public Color getHighColor() {
    return highColor;
  }

  public void setHighColor( final Color highColor ) {
    this.highColor = highColor;
  }

  public Color getLowColor() {
    return lowColor;
  }

  public void setLowColor( final Color lowColor ) {
    this.lowColor = lowColor;
  }

  public Color getMediumColor() {
    return mediumColor;
  }

  public void setMediumColor( Color mediumColor ) {
    this.mediumColor = mediumColor;
  }

  public Integer getStartAngle() {
    return startAngle;
  }

  public void setStartAngle( final Integer startAngle ) {
    this.startAngle = startAngle;
  }

  public Double getHighSlice() {
    return highSlice;
  }

  public void setHighSlice( Double highSlice ) {
    this.highSlice = highSlice;
  }

  public Double getMediumSlice() {
    return mediumSlice;
  }

  public void setMediumSlice( Double mediumSlice ) {
    this.mediumSlice = mediumSlice;
  }

  public Double getLowSlice() {
    return lowSlice;
  }

  public void setLowSlice( Double lowSlice ) {
    this.lowSlice = lowSlice;
  }

  public Boolean getCounterClockwise() {
    return counterClockwise;
  }

  public void setCounterClockwise( Boolean counterClockwise ) {
    this.counterClockwise = counterClockwise;
  }

  public String getFieldname() {
    return fieldname;
  }

  public void setFieldname( final String fieldname ) {
    this.fieldname = fieldname;
  }

  /**
   * Applies the style definition to the elements stylesheet.
   *
   * @param style
   *          the element stylesheet which should receive the style definition.
   */
  protected void applyStyle( final ElementStyleSheet style ) {
    super.applyStyle( style );
    if ( highColor != null ) {
      style.setStyleProperty( SparklineStyleKeys.HIGH_COLOR, getHighColor() );
    }
    if ( lowColor != null ) {
      style.setStyleProperty( SparklineStyleKeys.LOW_COLOR, getLowColor() );
    }
    if ( mediumColor != null ) {
      style.setStyleProperty( SparklineStyleKeys.MEDIUM_COLOR, getMediumColor() );
    }
  }

  /**
   * Creates a new instance of the element. Override this method to return a concrete subclass of the element.
   *
   * @return the newly generated instance of the element.
   */
  public Element createElement() {
    final Element element = new Element();
    applyElementName( element );
    applyStyle( element.getStyle() );

    element.setElementType( new PieSparklineType() );
    if ( getContent() != null ) {
      element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, getContent() );
    }
    if ( getFieldname() != null ) {
      element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, getFieldname() );
    }
    if ( getFormula() != null ) {
      final FormulaExpression formulaExpression = new FormulaExpression();
      formulaExpression.setFormula( getFormula() );
      element.setAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, formulaExpression );
    }
    if ( startAngle != null ) {
      element.setAttribute( SparklineAttributeNames.NAMESPACE, SparklineAttributeNames.START_ANGLE, startAngle );
    }
    if ( lowSlice != null ) {
      element.setAttribute( SparklineAttributeNames.NAMESPACE, SparklineAttributeNames.LOW_SLICE, lowSlice );
    }
    if ( mediumSlice != null ) {
      element.setAttribute( SparklineAttributeNames.NAMESPACE, SparklineAttributeNames.MEDIUM_SLICE, mediumSlice );
    }
    if ( highSlice != null ) {
      element.setAttribute( SparklineAttributeNames.NAMESPACE, SparklineAttributeNames.HIGH_SLICE, highSlice );
    }
    if ( counterClockwise != null ) {
      element.setAttribute( SparklineAttributeNames.NAMESPACE, SparklineAttributeNames.COUNTER_CLOCKWISE,
          counterClockwise );
    }

    return element;
  }
}
