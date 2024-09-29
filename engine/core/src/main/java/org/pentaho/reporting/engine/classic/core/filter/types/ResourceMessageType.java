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


package org.pentaho.reporting.engine.classic.core.filter.types;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.ResourceMessageFormatFilter;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;

public class ResourceMessageType extends AbstractElementType implements RotatableText {
  private transient ElementMetaData elementType;

  public static final ElementType INSTANCE = new ResourceMessageType();

  public ResourceMessageType() {
    super( "resource-message" );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final Object resourceMessageRaw = ElementTypeUtils.queryStaticValue( element );
    if ( resourceMessageRaw == null ) {
      return rotate( element, "<null>", runtime );
    }
    return rotate( element, resourceMessageRaw.toString(), runtime );
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

    final Object nullValue = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE );
    final Object message = ElementTypeUtils.queryStaticValue( element );
    if ( message == null ) {
      return rotate( element, nullValue, runtime );
    }

    final Object resourceId =
        element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.RESOURCE_IDENTIFIER );
    if ( resourceId == null ) {
      return rotate( element, nullValue, runtime );
    }

    final ResourceMessageFormatFilter messageFormatFilter =
        element.getElementContext( ResourceMessageFormatFilter.class );
    messageFormatFilter.setFormatKey( String.valueOf( message ) );
    messageFormatFilter.setResourceIdentifier( String.valueOf( resourceId ) );

    final Object messageNullValue =
        element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.MESSAGE_NULL_VALUE );
    if ( messageNullValue != null ) {
      messageFormatFilter.setNullString( String.valueOf( messageNullValue ) );
    } else if ( nullValue != null ) {
      messageFormatFilter.setNullString( String.valueOf( nullValue ) );
    } else {
      messageFormatFilter.setNullString( null );
    }

    final Object value = messageFormatFilter.getValue( runtime, element );
    if ( value == null ) {
      return rotate( element, nullValue, runtime );
    }
    return rotate( element, value, runtime );
  }
}
