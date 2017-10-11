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
