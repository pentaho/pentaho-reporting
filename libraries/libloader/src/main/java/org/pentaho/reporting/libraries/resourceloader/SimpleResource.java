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


package org.pentaho.reporting.libraries.resourceloader;

/**
 * A simple resource with only a single dependency.
 *
 * @author Thomas Morgner
 */
public class SimpleResource implements Resource {
  private Object value;
  private ResourceKey key;
  private long version;
  private static final long serialVersionUID = -6007941678785921339L;
  private Class targetType;

  public SimpleResource( final ResourceKey key,
                         final Object value,
                         final Class targetType,
                         final long version ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( value == null ) {
      throw new NullPointerException();
    }
    if ( targetType == null ) {
      throw new NullPointerException();
    }

    this.targetType = targetType;
    this.value = value;
    this.key = key;
    this.version = version;
  }

  public Object getResource() {
    return value;
  }

  public long getVersion( final ResourceKey key ) {
    if ( key.equals( this.key ) ) {
      return version;
    }
    // -1 is the placeholder for: not known.
    return -1;
  }

  /**
   * The primary source is also included in this set. The dependencies are given as ResourceKey objects. The keys itself
   * do not hold any state information.
   * <p/>
   * The dependencies do not track deep dependencies. So if Resource A depends on Resource B which depends on Resource
   * C, then A only knows about B, not C.
   *
   * @return
   */
  public ResourceKey[] getDependencies() {
    return new ResourceKey[] { getSource() };
  }

  public ResourceKey getSource() {
    return key;
  }

  public Class getTargetType() {
    return targetType;
  }

  public boolean isTemporaryResult() {
    return false;
  }
}
