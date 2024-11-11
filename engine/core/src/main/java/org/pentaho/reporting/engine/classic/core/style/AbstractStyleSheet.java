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


package org.pentaho.reporting.engine.classic.core.style;

import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public abstract class AbstractStyleSheet implements StyleSheet, Cloneable {
  /**
   * The instance id of this ElementStyleSheet. This id is shared among all clones.
   */
  private InstanceID id;

  protected AbstractStyleSheet() {
    this.id = new InstanceID();
  }

  /**
   * Returns the value of a style. If the style is not found in this style-sheet, the code looks in the parent
   * style-sheets. If the style is not found in any of the parent style-sheets, then <code>null</code> is returned.
   *
   * @param key
   *          the style key.
   * @return the value.
   */
  public Object getStyleProperty( final StyleKey key ) {
    return getStyleProperty( key, null );
  }

  /**
   * Returns a boolean style (defaults to false if the style is not found).
   *
   * @param key
   *          the style key.
   * @return <code>true</code> or <code>false</code>.
   */
  public boolean getBooleanStyleProperty( final StyleKey key ) {
    final Boolean b = (Boolean) getStyleProperty( key, null );
    if ( b == null ) {
      return false;
    }
    return b.booleanValue();
  }

  /**
   * Returns a boolean style.
   *
   * @param key
   *          the style key.
   * @param defaultValue
   *          the default value.
   * @return true or false.
   */
  public boolean getBooleanStyleProperty( final StyleKey key, final boolean defaultValue ) {
    final Boolean b = (Boolean) getStyleProperty( key, null );
    if ( b == null ) {
      return defaultValue;
    }
    return b.booleanValue();
  }

  /**
   * Returns an integer style.
   *
   * @param key
   *          the style key.
   * @param def
   *          the default value.
   * @return the style value.
   */
  public int getIntStyleProperty( final StyleKey key, final int def ) {
    final Number i = (Number) getStyleProperty( key, null );
    if ( i == null ) {
      return def;
    }
    return i.intValue();
  }

  /**
   * Returns an double style.
   *
   * @param key
   *          the style key.
   * @param def
   *          the default value.
   * @return the style value.
   */
  public double getDoubleStyleProperty( final StyleKey key, final double def ) {
    final Number i = (Number) getStyleProperty( key, null );
    if ( i == null ) {
      return def;
    }
    return i.doubleValue();
  }

  /**
   * Returns the ID of the stylesheet. The ID does identify an element stylesheet an all all cloned instances of that
   * stylesheet.
   *
   * @return the ID of this stylesheet.
   */
  public InstanceID getId() {
    return id;
  }

  public boolean isLocalKey( final StyleKey key ) {
    return false;
  }

  public StyleSheet derive( final boolean preserveId ) {
    final AbstractStyleSheet s = (AbstractStyleSheet) clone();
    if ( preserveId == false ) {
      s.id = new InstanceID();
    }
    return s;
  }

  public StyleSheet clone() {
    try {
      return (StyleSheet) super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  protected void setId( final InstanceID id ) {
    if ( id == null ) {
      throw new NullPointerException();
    }
    this.id = id;
  }

  public long getModificationCount() {
    return 0;
  }

  public long getChangeTrackerHash() {
    return 0;
  }
}
