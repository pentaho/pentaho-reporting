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
