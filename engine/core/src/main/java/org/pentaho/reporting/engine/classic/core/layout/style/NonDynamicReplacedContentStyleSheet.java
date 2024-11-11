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
 * A replaced content element that is contained in a 'canvas' box (which is the default for all non-inline replaced
 * content elements) must have a minimum width and height of 100% so that it fills the whole box.
 *
 * @author Thomas Morgner
 */
public class NonDynamicReplacedContentStyleSheet extends AbstractStyleSheet {
  private StyleSheet parent;

  public NonDynamicReplacedContentStyleSheet( final StyleSheet parent ) {
    this.parent = parent;
  }

  public StyleSheet getParent() {
    return parent;
  }

  public Object getStyleProperty( final StyleKey key, final Object defaultValue ) {
    if ( ElementStyleKeys.MAX_WIDTH.equals( key ) ) {
      return parent.getStyleProperty( ElementStyleKeys.WIDTH, parent.getStyleProperty( ElementStyleKeys.MIN_WIDTH,
          defaultValue ) );
    }
    if ( ElementStyleKeys.MAX_HEIGHT.equals( key ) ) {
      return parent.getStyleProperty( ElementStyleKeys.HEIGHT, parent.getStyleProperty( ElementStyleKeys.MIN_HEIGHT,
          defaultValue ) );
    }
    return parent.getStyleProperty( key, defaultValue );
  }

  public Object[] toArray() {
    final Object[] objects = parent.toArray();
    objects[ElementStyleKeys.MAX_WIDTH.getIdentifier()] = getStyleProperty( ElementStyleKeys.MAX_WIDTH );
    objects[ElementStyleKeys.MAX_HEIGHT.getIdentifier()] = getStyleProperty( ElementStyleKeys.MAX_HEIGHT );
    return objects;
  }

  public InstanceID getId() {
    return parent.getId();
  }

  public long getChangeTracker() {
    return parent.getChangeTracker();
  }
}
