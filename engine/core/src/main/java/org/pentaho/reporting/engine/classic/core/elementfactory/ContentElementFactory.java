/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
