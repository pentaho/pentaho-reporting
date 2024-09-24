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

package org.pentaho.reporting.engine.classic.core.wizard;

import java.awt.Component;
import java.awt.Image;
import java.awt.Shape;
import java.io.File;
import java.net.URL;
import java.sql.Blob;
import java.sql.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentFieldType;
import org.pentaho.reporting.engine.classic.core.filter.types.DateFieldType;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.filter.types.NumberFieldType;
import org.pentaho.reporting.engine.classic.core.filter.types.TextFieldType;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.libraries.formatting.FastMessageFormat;

public class AutoGeneratorUtility {
  private AutoGeneratorUtility() {
  }

  public static Group[] getGroups( final ReportDefinition definition ) {
    final int groupCount = definition.getGroupCount();
    final Group[] groups = new Group[groupCount];
    for ( int i = 0; i < groupCount; i++ ) {
      final Group group = definition.getGroup( i );
      groups[i] = group;
    }
    return groups;
  }

  public static String generateUniqueExpressionName( final DataSchema dataSchema, final String pattern,
      final String[] extraColumns ) throws ReportProcessingException {
    final FastMessageFormat fastMessageFormat = new FastMessageFormat( pattern );
    if ( fastMessageFormat.getSubFormatCount() == 0 ) {
      throw new IllegalArgumentException();
    }

    final HashSet<String> names = new HashSet<String>( Arrays.asList( dataSchema.getNames() ) );
    for ( int i = 0; i < extraColumns.length; i++ ) {
      names.add( extraColumns[i] );
    }

    final Object[] data = new Object[1];
    int i = 0;
    // call me at any time if you have more than 32000 functions of the same name-pattern in a single report.
    while ( i < Short.MAX_VALUE ) {
      data[0] = IntegerCache.getInteger( i );
      final String s = fastMessageFormat.format( data );
      if ( names.contains( s ) == false ) {
        return s;
      }
      i += 1;
    }
    throw new ReportProcessingException( "Unable to create a unique name for the given pattern" );
  }

  public static String generateUniqueExpressionName( final DataSchema dataSchema, final String pattern,
      final AbstractReportDefinition extraColumns ) throws ReportProcessingException {
    final FastMessageFormat fastMessageFormat = new FastMessageFormat( pattern );
    if ( fastMessageFormat.getSubFormatCount() == 0 ) {
      throw new IllegalArgumentException();
    }

    final HashSet<String> names = new HashSet<String>( Arrays.asList( dataSchema.getNames() ) );
    final Expression[] expressions = extraColumns.getExpressions().getExpressions();
    for ( int i = 0; i < expressions.length; i++ ) {
      final Expression expression = expressions[i];
      names.add( expression.getName() );
    }

    final Object[] data = new Object[1];
    int i = 0;
    // call me at any time if you have more than 32000 functions of the same name-pattern in a single report.
    while ( i < Short.MAX_VALUE ) {
      data[0] = IntegerCache.getInteger( i );
      final String s = fastMessageFormat.format( data );
      if ( names.contains( s ) == false ) {
        return s;
      }
      i += 1;
    }
    throw new ReportProcessingException( "Unable to create a unique name for the given pattern" );
  }

  /**
   * Computes a set of field widths. The input-width definitions can be a mix of absolute and relative values; the
   * resulting widths are always relative values. If the input width is null or zero, it is assumed that the field wants
   * to have a generic width.
   *
   * @param fieldDescriptions
   * @param pageWidth
   * @return
   */
  public static float[] computeFieldWidths( final Float[] fieldDescriptions, final float pageWidth ) {
    final float[] resultWidths = new float[fieldDescriptions.length];

    float definedWidth = 0;
    int definedNumberOfFields = 0;
    for ( int i = 0; i < fieldDescriptions.length; i++ ) {
      final Number number = fieldDescriptions[i];
      if ( number != null && number.floatValue() != 0 ) {
        if ( number.floatValue() < 0 ) {
          // a fixed value ..
          resultWidths[i] = number.floatValue();
          definedNumberOfFields += 1;
          definedWidth += number.floatValue();
        } else {
          final float absValue = number.floatValue();
          final float relativeValue = -absValue * 100 / pageWidth;
          resultWidths[i] = relativeValue;
          definedNumberOfFields += 1;
          definedWidth += relativeValue;
        }
      }
    }

    if ( definedNumberOfFields == fieldDescriptions.length ) {
      // we are done, all fields are defined.
      return resultWidths;
    }

    if ( definedNumberOfFields == 0 ) {
      // the worst case, no element provides a weight ..
      // therefore all fields have the same proportional width.
      Arrays.fill( resultWidths, -( 100 / fieldDescriptions.length ) );
      return resultWidths;
    }

    final float availableSpace = -100 - definedWidth;
    if ( availableSpace > 0 ) {
      // all predefined fields already fill the complete page. There is no space left for the
      // extra columns.
      return resultWidths;
    }

    final float avgSpace = availableSpace / ( fieldDescriptions.length - definedNumberOfFields );
    for ( int i = 0; i < resultWidths.length; i++ ) {
      final float width = resultWidths[i];
      if ( width == 0 ) {
        resultWidths[i] = avgSpace;
      }
    }
    return resultWidths;
  }

  public static Element generateFooterElement( final Class aggregationType, final ElementType targetType,
      final String group, final String fieldName ) {
    if ( aggregationType == null ) {
      final Element footerValueElement = new Element();
      footerValueElement.setElementType( new LabelType() );
      footerValueElement.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "" );
      footerValueElement.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING,
          Boolean.TRUE );
      footerValueElement.setAttribute( AttributeNames.Wizard.NAMESPACE,
          AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES, Boolean.FALSE );
      return footerValueElement;
    }

    final Element element = generateDetailsElement( fieldName, targetType );
    element.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.AGGREGATION_TYPE, aggregationType );
    element.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.AGGREGATION_GROUP, group );
    return element;
  }

  public static Element generateHeaderElement( final String fieldName ) {
    final Element headerElement = new Element();
    headerElement.getStyle().setStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, Boolean.TRUE );
    headerElement.setElementType( new LabelType() );
    headerElement.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR, fieldName );
    headerElement.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING,
        Boolean.TRUE );
    headerElement.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES,
        Boolean.TRUE );
    return headerElement;
  }

  public static Element generateDetailsElement( final String fieldName, final ElementType targetType ) {
    final Element detailsElement = new Element();
    detailsElement.getStyle().setStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, Boolean.TRUE );
    detailsElement.setElementType( targetType );
    detailsElement.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, fieldName );
    detailsElement.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING,
        Boolean.TRUE );
    detailsElement.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES,
        Boolean.TRUE );
    return detailsElement;
  }

  public static Number createFieldWidth( final DataAttributes attributes, final DataAttributeContext context ) {
    return (Number) attributes.getMetaAttribute( MetaAttributeNames.Formatting.NAMESPACE,
        MetaAttributeNames.Formatting.DISPLAY_SIZE, Number.class, context );
  }

  public static String createFieldName( final DataAttributes attributes, final DataAttributeContext context ) {
    return (String) attributes.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.NAME,
        String.class, context );
  }

  public static ElementType createFieldType( final DataAttributes attributes, final DataAttributeContext context ) {
    if ( attributes == null ) {
      return new TextFieldType();
    }

    final Class type =
        (Class) attributes.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.TYPE,
            Class.class, context );
    return createFieldType( type );
  }

  public static ElementType createFieldType( final Class type ) {
    final ElementType elementType;
    if ( Number.class.isAssignableFrom( type ) ) {
      elementType = new NumberFieldType();
    } else if ( Date.class.isAssignableFrom( type ) ) {
      elementType = new DateFieldType();
    } else if ( byte[].class.isAssignableFrom( type ) || Blob.class.isAssignableFrom( type )
        || File.class.isAssignableFrom( type ) || URL.class.isAssignableFrom( type )
        || Image.class.isAssignableFrom( type ) || Shape.class.isAssignableFrom( type )
        || Component.class.isAssignableFrom( type ) || ImageContainer.class.isAssignableFrom( type ) ) {
      elementType = new ContentFieldType();
    } else {
      elementType = new TextFieldType();
    }
    return elementType;
  }

  public static String computeFormatString( final DataAttributes attributes, final DataAttributeContext context ) {
    if ( attributes == null ) {
      throw new NullPointerException();
    }
    if ( context == null ) {
      throw new NullPointerException();
    }

    final Class type =
        (Class) attributes.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.TYPE,
            Class.class, context );
    final Boolean currency =
        (Boolean) attributes.getMetaAttribute( MetaAttributeNames.Numeric.NAMESPACE,
            MetaAttributeNames.Numeric.CURRENCY, Boolean.class, context );
    final Number scale =
        (Number) attributes.getMetaAttribute( MetaAttributeNames.Numeric.NAMESPACE, MetaAttributeNames.Numeric.SCALE,
            Number.class, context );
    final Number precision =
        (Number) attributes.getMetaAttribute( MetaAttributeNames.Numeric.NAMESPACE,
            MetaAttributeNames.Numeric.PRECISION, Number.class, context );

    if ( java.sql.Date.class.isAssignableFrom( type ) ) {
      // this includes timestamp ..
      final DateFormat dateFormat = DateFormat.getDateInstance( DateFormat.DEFAULT, context.getLocale() );
      if ( dateFormat instanceof SimpleDateFormat ) {
        final SimpleDateFormat sdf = (SimpleDateFormat) dateFormat;
        return sdf.toPattern();
      }
      // we cannot come up with a sensible default ..
      return null;
    } else if ( Time.class.isAssignableFrom( type ) ) {
      // this includes timestamp ..
      final DateFormat dateFormat = DateFormat.getTimeInstance( DateFormat.DEFAULT, context.getLocale() );
      if ( dateFormat instanceof SimpleDateFormat ) {
        final SimpleDateFormat sdf = (SimpleDateFormat) dateFormat;
        return sdf.toPattern();
      }
      // we cannot come up with a sensible default ..
      return null;
    } else if ( Date.class.isAssignableFrom( type ) ) {
      // this includes timestamp ..
      final DateFormat dateFormat =
          DateFormat.getDateTimeInstance( DateFormat.DEFAULT, DateFormat.DEFAULT, context.getLocale() );
      if ( dateFormat instanceof SimpleDateFormat ) {
        final SimpleDateFormat sdf = (SimpleDateFormat) dateFormat;
        return sdf.toPattern();
      }
      // we cannot come up with a sensible default ..
      return null;
    } else if ( Number.class.isAssignableFrom( type ) ) {
      if ( Boolean.TRUE.equals( currency ) ) {
        final NumberFormat format = NumberFormat.getCurrencyInstance( context.getLocale() );
        if ( format instanceof DecimalFormat ) {
          final DecimalFormat decimalFormat = (DecimalFormat) format;
          return decimalFormat.toPattern();
        }
      }

      final DecimalFormat format = new DecimalFormat();
      if ( scale != null && precision != null ) {
        format.setMaximumFractionDigits( scale.intValue() );
        format.setMinimumFractionDigits( scale.intValue() );
        format.setMaximumIntegerDigits( precision.intValue() - scale.intValue() );
        format.setMinimumIntegerDigits( 1 );
      }
      return format.toPattern();
    }
    return null;
  }

  public static boolean isIgnorable( final DataAttributes attributes, final DataAttributeContext context ) {
    if ( attributes == null ) {
      throw new NullPointerException();
    }
    if ( context == null ) {
      throw new NullPointerException();
    }

    final String source =
        (String) attributes.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.SOURCE,
            String.class, context );
    if ( MetaAttributeNames.Core.SOURCE_VALUE_ENVIRONMENT.equals( source ) ) {
      return true;
    }
    if ( MetaAttributeNames.Core.SOURCE_VALUE_EXPRESSION.equals( source ) ) {
      final Class expressionType =
          (Class) attributes.getMetaAttribute( MetaAttributeNames.Expressions.NAMESPACE,
              MetaAttributeNames.Expressions.CLASS, Class.class, context );
      if ( expressionType == null ) {
        return false;
      }

      if ( ExpressionRegistry.getInstance().isExpressionRegistered( expressionType.getClass().getName() ) ) {
        final ExpressionMetaData data =
            ExpressionRegistry.getInstance().getExpressionMetaData( expressionType.getName() );
        if ( data.isElementLayoutProcessor() || data.isGlobalLayoutProcessor() ) {
          // ignore the expression ..
          return true;
        }
      }
      return false;
    }
    if ( MetaAttributeNames.Core.SOURCE_VALUE_PARAMETER.equals( source ) ) {
      final Boolean include =
          (Boolean) attributes.getMetaAttribute( MetaAttributeNames.Parameters.NAMESPACE,
              MetaAttributeNames.Parameters.INCLUDE_IN_WIZARD, Boolean.class, context );
      if ( Boolean.TRUE.equals( include ) ) {
        return false;
      }
      return true;
    }

    final Object indexColumn =
        attributes.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.INDEXED_COLUMN,
            Boolean.class, context );
    if ( Boolean.TRUE.equals( indexColumn ) ) {
      return true;
    }
    return false;
  }

  public static Band findGeneratedContent( final Band band ) {
    final Band generatedContentInternal = findGeneratedContentInternal( band );
    if ( generatedContentInternal != null ) {
      generatedContentInternal.clear();
      return generatedContentInternal;
    }

    if ( band.getElementCount() == 0 ) {
      return band;
    }

    return null;
  }

  private static Band findGeneratedContentInternal( final Band band ) {
    if ( Boolean.TRUE.equals( band.getAttribute( AttributeNames.Wizard.NAMESPACE,
        AttributeNames.Wizard.GENERATED_CONTENT_MARKER ) ) ) {
      return band;
    }
    final Element[] elements = band.getElementArray();
    for ( int i = 0; i < elements.length; i++ ) {
      final Element element = elements[i];
      if ( element instanceof Band ) {
        final Band retval = findGeneratedContentInternal( (Band) element );
        if ( retval != null ) {
          return retval;
        }
      }
    }
    return null;
  }

}
