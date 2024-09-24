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
public class NonDynamicHeightWrapperStyleSheet extends AbstractStyleSheet {
  private StyleSheet parent;

  public NonDynamicHeightWrapperStyleSheet( final StyleSheet parent ) {
    this.parent = parent;
  }

  public StyleSheet getParent() {
    return parent;
  }

  public Object getStyleProperty( final StyleKey key, final Object defaultValue ) {
    if ( ElementStyleKeys.MAX_WIDTH.equals( key ) ) {
      return parent.getStyleProperty( ElementStyleKeys.MIN_WIDTH, defaultValue );
    }
    if ( ElementStyleKeys.MAX_HEIGHT.equals( key ) ) {
      return parent.getStyleProperty( ElementStyleKeys.MIN_HEIGHT, defaultValue );
    }
    return parent.getStyleProperty( key, defaultValue );
  }

  public Object[] toArray() {
    final Object[] objects = parent.toArray();
    objects[ElementStyleKeys.MAX_WIDTH.getIdentifier()] = objects[ElementStyleKeys.MIN_WIDTH.getIdentifier()];
    objects[ElementStyleKeys.MAX_HEIGHT.getIdentifier()] = objects[ElementStyleKeys.MIN_HEIGHT.getIdentifier()];
    return objects;
  }

  public InstanceID getId() {
    return parent.getId();
  }

  public long getChangeTracker() {
    return parent.getChangeTracker();
  }
}
