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
