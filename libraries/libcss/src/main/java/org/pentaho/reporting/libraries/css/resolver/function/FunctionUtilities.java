/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.resolver.function;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.resolver.FunctionEvaluationException;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSResourceValue;
import org.pentaho.reporting.libraries.css.values.CSSStringType;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Creation-Date: 04.07.2006, 14:30:10
 *
 * @author Thomas Morgner
 */
public class FunctionUtilities {
  private static CSSNumericType[] KNOWN_TYPES = {
    CSSNumericType.PERCENTAGE,
    CSSNumericType.EM,
    CSSNumericType.EX,
    CSSNumericType.CM,
    CSSNumericType.MM,
    CSSNumericType.INCH,
    CSSNumericType.PT,
    CSSNumericType.PC,
    CSSNumericType.DEG,
    CSSNumericType.PX
  };

  private FunctionUtilities() {
  }


  public static CSSResourceValue loadResource( final DocumentContext process,
                                               final Object value )
    throws FunctionEvaluationException {
    final Class[] supportedResourceTypes = process.getSupportedResourceTypes();
    if ( supportedResourceTypes.length == 0 ) {
      throw new FunctionEvaluationException
        ( "Failed to create URI: Resource loading failed as the output " +
          "target does not support any resource types." );
    }
    return loadResource( process, value, supportedResourceTypes );
  }

  public static CSSResourceValue loadResource( final DocumentContext process,
                                               final Object value,
                                               final Class[] type )
    throws FunctionEvaluationException {
    // ok, this is going to be expensive. Kids, you dont wanna try this at home ...
    final ResourceManager manager = process.getResourceManager();
    final ResourceKey baseKey = process.getContextKey();
    try {
      final ResourceKey key;
      if ( value instanceof ResourceKey ) {
        key = (ResourceKey) value;
      } else if ( baseKey == null ) {
        key = manager.createKey( value );
      } else if ( value instanceof String ) {
        key = manager.deriveKey( baseKey, (String) value );
      } else {
        throw new FunctionEvaluationException
          ( "Failed to create URI: Resource loading failed: Key not derivable" );
      }

      final Resource res = manager.create( key, baseKey, type );
      return new CSSResourceValue( res );
    } catch ( Exception e ) {
      throw new FunctionEvaluationException
        ( "Failed to create URI: Resource loading failed: " + e.getMessage(), e );
    }
  }


  public static CSSValue parseValue( final DocumentContext process,
                                     final String text ) {
    final CSSNumericValue val = convertToNumber( text );
    if ( val != null ) {
      return val;
    }

    // next step: That may be expensive, but we search for URLs ..
    try {
      return loadResource( process, text );
    } catch ( FunctionEvaluationException e ) {
      // ignore, it was just an attempt ...
    }

    return new CSSStringValue( CSSStringType.STRING, text );
  }

  public static CSSNumericValue parseNumberValue( final String text, final String type )
    throws FunctionEvaluationException {
    final CSSNumericValue val = convertToNumber( text, getUnitType( type ) );
    if ( val != null ) {
      return val;
    }
    throw new FunctionEvaluationException( "Unable to convert to number." );
  }

  public static CSSNumericValue parseNumberValue( final String text )
    throws FunctionEvaluationException {
    final CSSNumericValue val = convertToNumber( text );
    if ( val != null ) {
      return val;
    }
    throw new FunctionEvaluationException( "Unable to convert to number." );
  }

  private static CSSNumericValue convertToNumber( final String stringValue ) {
    final String txt = stringValue.trim();
    CSSNumericType type = null;

    for ( int i = 0; i < KNOWN_TYPES.length; i++ ) {
      final CSSNumericType numericType = KNOWN_TYPES[ i ];
      if ( txt.endsWith( numericType.getType() ) ) {
        type = numericType;
      }
    }
    if ( type == null ) {
      type = CSSNumericType.NUMBER;
    }
    final String number = txt.substring
      ( 0, txt.length() - type.getType().length() ).trim();
    return convertToNumber( number, type );
  }

  private static CSSNumericValue convertToNumber( final String stringValue,
                                                  CSSNumericType type ) {
    if ( type == null ) {
      type = CSSNumericType.NUMBER;
    }
    try {
      final String number = stringValue.trim();
      final double nVal = Double.parseDouble( number );
      return CSSNumericValue.createValue( type, nVal );
    } catch ( Exception e ) {
      return null;
    }
  }

  public static CSSNumericType getUnitType( final String typeText ) {
    if ( typeText == null ) {
      return CSSNumericType.NUMBER;
    }
    final String txt = typeText.trim();
    for ( int i = 0; i < KNOWN_TYPES.length; i++ ) {
      final CSSNumericType numericType = KNOWN_TYPES[ i ];
      if ( txt.equalsIgnoreCase( numericType.getType() ) ) {
        return numericType;
      }
    }
    return CSSNumericType.NUMBER;
  }


  public static String resolveString( final DocumentContext layoutProcess,
                                      final LayoutElement layoutElement,
                                      final CSSValue value )
    throws FunctionEvaluationException {
    final CSSValue notAFunctionAnymore = resolveParameter( layoutProcess, layoutElement, value );
    if ( notAFunctionAnymore instanceof CSSStringValue ) {
      final CSSStringValue strVal = (CSSStringValue) notAFunctionAnymore;
      return strVal.getValue();
    }

    // Falling back to the Value itself ..

    final String retval = notAFunctionAnymore.getCSSText();
    if ( retval == null ) {
      throw new FunctionEvaluationException
        ( "Value " + notAFunctionAnymore + " is invalid" );
    }
    return retval;
  }

  public static CSSValue resolveParameter( final DocumentContext layoutProcess,
                                           final LayoutElement layoutElement,
                                           final CSSValue value )
    throws FunctionEvaluationException {
    if ( value instanceof CSSFunctionValue == false ) {
      return value;
    }

    final CSSFunctionValue functionValue = (CSSFunctionValue) value;

    final StyleValueFunction function =
      FunctionFactory.getInstance().getStyleFunction
        ( functionValue.getFunctionName() );
    if ( function == null ) {
      throw new FunctionEvaluationException
        ( "Unsupported Function: " + functionValue );
    }
    return function.evaluate
      ( layoutProcess, layoutElement, functionValue );
  }

  public static ResourceKey createURI( final String uri, final DocumentContext layoutProcess ) {
    try {
      final ResourceKey base = layoutProcess.getContextKey();
      final ResourceManager resourceManager =
        layoutProcess.getResourceManager();

      if ( base != null ) {
        try {
          return resourceManager.deriveKey( base, uri );
        } catch ( ResourceKeyCreationException ex ) {
          // ignore ..
        }
      }
      return resourceManager.createKey( uri );
    } catch ( Exception e ) {
      return null;
    }
  }
}
