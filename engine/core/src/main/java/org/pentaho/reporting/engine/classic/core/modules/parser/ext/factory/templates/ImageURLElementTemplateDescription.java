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
