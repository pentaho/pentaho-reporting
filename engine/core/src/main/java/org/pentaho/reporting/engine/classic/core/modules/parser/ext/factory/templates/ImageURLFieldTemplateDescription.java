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
