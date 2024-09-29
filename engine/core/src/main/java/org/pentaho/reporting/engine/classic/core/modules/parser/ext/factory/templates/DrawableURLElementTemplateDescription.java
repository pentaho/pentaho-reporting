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
import org.pentaho.reporting.engine.classic.core.filter.templates.DrawableURLElementTemplate;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;

import java.net.URL;

/**
 * An image URL element template description.
 *
 * @author Thomas Morgner
 */
public class DrawableURLElementTemplateDescription extends AbstractTemplateDescription {
  private static final Log logger = LogFactory.getLog( DrawableURLElementTemplateDescription.class );

  /**
   * Creates a new template description.
   *
   * @param name
   *          the name.
   */
  public DrawableURLElementTemplateDescription( final String name ) {
    super( name, DrawableURLElementTemplate.class, true );
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final DrawableURLElementTemplate t = (DrawableURLElementTemplate) super.createObject();
    if ( t.getBaseURL() == null ) {
      final String baseURL = getConfig().getConfigProperty( AbstractXmlResourceFactory.CONTENTBASE_KEY );
      if ( baseURL != null ) {
        try {
          final URL bURL = new URL( baseURL );
          t.setBaseURL( bURL );
        } catch ( Exception e ) {
          DrawableURLElementTemplateDescription.logger.warn( "BaseURL is invalid: " + baseURL, e );
        }
      }
    }
    return t;
  }
}
