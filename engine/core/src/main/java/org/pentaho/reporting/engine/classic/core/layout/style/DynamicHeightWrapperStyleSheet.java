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
 * An element, that has the dynamic flag set to true, must restrict its maximum width to the minimum width (unless it
 * explicitly defines the maximum-width). This strange behavior is a legacy of our old sloopy layouting definition,
 * where the minimum-size provided the defaults for all other sizes.
 *
 * @author Thomas Morgner
 */
public class DynamicHeightWrapperStyleSheet extends AbstractStyleSheet {
  private StyleSheet parent;

  public DynamicHeightWrapperStyleSheet( final StyleSheet parent ) {
    this.parent = parent;
  }

  public StyleSheet getParent() {
    return parent;
  }

  public InstanceID getId() {
    return parent.getId();
  }

  public long getChangeTracker() {
    return parent.getChangeTracker();
  }

  public Object getStyleProperty( final StyleKey key, final Object defaultValue ) {
    if ( ElementStyleKeys.MAX_WIDTH.equals( key ) ) {
      return parent.getStyleProperty( ElementStyleKeys.MIN_WIDTH, defaultValue );
    }
    return parent.getStyleProperty( key, defaultValue );
  }

  public Object[] toArray() {
    final Object[] objects = parent.toArray();
    objects[ElementStyleKeys.MAX_WIDTH.getIdentifier()] = objects[ElementStyleKeys.MIN_WIDTH.getIdentifier()];
    return objects;
  }
}
