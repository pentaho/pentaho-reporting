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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.filter.templates.ImageURLElementTemplate;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;

import java.net.URL;

/**
 * An image URL element template description.
 *
 * @author Thomas Morgner
 */
public class ImageURLElementTemplateDescription extends AbstractTemplateDescription {
  private static final Log logger = LogFactory.getLog( ImageURLElementTemplateDescription.class );

  /**
   * Creates a new template description.
   *
   * @param name
   *          the name.
   */
  public ImageURLElementTemplateDescription( final String name ) {
    super( name, ImageURLElementTemplate.class, true );
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final ImageURLElementTemplate t = (ImageURLElementTemplate) super.createObject();
    if ( isBaseURLNeeded( t.getContent() ) ) {
      if ( t.getBaseURL() == null ) {
        final String baseURL = getConfig().getConfigProperty( AbstractXmlResourceFactory.CONTENTBASE_KEY );
        if ( baseURL == null ) {
          ImageURLElementTemplateDescription.logger
              .warn( "The image-URL will not be resolvable, as no BaseURL is defined." );
        } else {
          try {
            final URL bURL = new URL( baseURL );
            t.setBaseURL( bURL );
          } catch ( Exception e ) {
            ImageURLElementTemplateDescription.logger.warn( "BaseURL is invalid: " + baseURL, e );
          }
        }
      }
    }
    return t;
  }

  private boolean isBaseURLNeeded( final String content ) {
    try {
      new URL( content );
      return false;
    } catch ( Exception e ) {
      return true;
    }
  }
}
