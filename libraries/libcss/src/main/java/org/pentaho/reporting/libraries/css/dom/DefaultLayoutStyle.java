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


package org.pentaho.reporting.libraries.css.dom;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Unlike the old JFreeReport stylesheet, this implementation has no inheritance at all. It needs to be resolved
 * manually, which is no bad thing, as we have to do this anyway during the computation.
 *
 * @author Thomas Morgner
 */
public final class DefaultLayoutStyle implements LayoutStyle {
  private CSSValue[] values;
  private Object reference;

  public DefaultLayoutStyle() {
  }

  public Object getReference() {
    return reference;
  }

  public void setReference( final Object reference ) {
    this.reference = reference;
  }

  public CSSValue getValue( final StyleKey key ) {
    if ( values == null ) {
      return null;
    }
    return values[ key.getIndex() ];
  }

  public void setValue( final StyleKey key, final CSSValue value ) {
    if ( values == null ) {
      values = new CSSValue[ StyleKeyRegistry.getRegistry().getKeyCount() ];
    }
    values[ key.getIndex() ] = value;
  }

  // todo: Make sure we call dispose once the layout style goes out of context
  public void dispose() {
  }

  public DefaultLayoutStyle createCopy() {
    final DefaultLayoutStyle style = new DefaultLayoutStyle();
    if ( values == null ) {
      style.values = null;
      return style;
    }

    style.values = (CSSValue[]) values.clone();
    return style;
  }

  public boolean isEmpty() {
    if ( values == null ) {
      return true;
    }
    for ( int i = 0; i < values.length; i++ ) {
      if ( values[ i ] != null ) {
        return false;
      }
    }
    return true;
  }

  /**
   * Attempts to copy the supplied style information into this style object and returns a success code indicating if the
   * copy was performed.
   *
   * @param style the style information to be copied into this style holder
   * @return <code>true</code> if the copy was performed, <code>false</code> otherwise
   */
  public boolean copyFrom( final LayoutStyle style ) {
    // If the supplied style infomration isn't from DefaultStyle, we can't copy it
    if ( style instanceof DefaultLayoutStyle == false ) {
      return false;
    }

    // If there is no style information to copy, don't copy it (but say we did)
    final DefaultLayoutStyle rawstyle = (DefaultLayoutStyle) style;
    if ( rawstyle.values == null ) {
      return true;
    }

    // If we don't have a style holder currently, create one from the
    // supplied style and consider the copy to be done
    if ( values == null ) {
      values = (CSSValue[]) rawstyle.values.clone();
      return true;
    }

    // We have a holder and we were given the right type of style info ... copy the new over the old
    // NOTE: don't copy empty (null) information over
    final int length = rawstyle.values.length;
    for ( int i = 0; i < length; i++ ) {
      final CSSValue o = rawstyle.values[ i ];
      if ( o != null ) {
        values[ i ] = o;
      }
    }
    return true;
  }

}
