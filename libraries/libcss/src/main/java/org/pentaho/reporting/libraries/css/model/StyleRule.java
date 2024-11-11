/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
