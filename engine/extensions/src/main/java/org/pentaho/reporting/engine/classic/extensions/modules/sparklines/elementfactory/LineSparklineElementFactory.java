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

package org.pentaho.reporting.engine.classic.extensions.modules.sparklines.elementfactory;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.elementfactory.AbstractContentElementFactory;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.LineSparklineType;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineAttributeNames;

public class LineSparklineElementFactory extends AbstractContentElementFactory {
  private Object content;
  private String fieldname;
  private String formula;
  private Object nullValue;

  private Integer spacing;

  public LineSparklineElementFactory() {
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
   * Creates a new instance of the element. Override this method to return a concrete subclass of the element.
   *
   * @return the newly generated instance of the element.
   */
  public Element createElement() {
    final Element element = new Element();
    applyElementName( element );
    applyStyle( element.getStyle() );

    element.setElementType( new LineSparklineType() );
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
