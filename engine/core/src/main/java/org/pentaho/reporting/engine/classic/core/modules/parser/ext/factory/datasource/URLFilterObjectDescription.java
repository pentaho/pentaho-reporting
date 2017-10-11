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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.filter.URLFilter;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.BeanObjectDescription;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;

import java.net.URL;

/**
 * An ObjectDescription for the URLFilterClass. This class uses either an given or an preconfigured base url to
 * construct the URL.
 *
 * @author Thomas Morgner
 */
public class URLFilterObjectDescription extends BeanObjectDescription {
  private static final Log logger = LogFactory.getLog( URLFilterObjectDescription.class );

  /**
   * Creates a new object description.
   *
   * @param className
   *          the class.
   */
  public URLFilterObjectDescription( final Class className ) {
    super( className );
    if ( URLFilter.class.isAssignableFrom( className ) == false ) {
      throw new IllegalArgumentException( "Given class is no instance of URLFilter." );
    }
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final URLFilter t = (URLFilter) super.createObject();
    if ( t.getBaseURL() == null ) {
      final String baseURL = getConfig().getConfigProperty( AbstractXmlResourceFactory.CONTENTBASE_KEY );
      try {
        final URL bURL = new URL( baseURL );
        t.setBaseURL( bURL );
      } catch ( Exception e ) {
        URLFilterObjectDescription.logger.warn( "BaseURL is invalid: ", e );
      }
    }
    return t;
  }
}
