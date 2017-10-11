/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.docbundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * A set of utilitiy methods for working with WritableDocumentBundles.
 */
public final class WriteableDocumentBundleUtils {
  private static final Log logger = LogFactory.getLog( WriteableDocumentBundleUtils.class );

  /**
   * No external constructor
   */
  private WriteableDocumentBundleUtils() {
  }

  /**
   * Removes the specified resource from the resource bundle if that resource exists.
   *
   * @param documentBundle the bundle from which the resource will be removed
   * @param resource       the reference to the resource to be removed
   * @return <code>true</code> if the resource was removed, <code>false</code> otherwise
   * @throws IOException indicates an error trying to remove the resource from the bundle
   */
  public static boolean removeResource( final WriteableDocumentBundle documentBundle,
                                        final ResourceKey resource ) throws IOException {
    if ( documentBundle == null ) {
      throw new IllegalArgumentException();
    }
    if ( resource == null ) {
      throw new IllegalArgumentException();
    }

    if ( documentBundle.isEmbeddedKey( resource ) ) {
      return documentBundle.removeEntry( resource.getIdentifierAsString() );
    }
    return false;
  }


  /**
   * Embeds the specified source resource into the specified document bundle
   *
   * @param documentBundle    the bundle in which the resource will be embedded
   * @param source            the ResourceKey to the source which will be embedded - NOTE: the pattern can specify an
   *                          exact name or a pattern for creating a temporary name. If the name exists, it will be
   *                          replaced.
   * @param pattern           the pattern for the filename to be created
   * @param mimeType          the mimeType of the file to be embedded
   * @param factoryParameters any factory parameters which should be added to the ResourceKey being created
   * @return the ResourceKey for the newly created embedded entry
   */
  public static ResourceKey embedResource( final WriteableDocumentBundle documentBundle,
                                           final ResourceManager sourceManager,
                                           final ResourceKey source,
                                           final String pattern,
                                           final String mimeType,
                                           final Map factoryParameters )
    throws IOException, ResourceException {
    if ( documentBundle == null ) {
      throw new IllegalArgumentException();
    }
    if ( sourceManager == null ) {
      throw new IllegalArgumentException();
    }
    if ( source == null ) {
      throw new IllegalArgumentException();
    }
    if ( pattern == null ) {
      throw new IllegalArgumentException();
    }
    if ( mimeType == null ) {
      throw new IllegalArgumentException();
    }

    // Get a name for the resource
    final String name = BundleUtilities.getUniqueName( documentBundle, pattern );

    // Copy the resource into the bundle
    final ResourceData resourceData = sourceManager.load( source );
    final InputStream in = resourceData.getResourceAsStream( sourceManager );
    try {
      final OutputStream out = documentBundle.createEntry( name, mimeType );
      try {
        IOUtils.getInstance().copyStreams( in, out );
      } finally {
        try {
          out.close();
        } catch ( IOException e ) {
          logger.error( "Error closing input stream", e );
        }
      }
    } finally {
      try {
        in.close();
      } catch ( IOException e ) {
        logger.error( "Error closing output stream", e );
      }
    }

    // Create the resource key which refers to this new entry
    return documentBundle.createResourceKey( name, factoryParameters );
  }
}
