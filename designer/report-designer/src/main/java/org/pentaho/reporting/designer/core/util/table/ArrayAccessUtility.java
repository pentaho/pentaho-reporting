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

package org.pentaho.reporting.designer.core.util.table;

import org.pentaho.reporting.designer.core.util.FastPropertyEditorManager;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.MetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorRegistry;

import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.util.Locale;

public class ArrayAccessUtility {
  public static boolean isArray( final Object array ) {
    if ( array == null ) {
      return false;
    }
    if ( array.getClass().isArray() == false ) {
      return false;
    }
    return true;
  }

  public static Object[] normalizeArray( final Object array ) {
    if ( array == null ) {
      throw new IllegalArgumentException();
    }
    if ( array.getClass().isArray() == false ) {
      throw new IllegalArgumentException();
    }

    final Object[] retval = new Object[ Array.getLength( array ) ];
    for ( int i = 0; i < retval.length; i++ ) {
      retval[ i ] = Array.get( array, i );
    }
    return retval;
  }

  public static Object normalizeNative( final Object[] data, final Class arrayType ) {
    final Object array = Array.newInstance( arrayType, data.length );
    for ( int i = 0; i < data.length; i++ ) {
      final Object o = data[ i ];
      if ( o != null && arrayType.isInstance( o ) == false ) {
        throw new ClassCastException( "Object " + o + " cannot be cast to " + arrayType );
      }
      Array.set( array, i, o );
    }
    return array;
  }

  public static String getArrayAsString( final Object array ) {
    if ( array == null ) {
      throw new IllegalArgumentException();
    }
    if ( array.getClass().isArray() == false ) {
      throw new IllegalArgumentException();
    }
    final StringBuilder b = new StringBuilder();
    b.append( '[' );
    final int length = Array.getLength( array );
    for ( int i = 0; i < length; i++ ) {
      if ( i > 0 ) {
        b.append( ", " );
      }

      final Object value = Array.get( array, i );
      if ( value == null ) {
        b.append( "<null>" ); // NON-NLS
      } else if ( value instanceof String ) {
        b.append( value );
      } else if ( value instanceof Expression ) {
        final MetaData metaData =
          ExpressionRegistry.getInstance().getExpressionMetaData( value.getClass().getName() );
        if ( metaData != null ) {
          b.append( metaData.getDisplayName( Locale.getDefault() ) );
        } else {
          b.append( String.valueOf( value ) );
        }
      } else if ( value instanceof ReportElement ) {
        final Element element = (Element) value;
        final MetaData metaData = element.getElementType().getMetaData();
        if ( metaData != null ) {
          b.append( metaData.getDisplayName( Locale.getDefault() ) );
        } else {
          b.append( String.valueOf( value ) );
        }
      } else if ( value instanceof ReportPreProcessor ) {
        final ReportPreProcessorMetaData metaData =
          ReportPreProcessorRegistry.getInstance().getReportPreProcessorMetaData( value.getClass().getName() );
        if ( metaData != null ) {
          b.append( metaData.getDisplayName( Locale.getDefault() ) );
        } else {
          b.append( String.valueOf( value ) );
        }
      } else {
        final PropertyEditor propertyEditor = FastPropertyEditorManager.findEditor( value.getClass() );
        if ( propertyEditor != null ) {
          propertyEditor.setValue( value );
          b.append( propertyEditor.getAsText() );
        } else {
          b.append( value );
        }
      }
    }

    b.append( ']' );
    return b.toString();
  }
}
