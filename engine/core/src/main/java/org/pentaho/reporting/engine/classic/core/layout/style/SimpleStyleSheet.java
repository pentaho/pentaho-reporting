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
import org.pentaho.reporting.engine.classic.core.style.BandDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * A simple, read-only stylesheet.
 *
 * @author Thomas Morgner
 */
public final class SimpleStyleSheet extends AbstractStyleSheet {
  public static final StyleSheet EMPTY_STYLE = new SimpleStyleSheet( BandDefaultStyleSheet.getBandDefaultStyle() );

  private Object[] properties;
  private InstanceID parentId;
  private long changeTracker;
  private long changeTrackerHash;
  private long modificationCount;
  private InstanceID instanceId;

  public SimpleStyleSheet( final StyleSheet parent ) {
    this( new InstanceID(), parent );
  }

  public SimpleStyleSheet( final InstanceID id, final StyleSheet parent ) {
    if ( parent == null ) {
      throw new NullPointerException();
    }
    this.instanceId = id;
    this.parentId = parent.getId();
    this.changeTracker = parent.getChangeTracker();
    this.changeTrackerHash = parent.getChangeTrackerHash();
    this.modificationCount = parent.getModificationCount();
    this.properties = parent.toArray();
  }

  /**
   * Returns the value of a style. If the style is not found in this style-sheet, the code looks in the parent
   * style-sheets. If the style is not found in any of the parent style-sheets, then the default value (possibly
   * <code>null</code>) is returned.
   *
   * @param key
   *          the style key.
   * @param defaultValue
   *          the default value (<code>null</code> permitted).
   * @return the value.
   */
  public Object getStyleProperty( final StyleKey key, final Object defaultValue ) {
    final int identifier = key.identifier;
    if ( properties.length > identifier ) {
      final Object property = properties[identifier];
      if ( property != null ) {
        return property;
      }
    }
    return defaultValue;
  }

  public Object[] toArray() {
    return properties.clone();
  }

  public long getChangeTracker() {
    return changeTracker;
  }

  public InstanceID getParentId() {
    return parentId;
  }

  public InstanceID getId() {
    return instanceId;
  }

  public long getModificationCount() {
    return modificationCount;
  }

  public long getChangeTrackerHash() {
    return changeTrackerHash;
  }
}
