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


package org.pentaho.reporting.engine.classic.core.layout.style;

import org.pentaho.reporting.engine.classic.core.style.AbstractStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * An element, that does not have the dynamic flag set to true, is limited to its minimum size and cannot grow any
 * larger than that. This stylesheet simply enforces this policy by redefining the maximum size so that the maximum size
 * is equal to the minimum size.
 *
 * @author Thomas Morgner
 */
public class NonPaddingWrapperStyleSheet extends AbstractStyleSheet {
  private StyleSheet parent;
  private static final Float ZERO = new Float( 0 );

  public NonPaddingWrapperStyleSheet() {
  }

  public void setParent( final StyleSheet parent ) {
    this.parent = parent;
  }

  public StyleSheet getParent() {
    return parent;
  }

  public Object getStyleProperty( final StyleKey key, final Object defaultValue ) {
    if ( ElementStyleKeys.PADDING_TOP.equals( key ) ) {
      return NonPaddingWrapperStyleSheet.ZERO;
    }
    if ( ElementStyleKeys.PADDING_LEFT.equals( key ) ) {
      return NonPaddingWrapperStyleSheet.ZERO;
    }
    if ( ElementStyleKeys.PADDING_BOTTOM.equals( key ) ) {
      return NonPaddingWrapperStyleSheet.ZERO;
    }
    if ( ElementStyleKeys.PADDING_RIGHT.equals( key ) ) {
      return NonPaddingWrapperStyleSheet.ZERO;
    }
    return parent.getStyleProperty( key, defaultValue );
  }

  public Object[] toArray() {
    final Object[] objects = parent.toArray();
    objects[ElementStyleKeys.PADDING_TOP.getIdentifier()] = NonPaddingWrapperStyleSheet.ZERO;
    objects[ElementStyleKeys.PADDING_LEFT.getIdentifier()] = NonPaddingWrapperStyleSheet.ZERO;
    objects[ElementStyleKeys.PADDING_BOTTOM.getIdentifier()] = NonPaddingWrapperStyleSheet.ZERO;
    objects[ElementStyleKeys.PADDING_RIGHT.getIdentifier()] = NonPaddingWrapperStyleSheet.ZERO;
    return objects;
  }

  public InstanceID getId() {
    return parent.getId();
  }

  public long getChangeTracker() {
    return parent.getChangeTracker();
  }
}
