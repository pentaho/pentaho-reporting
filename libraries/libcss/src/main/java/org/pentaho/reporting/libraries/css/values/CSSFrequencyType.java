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
 * Creation-Date: 23.11.2005, 11:45:22
 *
 * @author Thomas Morgner
 */
public class CSSFrequencyType extends CSSType {
  public static final CSSFrequencyType HERTZ = new CSSFrequencyType( "hz" );
  public static final CSSFrequencyType KILOHERTZ = new CSSFrequencyType( "khz" );

  private CSSFrequencyType( String name ) {
    super( name );
  }

  public boolean equals( Object obj ) {
    return ( obj instanceof CSSFrequencyType && super.equals( obj ) );
  }
}
