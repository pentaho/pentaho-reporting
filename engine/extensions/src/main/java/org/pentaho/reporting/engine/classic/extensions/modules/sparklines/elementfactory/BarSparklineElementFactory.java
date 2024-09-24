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
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.BarSparklineType;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineAttributeNames;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineStyleKeys;

public class BarSparklineElementFactory extends AbstractContentElementFactory {
  private Object content;
  private String fieldname;
  private String formula;
  private Object nullValue;

  private Color highColor;
  private Color lastColor;
  private Integer spacing;

  public BarSparklineElementFactory() {
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

  public Color getLastColor() {
    return lastColor;
  }

  public void setLastColor( final Color lastColor ) {
    this.lastColor = lastColor;
  }

  public Integer getSpacing() {
    return spacing;
  }

  public void setSpacing( final Integer spacing ) {
    this.spacing = spacing;
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
    if ( lastColor != null ) {
      style.setStyleProperty( SparklineStyleKeys.LAST_COLOR, getLastColor() );
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

    element.setElementType( new BarSparklineType() );
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
    if ( spacing != null ) {
      element.setAttribute( SparklineAttributeNames.NAMESPACE, SparklineAttributeNames.SPACING, spacing );
    }

    return element;
  }
}
