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
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;

import java.util.Locale;

public class LabelType extends AbstractElementType implements RotatableText {
  public static final ElementType INSTANCE = new LabelType();

  public LabelType() {
    super( "label" );
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( runtime == null ) {
      throw new NullPointerException( "Runtime must never be null." );
    }
    if ( element == null ) {
      throw new NullPointerException( "Element must never be null." );
    }

    final Object retval = ElementTypeUtils.queryStaticValue( element );
    if ( retval == null ) {
      return rotate( element, element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE ), runtime );
    }
    return rotate( element, retval, runtime );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( Boolean.TRUE.equals( element.getAttribute( AttributeNames.Wizard.NAMESPACE,
        AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES ) ) ) {
      final Object labelFor = element.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR );
      if ( labelFor instanceof String ) {
        final String labelForText = (String) labelFor;
        final DataAttributes attributes = runtime.getDataSchema().getAttributes( labelForText );
        if ( attributes != null ) {
          final DefaultDataAttributeContext context =
              new DefaultDataAttributeContext( runtime.getProcessingContext().getOutputProcessorMetaData(), runtime
                  .getResourceBundleFactory().getLocale() );
          final Object o =
              attributes.getMetaAttribute( MetaAttributeNames.Formatting.NAMESPACE,
                  MetaAttributeNames.Formatting.LABEL, String.class, context );
          if ( o != null ) {
            return rotate( element, o, runtime );
          }
        }
      }
    }

    final Object retval = ElementTypeUtils.queryStaticValue( element );
    if ( retval == null ) {
      return rotate( element, "Label", runtime );
    }
    return rotate( element,  retval, runtime );
  }

  public void configureDesignTimeDefaults( final ReportElement element, final Locale locale ) {
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "Label" );
  }
}
