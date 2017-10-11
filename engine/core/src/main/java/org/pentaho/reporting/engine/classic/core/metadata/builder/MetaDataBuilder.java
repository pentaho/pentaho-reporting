/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
