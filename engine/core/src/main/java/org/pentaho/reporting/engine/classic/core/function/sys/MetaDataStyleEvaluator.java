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

package org.pentaho.reporting.engine.classic.core.function.sys;

import java.awt.Color;
import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.filter.types.ResourceLabelType;
import org.pentaho.reporting.engine.classic.core.function.AbstractElementFormatFunction;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.wizard.AutoGeneratorUtility;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;

public class MetaDataStyleEvaluator extends AbstractElementFormatFunction implements StructureFunction {
  private class VolatileDataAttributeContext implements DataAttributeContext {
    private VolatileDataAttributeContext() {
    }

    public Locale getLocale() {
      final ExpressionRuntime expressionRuntime = getRuntime();
      if ( expressionRuntime == null ) {
        throw new IllegalStateException();
      }
      return expressionRuntime.getResourceBundleFactory().getLocale();
    }

    public OutputProcessorMetaData getOutputProcessorMetaData() {
      final ExpressionRuntime expressionRuntime = getRuntime();
      if ( expressionRuntime == null ) {
        throw new IllegalStateException();
      }
      return expressionRuntime.getProcessingContext().getOutputProcessorMetaData();
    }
  }

  private transient VolatileDataAttributeContext attributeContext;
  private Boolean legacyMode;

  public MetaDataStyleEvaluator() {
  }

  private boolean isLegacyMode() {
    if ( legacyMode != null ) {
      return legacyMode.booleanValue();
    }

    if ( getRuntime() == null ) {
      return false;
    }

    // noinspection UnnecessaryBoxing
    legacyMode =
        Boolean.valueOf( "false".equals( getRuntime().getConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.WizardAllowAttributeChangeWithoutStyleChange" ) ) );
    return legacyMode.booleanValue();
  }

  public int getProcessingPriority() {
    return 4000;
  }

  public VolatileDataAttributeContext getAttributeContext() {
    if ( attributeContext == null ) {
      attributeContext = new VolatileDataAttributeContext();
    }
    return attributeContext;
  }

  /**
   * Evaluates all defined style-expressions of the given element.
   *
   * @param e
   *          the element that should be updated.
   * @return true, if the element can be styled.
   */
  protected boolean evaluateElement( final ReportElement e ) {
    final DataSchema dataSchema = getRuntime().getDataSchema();

    final Object allowStylingFlag =
        e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING );
    final Object allowAttributesFlag =
        e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES );
    if ( Boolean.TRUE.equals( allowStylingFlag ) == false && Boolean.TRUE.equals( allowAttributesFlag ) == false ) {
      // the element prohibits meta-data styling ..
      return false;
    }

    // a flag indicating whether we are dealing with a field or a label.
    String fieldName = (String) e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR );
    if ( fieldName == null ) {
      fieldName = (String) e.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD );
      if ( fieldName == null ) {
        return false;
      }
    }

    // the element always overrides all properties; properties not defined in the meta-data layer
    // will be removed from the stylesheet or attribute collection.

    final DataAttributes attributes = dataSchema.getAttributes( fieldName );
    if ( attributes == null ) {
      return false;
    }

    final MetaDataStyleEvaluator.VolatileDataAttributeContext context = getAttributeContext();
    final String typeName = e.getElementType().getMetaData().getName();

    if ( Boolean.TRUE.equals( allowStylingFlag ) ) {
      final Boolean bold =
          (Boolean) attributes.getMetaAttribute( MetaAttributeNames.Style.NAMESPACE, MetaAttributeNames.Style.BOLD,
              Boolean.class, context );
      final Boolean italic =
          (Boolean) attributes.getMetaAttribute( MetaAttributeNames.Style.NAMESPACE, MetaAttributeNames.Style.ITALIC,
              Boolean.class, context );
      final Boolean strikethrough =
          (Boolean) attributes.getMetaAttribute( MetaAttributeNames.Style.NAMESPACE,
              MetaAttributeNames.Style.STRIKETHROUGH, Boolean.class, context );
      final Boolean underline =
          (Boolean) attributes.getMetaAttribute( MetaAttributeNames.Style.NAMESPACE,
              MetaAttributeNames.Style.UNDERLINE, Boolean.class, context );
      final Integer fontSize =
          (Integer) attributes.getMetaAttribute( MetaAttributeNames.Style.NAMESPACE, MetaAttributeNames.Style.FONTSIZE,
              Integer.class, context );
      final String font =
          (String) attributes.getMetaAttribute( MetaAttributeNames.Style.NAMESPACE,
              MetaAttributeNames.Style.FONTFAMILY, String.class, context );
      final Color textColor =
          (Color) attributes.getMetaAttribute( MetaAttributeNames.Style.NAMESPACE, MetaAttributeNames.Style.COLOR,
              Color.class, context );
      final Color bgColor =
          (Color) attributes.getMetaAttribute( MetaAttributeNames.Style.NAMESPACE,
              MetaAttributeNames.Style.BACKGROUND_COLOR, Color.class, context );
      final ElementAlignment hAlign =
          (ElementAlignment) attributes.getMetaAttribute( MetaAttributeNames.Style.NAMESPACE,
              MetaAttributeNames.Style.HORIZONTAL_ALIGNMENT, ElementAlignment.class, context );
      final ElementAlignment vAlign =
          (ElementAlignment) attributes.getMetaAttribute( MetaAttributeNames.Style.NAMESPACE,
              MetaAttributeNames.Style.VERTICAL_ALIGNMENT, ElementAlignment.class, context );

      final ElementStyleSheet styleSheet = e.getStyle();
      if ( isMetaStylingEnabled( e.getAttributes().getAttribute( AttributeNames.Wizard.NAMESPACE,
          AttributeNames.Wizard.ENABLE_STYLE_VALIGNMENT ) ) ) {
        styleSheet.setStyleProperty( ElementStyleKeys.VALIGNMENT, vAlign );
      }
      if ( isMetaStylingEnabled( e.getAttributes().getAttribute( AttributeNames.Wizard.NAMESPACE,
          AttributeNames.Wizard.ENABLE_STYLE_ALIGNMENT ) ) ) {
        if ( hAlign != null ) {
          styleSheet.setStyleProperty( ElementStyleKeys.ALIGNMENT, hAlign );
        } else {
          if ( LabelType.INSTANCE.getMetaData().getName().equals( typeName ) == false
              && ResourceLabelType.INSTANCE.getMetaData().getName().equals( typeName ) == false ) {
            styleSheet.setStyleProperty( ElementStyleKeys.ALIGNMENT, computeAlignment( attributes, context ) );
          }
        }
      }
      if ( isMetaStylingEnabled( e.getAttributes().getAttribute( AttributeNames.Wizard.NAMESPACE,
          AttributeNames.Wizard.ENABLE_STYLE_BACKGROUND_COLOR ) ) ) {
        styleSheet.setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, bgColor );
      }
      if ( isMetaStylingEnabled( e.getAttributes().getAttribute( AttributeNames.Wizard.NAMESPACE,
          AttributeNames.Wizard.ENABLE_STYLE_COLOR ) ) ) {
        styleSheet.setStyleProperty( ElementStyleKeys.PAINT, textColor );
      }
      if ( isMetaStylingEnabled( e.getAttributes().getAttribute( AttributeNames.Wizard.NAMESPACE,
          AttributeNames.Wizard.ENABLE_STYLE_FONTFAMILY ) ) ) {
        styleSheet.setStyleProperty( TextStyleKeys.FONT, font );
      }
      if ( isMetaStylingEnabled( e.getAttributes().getAttribute( AttributeNames.Wizard.NAMESPACE,
          AttributeNames.Wizard.ENABLE_STYLE_FONTSIZE ) ) ) {
        styleSheet.setStyleProperty( TextStyleKeys.FONTSIZE, fontSize );
      }
      if ( isMetaStylingEnabled( e.getAttributes().getAttribute( AttributeNames.Wizard.NAMESPACE,
          AttributeNames.Wizard.ENABLE_STYLE_UNDERLINE ) ) ) {
        styleSheet.setStyleProperty( TextStyleKeys.UNDERLINED, underline );
      }
      if ( isMetaStylingEnabled( e.getAttributes().getAttribute( AttributeNames.Wizard.NAMESPACE,
          AttributeNames.Wizard.ENABLE_STYLE_BOLD ) ) ) {
        styleSheet.setStyleProperty( TextStyleKeys.BOLD, bold );
      }
      if ( isMetaStylingEnabled( e.getAttributes().getAttribute( AttributeNames.Wizard.NAMESPACE,
          AttributeNames.Wizard.ENABLE_STYLE_STRIKETHROUGH ) ) ) {
        styleSheet.setStyleProperty( TextStyleKeys.STRIKETHROUGH, strikethrough );
      }
      if ( isMetaStylingEnabled( e.getAttributes().getAttribute( AttributeNames.Wizard.NAMESPACE,
          AttributeNames.Wizard.ENABLE_STYLE_ITALICS ) ) ) {
        styleSheet.setStyleProperty( TextStyleKeys.ITALIC, italic );
      }
    }

    final boolean legacyMode = isLegacyMode();
    if ( Boolean.TRUE.equals( allowAttributesFlag )
        && ( ( legacyMode == false ) || ( Boolean.TRUE.equals( allowStylingFlag ) ) ) ) {
      if ( LabelType.INSTANCE.getMetaData().getName().equals( typeName ) == false
          && ResourceLabelType.INSTANCE.getMetaData().getName().equals( typeName ) == false ) {
        final String format =
            (String) attributes.getMetaAttribute( MetaAttributeNames.Formatting.NAMESPACE,
                MetaAttributeNames.Formatting.FORMAT, String.class, context );
        if ( format != null ) {
          e.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING, format );
        } else {
          final String autoFormat = AutoGeneratorUtility.computeFormatString( attributes, context );
          e.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING, autoFormat );
        }
      } else {
        // only change the static text of labels. We do not touch anything else.
        if ( "label".equals( typeName ) ) { // NON-NLS
          final String format =
              (String) attributes.getMetaAttribute( MetaAttributeNames.Formatting.NAMESPACE,
                  MetaAttributeNames.Formatting.LABEL, String.class, context );
          e.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, format );
        }
      }
    }
    return true;
  }

  private boolean isMetaStylingEnabled( final Object b ) {
    if ( b == null ) {
      return true;
    }
    return Boolean.TRUE.equals( b );
  }

  private static ElementAlignment
    computeAlignment( final DataAttributes attributes, final DataAttributeContext context ) {
    final Class type =
        (Class) attributes.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.TYPE,
            Class.class, context );

    if ( Number.class.isAssignableFrom( type ) ) {
      return ElementAlignment.RIGHT;
    }
    return ElementAlignment.LEFT;
  }

  /**
   * Clones the expression. The expression should be reinitialized after the cloning.
   * <P>
   * Expressions maintain no state, cloning is done at the beginning of the report processing to disconnect the
   * expression from any other object space.
   *
   * @return a clone of this expression.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public Object clone() throws CloneNotSupportedException {
    final MetaDataStyleEvaluator o = (MetaDataStyleEvaluator) super.clone();
    o.attributeContext = null;
    return o;
  }

  public MetaDataStyleEvaluator getInstance() {
    final MetaDataStyleEvaluator eval = (MetaDataStyleEvaluator) super.getInstance();
    eval.attributeContext = null;
    return eval;
  }
}
