/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.elementfactory;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentFieldType;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;

import java.net.URL;

/**
 * The drawable field element factory can be used to create elements that display <code>Drawable</code> elements.
 * <p/>
 * A drawable field expects the named datasource to contain Drawable objects.
 * <p/>
 * Once the desired properties are set, the factory can be reused to create similiar elements.
 *
 * @author Thomas Morgner
 */
public class ContentFieldElementFactory extends AbstractContentElementFactory {
  /**
   * The fieldname of the datarow from where to read the element data.
   */
  private String fieldname;
  /**
   * The value-formula that computes the value for this element.
   */
  private String formula;
  private Object nullValue;

  /**
   * The base URL is used to resolve relative URLs.
   */
  private URL baseURL;

  /**
   * DefaultConstructor.
   */
  public ContentFieldElementFactory() {
  }

  /**
   * Returns the field name from where to read the content of the element.
   *
   * @return the field name.
   */
  public String getFieldname() {
    return fieldname;
  }

  /**
   * Defines the field name from where to read the content of the element. The field name is the name of a datarow
   * column.
   *
   * @param fieldname
   *          the field name.
   */
  public void setFieldname( final String fieldname ) {
    this.fieldname = fieldname;
  }

  /**
   * Returns the formula that should be used to compute the value of the field. The formula must be valid according to
   * the OpenFormula specifications.
   *
   * @return the formula as string.
   */
  public String getFormula() {
    return formula;
  }

  /**
   * Returns the base url. The BaseURL is used to resolve relative URLs found in the datasource.
   *
   * @return the base url.
   */
  public URL getBaseURL() {
    return baseURL;
  }

  /**
   * Defines a BaseURL for the new element. The BaseURL is used to resolve relative URLs found in the datasource.
   *
   * @param baseURL
   *          the base URL.
   */
  public void setBaseURL( final URL baseURL ) {
    this.baseURL = baseURL;
  }

  /**
   * Assigns a formula to the element to compute the value for this element. If a formula is defined, it will override
   * the 'field' property.
   *
   * @param formula
   *          the formula as a string.
   */
  public void setFormula( final String formula ) {
    this.formula = formula;
  }

  public Object getNullValue() {
    return nullValue;
  }

  public void setNullValue( final Object nullValue ) {
    this.nullValue = nullValue;
  }

  /**
   * Creates a new drawable field element based on the defined properties.
   *
   * @return the generated elements
   * @throws IllegalStateException
   *           if the field name is not set.
   * @see ElementFactory#createElement()
   */
  public Element createElement() {
    final Element element = new Element();
    applyElementName( element );
    applyStyle( element.getStyle() );

    element.setElementType( new ContentFieldType() );
    if ( getFieldname() != null ) {
      element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, getFieldname() );
    }
    if ( getFormula() != null ) {
      final FormulaExpression formulaExpression = new FormulaExpression();
      formulaExpression.setFormula( getFormula() );
      element.setAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, formulaExpression );
    }
    if ( getNullValue() != null ) {
      element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE, getNullValue() );
    }
    if ( getBaseURL() != null ) {
      element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.CONTENT_BASE, getBaseURL() );
    }
    return element;
  }
}
