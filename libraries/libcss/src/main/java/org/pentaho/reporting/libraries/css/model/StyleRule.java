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

package org.pentaho.reporting.libraries.css.model;

import java.io.Serializable;

/**
 * Creation-Date: 23.11.2005, 10:50:15
 *
 * @author Thomas Morgner
 */
public abstract class StyleRule implements Serializable, Cloneable {
  private StyleSheet parentStyle;
  private StyleRule parentRule;
  private boolean readOnly;
  private StyleKeyRegistry styleKeyRegistry;

  protected StyleRule( final StyleKeyRegistry styleKeyRegistry ) {
    if ( styleKeyRegistry == null ) {
      throw new NullPointerException();
    }
    this.styleKeyRegistry = styleKeyRegistry;
  }

  protected StyleRule( final StyleSheet parentStyle,
                       final StyleRule parentRule ) {
    if ( parentStyle == null ) {
      throw new NullPointerException();
    }
    this.styleKeyRegistry = parentStyle.getStyleKeyRegistry();
    this.parentStyle = parentStyle;
    this.parentRule = parentRule;
  }

  public StyleKeyRegistry getStyleKeyRegistry() {
    return styleKeyRegistry;
  }

  public StyleSheet getParentStyle() {
    return parentStyle;
  }

  public StyleRule getParentRule() {
    return parentRule;
  }

  protected void setParentStyle( final StyleSheet parentStyle ) {
    if ( parentStyle == null ) {
      throw new NullPointerException();
    }
    this.parentStyle = parentStyle;
  }

  protected void setParentRule( final StyleRule parentRule ) {
    this.parentRule = parentRule;
  }

  public Object clone() throws CloneNotSupportedException {
    // parent rule and parent style are not cloned.
    return super.clone();
  }

  public final void makeReadOnly() {
    readOnly = true;
  }

  public final boolean isReadOnly() {
    return readOnly;
  }
}
