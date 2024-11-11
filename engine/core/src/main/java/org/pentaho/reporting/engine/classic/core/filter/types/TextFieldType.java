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


package org.pentaho.reporting.engine.classic.core.filter.types;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;

import javax.swing.text.Document;

public class TextFieldType extends AbstractElementType implements RotatableText {
  public static final ElementType INSTANCE = new TextFieldType();

  public TextFieldType() {
    super( "text-field" );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final Object staticValue = ElementTypeUtils.queryStaticValue( element );
    if ( staticValue != null ) {
      return rotate( element, staticValue, runtime );
    }
    final Object value = ElementTypeUtils.queryFieldName( element );
    return rotate( element, value != null ? value : getId(), runtime );
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   *          the element from which to read attribute.
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( runtime == null ) {
      throw new NullPointerException( "Runtime must never be null." );
    }
    if ( element == null ) {
      throw new NullPointerException( "Element must never be null." );
    }

    final Object o = ElementTypeUtils.queryFieldOrValue( runtime, element );
    if ( o instanceof Document ) {
      return rotate( element, o, runtime );
    }

    final String retval = ElementTypeUtils.toString( o );
    if ( retval == null ) {
      return rotate( element, element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE ), runtime );
    }
    return rotate( element, retval, runtime );
  }
}
