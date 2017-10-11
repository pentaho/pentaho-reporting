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

package org.pentaho.reporting.libraries.css.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.css.LibCssBoot;
import org.pentaho.reporting.libraries.css.model.CSSDeclarationRule;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;
import org.pentaho.reporting.libraries.css.values.CSSAttrFunction;
import org.pentaho.reporting.libraries.css.values.CSSCompoundAttrFunction;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSInheritValue;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSStringType;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Creation-Date: 25.11.2005, 17:43:38
 *
 * @author Thomas Morgner
 */
public class CSSValueFactory {
  private static final CSSValue[] EMPTY_PARAMETERS = new CSSValue[ 0 ];
  private static final Log logger = LogFactory.getLog( CSSValueFactory.class );

  public static final String SIMPLE_PREFIX = "org.pentaho.reporting.libraries.css.parser.handlers.";
  public static final String COMPOUND_PREFIX = "org.pentaho.reporting.libraries.css.parser.compoundhandlers.";

  private HashMap handlers;
  private HashMap compoundHandlers;
  private StyleKeyRegistry registry;

  public CSSValueFactory( StyleKeyRegistry registry ) {
    if ( registry == null ) {
      throw new NullPointerException();
    }
    this.registry = registry;
    this.handlers = new HashMap();
    this.compoundHandlers = new HashMap();
    this.registerDefaults();
  }


  public void registerDefaults() {
    final Configuration config = LibCssBoot.getInstance().getGlobalConfig();
    Iterator sit = config.findPropertyKeys( SIMPLE_PREFIX );
    while ( sit.hasNext() ) {
      final String key = (String) sit.next();
      final String name = key.substring( SIMPLE_PREFIX.length() ).toLowerCase();
      final String c = config.getConfigProperty( key );
      Object module =
        ObjectUtilities.loadAndInstantiate( c, CSSValueFactory.class, CSSValueReadHandler.class );
      if ( module instanceof CSSValueReadHandler ) {
        handlers.put( name, module );
      } else {
        logger.warn( "Invalid module implementation: [" + c + "] for style key [" + name + ']' );
      }
    }

    Iterator cit = config.findPropertyKeys( COMPOUND_PREFIX );
    while ( cit.hasNext() ) {
      final String key = (String) cit.next();
      final String name = key.substring( COMPOUND_PREFIX.length() ).toLowerCase();
      final String c = config.getConfigProperty( key );
      Object module =
        ObjectUtilities.loadAndInstantiate( c, CSSValueFactory.class, CSSCompoundValueReadHandler.class );
      if ( module instanceof CSSCompoundValueReadHandler ) {
        compoundHandlers.put( name, module );
      }
    }
  }


  private CSSValue createValue( StyleKey key, LexicalUnit value ) {
    final CSSValueReadHandler module =
      (CSSValueReadHandler) handlers.get( key.getName() );
    if ( module == null ) {
      //  || module instanceof CSSCompoundValueReadHandler
      // Compund handler are more important than simple handlers ..
      return null;
    }

    return module.createValue( key, value );
  }

  public static CSSAttrFunction parseAttrFunction( LexicalUnit unit ) {
    if ( unit.getLexicalUnitType() != LexicalUnit.SAC_ATTR ) {
      return null;
    }

    final String attrName = unit.getStringValue().trim();
    final String[] name = StyleSheetParserUtil.parseNamespaceIdent( attrName );
    return new CSSAttrFunction( name[ 0 ], name[ 1 ] );
  }

  public static boolean isFunctionValue( LexicalUnit unit ) {
    final short lexicalUnitType = unit.getLexicalUnitType();
    return ( lexicalUnitType == LexicalUnit.SAC_FUNCTION ||
      lexicalUnitType == LexicalUnit.SAC_COUNTER_FUNCTION ||
      lexicalUnitType == LexicalUnit.SAC_COUNTERS_FUNCTION ||
      lexicalUnitType == LexicalUnit.SAC_RGBCOLOR ||
      lexicalUnitType == LexicalUnit.SAC_RECT_FUNCTION );
  }

  private static CSSAttrFunction parseComplexAttrFn( LexicalUnit parameters ) {
    if ( parameters == null ) {
      return null;
    }

    final String attrName = parameters.getStringValue().trim();
    final String[] name = StyleSheetParserUtil.parseNamespaceIdent( attrName );

    final LexicalUnit afterComma = parseComma( parameters );
    if ( afterComma == null ) {
      return new CSSAttrFunction( name[ 0 ], name[ 1 ] );
    }

    final String attrType = parseAttributeType( afterComma );
    if ( attrType == null ) {
      return new CSSAttrFunction( name[ 0 ], name[ 1 ] );
    } else {
      return new CSSAttrFunction( name[ 0 ], name[ 1 ], attrType );
    }
  }

  public static CSSFunctionValue parseFunction( LexicalUnit unit ) {
    if ( isFunctionValue( unit ) == false ) {
      return null;
    }
    LexicalUnit parameters = unit.getParameters();
    String functionName = unit.getFunctionName();
    if ( parameters == null ) {
      // no-parameter function include the date() function...
      return new CSSFunctionValue( functionName, EMPTY_PARAMETERS );
    }
    if ( "attr".equalsIgnoreCase( functionName ) ) {
      return parseComplexAttrFn( unit.getParameters() );
    }
    if ( "color".equalsIgnoreCase( functionName ) ) {
      // for some strange reason, flute translates "rgb" functions into "color" functions which
      // are not even mentioned in the CSS specs. We therefore translate it back into RGB.
      functionName = "rgb";
    }

    final ArrayList contentList = new ArrayList();
    while ( parameters != null ) {
      if ( parameters.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
        contentList.add( new CSSConstant( parameters.getStringValue() ) );
      } else if ( parameters.getLexicalUnitType() == LexicalUnit.SAC_STRING_VALUE ) {
        contentList.add( new CSSStringValue( CSSStringType.STRING,
          parameters.getStringValue() ) );
      } else if ( CSSValueFactory.isNumericValue( parameters ) ) {
        final CSSNumericValue numericValue =
          CSSValueFactory.createNumericValue( parameters );
        if ( numericValue == null ) {
          return null;
        }
        contentList.add( numericValue );
      } else if ( CSSValueFactory.isLengthValue( parameters ) ) {
        final CSSNumericValue lengthValue =
          CSSValueFactory.createLengthValue( parameters );
        if ( lengthValue == null ) {
          return null;
        }
        contentList.add( lengthValue );
      } else if ( parameters.getLexicalUnitType() == LexicalUnit.SAC_ATTR ) {
        final CSSAttrFunction attrFn =
          CSSValueFactory.parseAttrFunction( parameters );
        if ( attrFn == null ) {
          return null;
        }
        contentList.add( attrFn );
      } else if ( parameters.getLexicalUnitType() == LexicalUnit.SAC_URI ) {
        final CSSStringValue uriValue = CSSValueFactory.createUriValue(
          parameters );
        if ( uriValue == null ) {
          return null;
        }
        contentList.add( uriValue );
      } else if ( isFunctionValue( parameters ) ) {
        final CSSFunctionValue functionValue = parseFunction( parameters );
        if ( functionValue == null ) {
          return null;
        }
        contentList.add( functionValue );
      } else {
        // parse error: Something we do not understand ...
        return null;
      }
      parameters = CSSValueFactory.parseComma( parameters );
    }
    final CSSValue[] paramVals = (CSSValue[])
      contentList.toArray( new CSSValue[ contentList.size() ] );

    return new CSSFunctionValue( functionName, paramVals );
  }


  private static String parseAttributeType( LexicalUnit unit ) {
    if ( unit == null ) {
      return null;
    }
    if ( unit.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      return unit.getStringValue();
    }
    return null;
  }

  private void setCompundInheritValue( String name,
                                       CSSDeclarationRule rule,
                                       boolean important ) {
    CSSCompoundValueReadHandler handler =
      (CSSCompoundValueReadHandler) compoundHandlers.get( name );
    if ( handler == null ) {
      logger.warn( "Got no key for inherited value: " + name );
      return;
    }

    StyleKey[] keys = handler.getAffectedKeys();
    for ( int i = 0; i < keys.length; i++ ) {
      StyleKey key = keys[ i ];
      rule.setPropertyValue( key, CSSInheritValue.getInstance(), important );
    }
  }

  private void setCompundAttrValue( String name,
                                    CSSAttrFunction attr,
                                    CSSDeclarationRule rule,
                                    boolean important ) {


    final CSSCompoundValueReadHandler handler =
      (CSSCompoundValueReadHandler) compoundHandlers.get( name );
    if ( handler == null ) {
      logger.warn( "Got no key for compound attr function: " + name );
      return;
    }

    StyleKey[] keys = handler.getAffectedKeys();
    for ( int i = 0; i < keys.length; i++ ) {
      StyleKey key = keys[ i ];
      final CSSCompoundAttrFunction cattr = new CSSCompoundAttrFunction
        ( name, attr.getNamespace(), attr.getName(), attr.getValueType() );
      rule.setPropertyValue( key, cattr, important );
    }
  }

  public void parseValue( CSSDeclarationRule rule,
                          String name,
                          LexicalUnit value,
                          boolean important )
    throws CSSParserFactoryException {
    if ( rule == null ) {
      throw new NullPointerException( "Rule given is null." );
    }

    final String normalizedName = name.toLowerCase();
    final StyleKey key = registry.findKeyByName( normalizedName );
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_INHERIT ) {
      if ( key == null ) {
        setCompundInheritValue( normalizedName, rule, important );
        return;
      }
      rule.setPropertyValue( key, CSSInheritValue.getInstance(), important );
      return;
    }

    if ( value.getLexicalUnitType() == LexicalUnit.SAC_ATTR ) {
      final CSSAttrFunction attrFn = parseAttrFunction( value );
      // ATTR function.
      if ( attrFn != null ) {
        if ( key == null ) {
          // Log.warn("Got no key for attribute-function " + normalizedName);
          setCompundAttrValue( normalizedName, attrFn, rule, important );
          return;
        }
        rule.setPropertyValue( key, attrFn, important );
      }
      return;
    } else if ( isFunctionValue( value ) && "attr".equals( value.getFunctionName() ) ) {
      // ATTR function (extended version).
      if ( key == null ) {
        logger.warn( "Got no key for attribute-function " + normalizedName );
        return;
      }
      final CSSAttrFunction attrFn = parseComplexAttrFn( value.getParameters() );
      if ( attrFn != null ) {
        rule.setPropertyValue( key, attrFn, important );
      }
      return;
    }

    if ( key != null ) {
      CSSValue cssValue = createValue( key, value );
      if ( cssValue != null ) {
        rule.setPropertyValue( key, cssValue, important );
        //Log.debug ("Got value " + key.getName() + " = " + cssValue + "(" + cssValue.getClass() + ") - (important =
        // " + important + ")");
        return;
      }
    }

    final CSSCompoundValueReadHandler module =
      (CSSCompoundValueReadHandler) compoundHandlers.get( normalizedName );
    if ( module == null ) {
      if ( key == null ) {
        logger.info( "Unknown style-key: Neither compound handlers nor single-value handers are registered for "
          + normalizedName );
        return;
      }

      logger.warn( "Unparsable value: Got no valid result for " + normalizedName + " (" + value + ')' );
      return; // ignore this rule ..
    }

    Map map = module.createValues( value );
    if ( map == null ) {
      return;
    }
    Iterator iterator = map.entrySet().iterator();
    while ( iterator.hasNext() ) {
      Map.Entry entry = (Map.Entry) iterator.next();
      StyleKey entryKey = (StyleKey) entry.getKey();
      CSSValue mapCssValue = (CSSValue) entry.getValue();

      rule.setPropertyValue( entryKey, mapCssValue, important );
      //Log.debug ("Got value " + entryKey.getName() + " = " + mapCssValue + "(" + mapCssValue.getClass() + ") -
      // (important = " + important + ")");
    }
  }

  public static CSSStringValue createUriValue( LexicalUnit value ) {
    if ( value.getLexicalUnitType() != LexicalUnit.SAC_URI ) {
      return null;
    }

    final String uri = value.getStringValue();
    return new CSSStringValue( CSSStringType.URI, uri );
  }

  public static boolean isNumericValue( LexicalUnit value ) {
    final short lexicalUnitType = value.getLexicalUnitType();
    if ( lexicalUnitType == LexicalUnit.SAC_INTEGER ) {
      return true;
    } else if ( lexicalUnitType == LexicalUnit.SAC_REAL ) {
      return true;
    }
    return false;
  }


  public static CSSNumericValue createNumericValue( LexicalUnit value ) {
    final short lexicalUnitType = value.getLexicalUnitType();
    if ( lexicalUnitType == LexicalUnit.SAC_INTEGER ) {
      return CSSNumericValue.createValue( CSSNumericType.NUMBER, value.getIntegerValue() );
    }
    if ( lexicalUnitType == LexicalUnit.SAC_REAL ) {
      return CSSNumericValue.createValue( CSSNumericType.NUMBER, value.getFloatValue() );
    }
    return null;
  }

  public static boolean isLengthValue( LexicalUnit value ) {
    final short lexicalUnitType = value.getLexicalUnitType();
    return lexicalUnitType >= LexicalUnit.SAC_EM && lexicalUnitType <= LexicalUnit.SAC_PICA;
    //    if (lexicalUnitType == LexicalUnit.SAC_EM)
    //    {
    //      return true;
    //    }
    //    else if (lexicalUnitType == LexicalUnit.SAC_EX)
    //    {
    //      return true;
    //    }
    //    else if (lexicalUnitType == LexicalUnit.SAC_PIXEL)
    //    {
    //      return true;
    //    }
    //    else if (lexicalUnitType == LexicalUnit.SAC_INCH)
    //    {
    //      return true;
    //    }
    //    else if (lexicalUnitType == LexicalUnit.SAC_CENTIMETER)
    //    {
    //      return true;
    //    }
    //    else if (lexicalUnitType == LexicalUnit.SAC_MILLIMETER)
    //    {
    //      return true;
    //    }
    //    else if (lexicalUnitType == LexicalUnit.SAC_PICA)
    //    {
    //      return true;
    //    }
    //    else if (lexicalUnitType == LexicalUnit.SAC_POINT)
    //    {
    //      return true;
    //    }
    //    return false;
  }


  public static CSSNumericValue createLengthValue( LexicalUnit value ) {
    final short lexicalUnitType = value.getLexicalUnitType();
    if ( lexicalUnitType == LexicalUnit.SAC_INTEGER ) {
      if ( value.getFloatValue() != 0 ) {
        return null;
      }
      return CSSNumericValue.createValue( CSSNumericType.PT, 0 );
    }
    if ( lexicalUnitType == LexicalUnit.SAC_EM ) {
      return CSSNumericValue.createValue( CSSNumericType.EM, value.getFloatValue() );
    } else if ( lexicalUnitType == LexicalUnit.SAC_EX ) {
      return CSSNumericValue.createValue( CSSNumericType.EX,
        value.getFloatValue() );
    } else if ( lexicalUnitType == LexicalUnit.SAC_PIXEL ) {
      return CSSNumericValue.createValue( CSSNumericType.PX,
        value.getFloatValue() );
    } else if ( lexicalUnitType == LexicalUnit.SAC_INCH ) {
      return CSSNumericValue.createValue( CSSNumericType.INCH,
        value.getFloatValue() );
    } else if ( lexicalUnitType == LexicalUnit.SAC_CENTIMETER ) {
      return CSSNumericValue.createValue( CSSNumericType.CM,
        value.getFloatValue() );
    } else if ( lexicalUnitType == LexicalUnit.SAC_MILLIMETER ) {
      return CSSNumericValue.createValue( CSSNumericType.MM,
        value.getFloatValue() );
    } else if ( lexicalUnitType == LexicalUnit.SAC_PICA ) {
      return CSSNumericValue.createValue( CSSNumericType.PC,
        value.getFloatValue() );
    } else if ( lexicalUnitType == LexicalUnit.SAC_POINT ) {
      return CSSNumericValue.createValue( CSSNumericType.PT,
        value.getFloatValue() );
    }
    return null;
  }

  public static LexicalUnit parseComma( final LexicalUnit value ) {
    if ( value == null ) {
      return null;
    }

    LexicalUnit maybeComma = value.getNextLexicalUnit();
    if ( maybeComma == null ) {
      return null;
    }
    if ( maybeComma.getLexicalUnitType() == LexicalUnit.SAC_OPERATOR_COMMA ) {
      return maybeComma.getNextLexicalUnit();
    }
    return null;
  }

}
