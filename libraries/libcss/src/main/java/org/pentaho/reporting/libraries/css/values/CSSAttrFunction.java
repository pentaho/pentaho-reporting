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

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: 05.12.2005, 20:41:01
 *
 * @author Thomas Morgner
 */
public class CSSAttrFunction extends CSSFunctionValue {
  private String namespace;
  private String name;
  private String type;

  public CSSAttrFunction( final String namespace,
                          final String name,
                          final String type ) {
    super( "attr", produceParameters( namespace, name, type ) );
    this.namespace = namespace;
    this.name = name;
    this.type = type;
  }

  public CSSAttrFunction( final String namespace,
                          final String name ) {
    this( namespace, name, null );
  }

  private static CSSValue[] produceParameters( final String namespace,
                                               final String name,
                                               final String type ) {
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
      return new CSSValue[] { nameConst, new CSSConstant( name ) };
    } else {
      return new CSSValue[] { nameConst, new CSSConstant( name ), new CSSConstant(
        type ) };
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

  public String getCSSText() {
    if ( type != null ) {
      if ( namespace == null ) {
        return "attr(|" + name + ", " + type + ")";
      } else {
        return "attr(" + namespace + "|" + name + ", " + type + ")";
      }
    } else {
      if ( namespace == null ) {
        return "attr(|" + name + ")";
      } else {
        return "attr(" + namespace + "|" + name + ")";
      }
    }
  }

  /**
   * Determines if this instance of the object is equals to another Object
   *
   * @return <code>true</code> if the supplied object is equivalent to this object, <code>false</code> otherwise
   */
  public boolean equals( Object obj ) {
    if ( obj instanceof CSSAttrFunction && super.equals( obj ) ) {
      CSSAttrFunction that = (CSSAttrFunction) obj;
      return ( ObjectUtilities.equal( this.name, that.name ) && ObjectUtilities.equal( this.namespace, that.namespace )
        && ObjectUtilities.equal( this.type, that.type ) );
    } else {
      return false;
    }
  }
}
