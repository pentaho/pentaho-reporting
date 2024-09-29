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
 * Creation-Date: 30.09.2007, 17:19:49
 *
 * @author Thomas Morgner
 */
public class AnchorStyleSheet extends AbstractStyleSheet {
  private StyleSheet parent;
  private String anchor;

  public AnchorStyleSheet( final String anchor, final StyleSheet parent ) {
    if ( parent == null ) {
      throw new NullPointerException( "Parent must not be null" );
    }
    this.parent = parent;
    this.anchor = anchor;
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
    if ( ElementStyleKeys.ANCHOR_NAME.equals( key ) ) {
      return anchor;
    }
    return parent.getStyleProperty( key, defaultValue );
  }

  public Object[] toArray() {
    final Object[] objects = parent.toArray();
    objects[ElementStyleKeys.ANCHOR_NAME.getIdentifier()] = anchor;
    return objects;
  }

}
