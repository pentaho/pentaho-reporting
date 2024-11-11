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


package org.pentaho.reporting.engine.classic.core.metadata.builder;

import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;

public abstract class MetaDataBuilder<T extends MetaDataBuilder<T>> implements Cloneable {
  private String name;
  private String bundleLocation;
  private String keyPrefix;
  private boolean expert;
  private boolean preferred;
  private boolean hidden;
  private boolean deprecated;
  private MaturityLevel maturityLevel;
  private int compatibilityLevel;

  protected abstract T self();

  public T name( final String name ) {
    this.name = name;
    return self();
  }

  public T bundle( final String bundleLocation, final String keyPrefix ) {
    this.bundleLocation = bundleLocation;
    this.keyPrefix = keyPrefix;
    return self();
  }

  public T maturity( final MaturityLevel m ) {
    this.maturityLevel = m;
    return self();
  }

  public T since( final int comp ) {
    this.compatibilityLevel = comp;
    return self();
  }

  public T expert() {
    this.expert = true;
    return self();
  }

  public T preferred() {
    this.preferred = true;
    return self();
  }

  public T hidden() {
    this.hidden = true;
    return self();
  }

  public T deprecated() {
    this.deprecated = true;
    return self();
  }

  public T expert( final boolean v ) {
    this.expert = v;
    return self();
  }

  public T preferred( final boolean v ) {
    this.preferred = v;
    return self();
  }

  public T hidden( final boolean v ) {
    this.hidden = v;
    return self();
  }

  public T deprecated( final boolean v ) {
    this.deprecated = v;
    return self();
  }

  public String getName() {
    return name;
  }

  public String getBundleLocation() {
    return bundleLocation;
  }

  public String getKeyPrefix() {
    return keyPrefix;
  }

  public boolean isExpert() {
    return expert;
  }

  public boolean isPreferred() {
    return preferred;
  }

  public boolean isHidden() {
    return hidden;
  }

  public boolean isDeprecated() {
    return deprecated;
  }

  public MaturityLevel getMaturityLevel() {
    return maturityLevel;
  }

  public int getCompatibilityLevel() {
    return compatibilityLevel;
  }

  public T clone() {
    try {
      return (T) super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException( e );
    }
  }
}
