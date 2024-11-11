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


package org.pentaho.reporting.engine.classic.core.elementfactory;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentType;

import java.net.URL;

/**
 * The drawable field element factory can be used to create elements that display <code>Drawable</code> elements.
 * <p/>
 * A drawable field expects the named datasource to contain Drawable objects.
 * <p/>
 * Once the desired properties are set, the factory can be reused to create similiar elements.
 *
 * @author Thomas Morgner
 */
public class ContentElementFactory extends AbstractContentElementFactory {
  /**
   * The static value.
   */
  private Object content;

  /**
   * The base URL is used to resolve relative URLs.
   */
  private URL baseURL;

  /**
   * DefaultConstructor.
   */
  public ContentElementFactory() {
  }

  /**
   * Returns the base url. The BaseURL is used to resolve relative URLs found in the datasource.
   *
   * @return the base url.
   */
  public URL getBaseURL() {
    return baseURL;
  }

  /**
   * Defines a BaseURL for the new element. The BaseURL is used to resolve relative URLs found in the datasource.
   *
   * @param baseURL
   *          the base URL.
   */
  public void setBaseURL( final URL baseURL ) {
    this.baseURL = baseURL;
  }

  public Object getContent() {
    return content;
  }

  public void setContent( final Object content ) {
    this.content = content;
  }

  /**
   * Creates a new drawable field element based on the defined properties.
   *
   * @return the generated elements
   * @throws IllegalStateException
   *           if the field name is not set.
   * @see ElementFactory#createElement()
   */
  public Element createElement() {
    final Element element = new Element();
    applyElementName( element );
    applyStyle( element.getStyle() );

    element.setElementType( new ContentType() );
    if ( content != null ) {
      element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, content );
    }
    if ( baseURL != null ) {
      element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.CONTENT_BASE, baseURL );
    }
    return element;
  }
}
