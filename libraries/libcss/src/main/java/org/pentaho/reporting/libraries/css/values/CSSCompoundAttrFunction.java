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

package org.pentaho.reporting.libraries.css.values;

/**
 * Creation-Date: 05.12.2005, 20:41:01
 *
 * @author Thomas Morgner
 */
public class CSSCompoundAttrFunction extends CSSFunctionValue {
  private String namespace;
  private String name;
  private String type;
  private String key;

  public CSSCompoundAttrFunction( final String key,
                                  final String namespace,
                                  final String name,
                                  final String type ) {
    super( "-x-pentaho-css-attr", CSSCompoundAttrFunction.produceParameters
      ( key, namespace, name, type ) );
    this.namespace = namespace;
    this.name = name;
    this.type = type;
    this.key = key;
  }

  public CSSCompoundAttrFunction( final String key,
                                  final String namespace,
                                  final String name ) {
    this( key, namespace, name, null );
  }

  private static CSSValue[] produceParameters( final String key,
                                               final String namespace,
                                               final String name,
                                               final String type ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    CSSConstant nameConst;
    if ( namespace == null ) {
      nameConst = new CSSConstant( "" );
    } else {
      nameConst = new CSSConstant( namespace );
    }
    if ( type == null ) {
      return new CSSValue[] { new CSSRawValue( key ), nameConst,
        new CSSConstant( name ) };
    } else {
      return new CSSValue[] { new CSSRawValue( key ), nameConst,
        new CSSConstant( name ), new CSSConstant( type ) };
    }
  }

  public String getName() {
    return name;
  }

  public String getValueType() {
    return type;
  }

  public String getNamespace() {
    return namespace;
  }

  public String getKey() {
    return key;
  }

  public String getCSSText() {
    if ( type != null ) {
      if ( namespace == null ) {
        return "-x-pentaho-css-attr(|" + name + ", " + type + ')';
      } else {
        return "-x-pentaho-css-attr(" + namespace + '|' + name + ", " + type + ')';
      }
    } else {
      if ( namespace == null ) {
        return "attr(|" + name + ')';
      } else {
        return "attr(" + namespace + '|' + name + ')';
      }
    }
  }
}
