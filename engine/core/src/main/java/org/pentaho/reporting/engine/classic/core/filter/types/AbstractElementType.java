/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.filter.types;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;

import java.util.Locale;

public abstract class AbstractElementType implements ElementType {
  private String id;
  private ElementMetaData metaData;

  protected AbstractElementType( final String id ) {
    if ( id == null ) {
      throw new NullPointerException();
    }
    this.id = id;
  }

  public ElementMetaData getMetaData() {
    if ( metaData == null ) {
      metaData = ElementTypeRegistry.getInstance().getElementType( id );
    }
    return metaData;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final AbstractElementType that = (AbstractElementType) o;

    if ( !id.equals( that.id ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return id.hashCode();
  }

  public void configureDesignTimeDefaults( final ReportElement element, final Locale locale ) {

  }

  public ReportElement create() {
    final Element element = new Element();
    element.setElementType( this );
    return element;
  }

  public ElementType clone() {
    try {
      return (ElementType) super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  protected String getId() {
    return this.id;
  }
}
