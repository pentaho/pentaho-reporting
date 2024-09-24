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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Creation-Date: 05.11.2006, 14:31:22
 *
 * @author Thomas Morgner
 */
public class AbstractFunctionCategory implements FunctionCategory {
  private String bundleName;

  protected AbstractFunctionCategory( final String bundleName ) {
    this.bundleName = bundleName;
  }

  protected ResourceBundle getBundle( final Locale locale ) {
    return ResourceBundle.getBundle( bundleName, locale );
  }

  public String getDisplayName( final Locale locale ) {
    return getBundle( locale ).getString( "display-name" );
  }

  public String getDescription( final Locale locale ) {
    return getBundle( locale ).getString( "description" );
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    return !( o == null || getClass() != o.getClass() );
  }

  public int hashCode() {
    return getClass().hashCode();
  }
}
