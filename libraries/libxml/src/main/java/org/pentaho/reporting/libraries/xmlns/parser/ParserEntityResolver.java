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

package org.pentaho.reporting.libraries.xmlns.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 * Resolves the JFreeReport DTD specification and routes the parser to a local copy.
 *
 * @author Thomas Morgner
 */
public final class ParserEntityResolver implements EntityResolver {
  private static final Log logger = LogFactory.getLog( ParserEntityResolver.class );

  /**
   * The hashtable for the known entities (deprecated DTDs).
   */
  private final HashMap deprecatedDTDs;
  /**
   * The hashtable for the known entities.
   */
  private final HashMap dtds;
  /**
   * The singleton instance of this entity resolver.
   */
  private static ParserEntityResolver singleton;

  /**
   * Creates a new, uninitialized ParserEntityResolver.
   */
  private ParserEntityResolver() {
    dtds = new HashMap();
    deprecatedDTDs = new HashMap();
  }

  /**
   * Defines a DTD used to validate the report definition. Your XMLParser must be a validating parser for this feature
   * to work.
   *
   * @param publicID the public ID.
   * @param location the URL.
   * @return A boolean.
   */
  public boolean setDTDLocation( final String publicID, final URL location ) {
    if ( isValid( location ) ) {
      this.dtds.put( publicID, location );
      return true;
    } else {
      logger.warn( "Validate location failed for " + publicID + " location: " + location );
      return false;
    }
  }

  /**
   * Defines a DTD used to validate the report definition. Your XMLParser must be a validating parser for this feature
   * to work.
   *
   * @param systemId the system ID for the DTD.
   * @param publicID the public ID.
   * @param location the URL.
   * @return A boolean.
   */
  public boolean setDTDLocation( final String publicID,
                                 final String systemId,
                                 final URL location ) {
    if ( isValid( location ) ) {
      this.dtds.put( publicID, location );
      this.dtds.put( systemId, location );
      return true;
    } else {
      logger.warn( "Validate location failed for " + publicID + " location: " + location );
      return false;
    }
  }

  /**
   * Sets the location of the DTD. This is used for validating XML parsers to validate the structure of the report
   * definition.
   *
   * @param publicID the id.
   * @return the URL for the DTD.
   */
  public URL getDTDLocation( final String publicID ) {
    return (URL) dtds.get( publicID );
  }

  /**
   * Checks whether the speficied URL is readable.
   *
   * @param reportDtd the url pointing to the local DTD copy.
   * @return true, if the URL can be read, false otherwise.
   */
  private boolean isValid( final URL reportDtd ) {
    if ( reportDtd == null ) {
      return false;
    }
    try {
      final InputStream uc = reportDtd.openStream();
      uc.close();
      return true;
    } catch ( IOException ioe ) {
      return false;
    }
  }

  /**
   * Allow the application to resolve external entities.
   * <p/>
   * Resolves the DTD definition to point to a local copy, if the specified public ID is known to this resolver.
   *
   * @param publicId the public ID.
   * @param systemId the system ID.
   * @return The input source.
   */
  public InputSource resolveEntity( final String publicId,
                                    final String systemId ) {
    try {
      // cannot validate without public id ...
      if ( publicId == null ) {
        //Log.debug ("No PUBLIC ID, cannot continue");
        if ( systemId != null ) {
          final URL location = getDTDLocation( systemId );
          if ( location != null ) {
            final InputSource inputSource = new InputSource( location.openStream() );
            inputSource.setSystemId( systemId );
            return inputSource;
          }
        }
        return null;
      }

      final URL location = getDTDLocation( publicId );
      if ( location != null ) {
        final InputSource inputSource = new InputSource( location.openStream() );
        inputSource.setSystemId( systemId );
        inputSource.setPublicId( publicId );
        return inputSource;
      }
      final String message = getDeprecatedDTDMessage( publicId );
      if ( message != null ) {
        logger.info( message );
      } else {
        logger.info( "A public ID was given for the document, but it was unknown or invalid." );
      }
      return null;
    } catch ( IOException ioe ) {
      logger.warn( "Unable to open specified DTD", ioe );
    }
    return null;
  }

  /**
   * Returns a default resolver, which is initialized to redirect the parser to a local copy of the JFreeReport DTDs.
   *
   * @return the default entity resolver.
   */
  public static synchronized ParserEntityResolver getDefaultResolver() {
    if ( singleton == null ) {
      singleton = new ParserEntityResolver();
    }
    return singleton;
  }

  /**
   * Defines that the given public ID should be deprecated and provides a log-message along with the deprecation.
   *
   * @param publicID the public id that should be considered deprecated.
   * @param message  the message to present to the user to warn them about their use of deprecated DTDs.
   */
  public void setDeprecatedDTDMessage( final String publicID, final String message ) {
    deprecatedDTDs.put( publicID, message );
  }


  /**
   * Returns deprecation message for the given public ID.
   *
   * @param publicID the public id that should be considered deprecated.
   * @return the deprecation message or null if the ID is not considered deprecated.
   */
  public String getDeprecatedDTDMessage( final String publicID ) {
    return (String) deprecatedDTDs.get( publicID );
  }
}
