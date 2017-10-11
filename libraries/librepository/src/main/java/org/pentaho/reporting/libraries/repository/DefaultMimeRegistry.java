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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.repository;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.Serializable;
import java.util.Iterator;

/**
 * The default-mime registry contains a list of well-known file types and returns mime-information for them. This
 * implementation recognizes a couple of image types and CSS, XML and HTML files. The content is recognized by its
 * filename, not by its actual content.
 *
 * @author Thomas Morgner
 */
public class DefaultMimeRegistry implements MimeRegistry, Serializable {
  private static final long serialVersionUID = 2815922456302361614L;
  private static final String SUFFIX_KEY_PREFIX = "org.pentaho.report.libraries.repository.mime-registry.suffix";
  private static final String SUFFIX_KEY_PREFIX_WDOT = SUFFIX_KEY_PREFIX + ".";

  private static final Configuration configuration = LibRepositoryBoot.getInstance().getGlobalConfig();
  private static final String defaultMimeType = configuration.getConfigProperty
    ( "org.pentaho.report.libraries.repository.mime-registry.default-mimetype", "application/octet-stream" );

  /**
   * Default Constructor.
   */
  public DefaultMimeRegistry() {
  }

  /**
   * Queries the mime-type for a given content-item. Some repositories store mime-type information along with the
   * content data, while others might resort to heuristics based on the filename or actual data stored in the item.
   *
   * @param item the content item for which Mime-Data should be queried.
   * @return the mime-type never null.
   */
  public String getMimeType( final ContentItem item ) {
    final String name = item.getName();
    if ( name == null ) {
      return defaultMimeType;
    }
    final String extension = IOUtils.getInstance().getFileExtension( name ).toLowerCase();
    return configuration.getConfigProperty( SUFFIX_KEY_PREFIX + extension, "application/octet-stream" );
  }

  /**
   * Queries the mime-type for a given filename. Some repositories store mime-type information along with the content
   * data, while others might resort to heuristics based on the filename or actual data stored in the item.
   *
   * @param filename the content item for which Mime-Data should be queried.
   * @return the mime-type never null.
   */
  public String getMimeType( final String filename ) {
    if ( filename == null ) {
      return defaultMimeType;
    }
    final String extension = IOUtils.getInstance().getFileExtension( filename ).toLowerCase();
    return configuration.getConfigProperty( SUFFIX_KEY_PREFIX + extension, "application/octet-stream" );
  }

  /**
   * Returns the default suffix for files with the given content type.
   *
   * @param mimeType the mime-type for which a suffix is queried.
   * @return the suffix, never null.
   */
  public String getSuffix( final String mimeType ) {
    final Iterator propertyKeys = configuration.findPropertyKeys( SUFFIX_KEY_PREFIX_WDOT );
    while ( propertyKeys.hasNext() ) {
      final String key = (String) propertyKeys.next();
      final String keyMimeType = configuration.getConfigProperty( key );
      if ( ObjectUtilities.equal( keyMimeType, mimeType ) ) {
        return key.substring( SUFFIX_KEY_PREFIX_WDOT.length() );
      }
    }
    return configuration.getConfigProperty
      ( "org.pentaho.report.libraries.repository.mime-registry.default-suffix", "bin" );
  }
}
