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


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.filter.templates.DrawableURLFieldTemplate;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;

import java.net.URL;

/**
 * A drawable field template description.
 *
 * @author Thomas Morgner.
 */
public class DrawableURLFieldTemplateDescription extends AbstractTemplateDescription {
  private static final Log logger = LogFactory.getLog( DrawableURLFieldTemplateDescription.class );

  /**
   * Creates a new template description.
   *
   * @param name
   *          the name.
   */
  public DrawableURLFieldTemplateDescription( final String name ) {
    super( name, DrawableURLFieldTemplate.class, true );
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final DrawableURLFieldTemplate t = (DrawableURLFieldTemplate) super.createObject();
    if ( t.getBaseURL() == null ) {
      final String baseURL = getConfig().getConfigProperty( AbstractXmlResourceFactory.CONTENTBASE_KEY );
      try {
        final URL bURL = new URL( baseURL );
        t.setBaseURL( bURL );
      } catch ( Exception e ) {
        DrawableURLFieldTemplateDescription.logger.warn( "BaseURL is invalid: ", e );
      }
    }
    return t;
  }
}
