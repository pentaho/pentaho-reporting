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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;

import java.net.URL;

/**
 * An object-description for a <code>URL</code> object.
 *
 * @author Thomas Morgner
 */
public class URLObjectDescription extends AbstractObjectDescription {
  private static final Log logger = LogFactory.getLog( URLObjectDescription.class );

  /**
   * Creates a new object description.
   */
  public URLObjectDescription() {
    super( URL.class );
    setParameterDefinition( "value", String.class );
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final String o = (String) getParameter( "value" );
    final String baseURL = getConfig().getConfigProperty( AbstractXmlResourceFactory.CONTENTBASE_KEY );

    try {
      if ( baseURL != null ) {
        try {
          final URL bURL = new URL( baseURL );
          return new URL( bURL, o );
        } catch ( Exception e ) {
          URLObjectDescription.logger.warn( "BaseURL is invalid: " + baseURL );
        }
      }
      return new URL( o );
    } catch ( Exception e ) {
      return null;
    }
  }

  /**
   * Sets the parameters of this description object to match the supplied object.
   *
   * @param o
   *          the object (should be an instance of <code>URL</code>).
   * @throws ObjectFactoryException
   *           if the object is not an instance of <code>URL</code>.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( !( o instanceof URL ) ) {
      throw new ObjectFactoryException( "Is no instance of java.net.URL" );
    }

    final URL comp = (URL) o;
    final String baseURL = getConfig().getConfigProperty( AbstractXmlResourceFactory.CONTENTBASE_KEY );
    try {
      final URL bURL = new URL( baseURL );
      if ( ObjectUtilities.equal( bURL, comp ) ) {
        setParameter( "value", null );
      } else {
        setParameter( "value", IOUtils.getInstance().createRelativeURL( comp, bURL ) );
      }
    } catch ( Exception e ) {
      URLObjectDescription.logger.warn( "BaseURL is invalid: ", e );
      setParameter( "value", comp.toExternalForm() );
    }
  }

}
