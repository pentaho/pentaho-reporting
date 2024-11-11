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


package org.pentaho.reporting.engine.classic.core.style;

import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public interface StyleSheet {
  public boolean getBooleanStyleProperty( final StyleKey key );

  public boolean getBooleanStyleProperty( final StyleKey key, final boolean defaultValue );

  public int getIntStyleProperty( final StyleKey key, final int def );

  public double getDoubleStyleProperty( final StyleKey key, final double def );

  public Object getStyleProperty( final StyleKey key );

  public Object getStyleProperty( final StyleKey key, final Object defaultValue );

  public Object[] toArray();

  public InstanceID getId();

  public long getChangeTracker();

  public long getChangeTrackerHash();

  public long getModificationCount();

  boolean isLocalKey( StyleKey key );
}
