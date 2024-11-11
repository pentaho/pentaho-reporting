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


package org.pentaho.reporting.libraries.repository;

/**
 * The MimeRegistry encodes content-type information and allows to detect or query the file type of a given content
 * item. It also assists in naming files by providing a default suffix for a given mime-type.
 *
 * @author Thomas Morgner
 */
public interface MimeRegistry {
  /**
   * Queries the mime-type for a given content-item. Some repositories store mime-type information along with the
   * content data, while others might resort to heuristics based on the filename or actual data stored in the item.
   *
   * @param item the content item for which Mime-Data should be queried.
   * @return the mime-type never null.
   */
  public String getMimeType( ContentItem item );

  /**
   * Returns the default suffix for files with the given content type.
   *
   * @param mimeType the mime-type for which a suffix is queried.
   * @return the suffix, never null.
   */
  public String getSuffix( String mimeType );
}
