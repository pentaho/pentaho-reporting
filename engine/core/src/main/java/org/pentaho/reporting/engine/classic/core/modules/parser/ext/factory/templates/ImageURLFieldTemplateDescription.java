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
import org.pentaho.reporting.engine.classic.core.filter.templates.ImageURLFieldTemplate;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;

import java.net.URL;

/**
 * An image URL field template description.
 *
 * @author Thomas Morgner
 */
public class ImageURLFieldTemplateDescription extends AbstractTemplateDescription {
  private static final Log logger = LogFactory.getLog( ImageURLFieldTemplateDescription.class );

  /**
   * Creates a new template description.
   *
   * @param name
   *          the name.
   */
  public ImageURLFieldTemplateDescription( final String name ) {
    super( name, ImageURLFieldTemplate.class, true );
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final ImageURLFieldTemplate t = (ImageURLFieldTemplate) super.createObject();
    if ( t.getBaseURL() == null ) {
      final String baseURL = getConfig().getConfigProperty( AbstractXmlResourceFactory.CONTENTBASE_KEY );
      try {
        final URL bURL = new URL( baseURL );
        t.setBaseURL( bURL );
      } catch ( Exception e ) {
        ImageURLFieldTemplateDescription.logger.warn(
            "Relative URLs may not be resolvable, as the BaseURL is invalid.", e );
      }
    }
    return t;
  }
}
