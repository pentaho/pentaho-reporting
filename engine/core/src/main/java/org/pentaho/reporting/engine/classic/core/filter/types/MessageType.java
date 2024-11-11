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
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.filter.MessageFormatSupport;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.Locale;
import java.util.TimeZone;

public class MessageType extends AbstractElementType implements RotatableText {
  public static final MessageType INSTANCE = new MessageType();

  public static class MessageTypeContext {
    public MessageTypeContext() {
      messageFormatFilter = new MessageFormatSupport();
    }

    public MessageFormatSupport messageFormatFilter;
  }

  public MessageType() {
    super( "message" );
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

    final MessageTypeContext context = element.getElementContext( MessageTypeContext.class );
    final MessageFormatSupport messageFormatFilter = context.messageFormatFilter;
    messageFormatFilter.setFormatString( String.valueOf( message ) );

    final Object messageNullValue =
        element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.MESSAGE_NULL_VALUE );
    if ( messageNullValue != null ) {
      messageFormatFilter.setNullString( String.valueOf( messageNullValue ) );
    } else if ( nullValue != null ) {
      messageFormatFilter.setNullString( String.valueOf( nullValue ) );
    } else {
      messageFormatFilter.setNullString( null );
    }

    final ResourceBundleFactory resourceBundleFactory = runtime.getResourceBundleFactory();
    final Locale newLocale = resourceBundleFactory.getLocale();
    if ( ObjectUtilities.equal( newLocale, messageFormatFilter.getLocale() ) == false ) {
      messageFormatFilter.setLocale( newLocale );
    }

    final TimeZone newTimeZone = resourceBundleFactory.getTimeZone();
    if ( ObjectUtilities.equal( newTimeZone, messageFormatFilter.getTimeZone() ) == false ) {
      messageFormatFilter.setTimeZone( newTimeZone );
    }

    final Object value = messageFormatFilter.performFormat( runtime.getDataRow() );
    if ( value == null ) {
      return rotate( element, nullValue, runtime );
    }
    return rotate( element, value, runtime );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final Object message = ElementTypeUtils.queryStaticValue( element );
    if ( message == null || String.valueOf( message ).length() == 0 ) {
      final String value = element.getElementType().getMetaData().getName();
      return rotate( element, value != null ? value : getId(), runtime );
    }
    return rotate( element, message, runtime );
  }

  public void configureDesignTimeDefaults( final ReportElement element, final Locale locale ) {
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "Message" );
  }
}
