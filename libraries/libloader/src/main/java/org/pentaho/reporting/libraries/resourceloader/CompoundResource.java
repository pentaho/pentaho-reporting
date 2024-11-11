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
 * Creation-Date: 08.04.2006, 14:08:13
 *
 * @author Thomas Morgner
 */
public class CompoundResource implements Resource {
  private static final long serialVersionUID = -5828242419391352185L;

  private ResourceKey source;
  private DependencyCollector dependencies;
  private Object product;
  private Class targetType;

  public CompoundResource( final ResourceKey source,
                           final DependencyCollector dependencies,
                           final Object product,
                           final Class targetType ) {
    if ( source == null ) {
      throw new NullPointerException( "Source must not be null" );
    }
    if ( dependencies == null ) {
      throw new NullPointerException( "Dependecies must be given." );
    }
    if ( targetType == null ) {
      throw new NullPointerException( "TargetType must not be null" );
    }
    if ( product == null ) {
      throw new NullPointerException( "Product must not be null" );
    }
    this.targetType = targetType;
    this.source = source;
    try {
      this.dependencies = (DependencyCollector) dependencies.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException
        ( "Clone not supported? This should not happen." );
    }
    this.product = product;
  }

  public Object getResource() throws ResourceException {
    return product;
  }

  public long getVersion( final ResourceKey key ) {
    return dependencies.getVersion( key );
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
    return dependencies.getDependencies();
  }

  public ResourceKey getSource() {
    return source;
  }

  public Class getTargetType() {
    return targetType;
  }

  public boolean isTemporaryResult() {
    return false;
  }
}
