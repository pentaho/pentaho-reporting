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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A repository that can be globally identified by an URL.
 *
 * @author Thomas Morgner
 */
public interface UrlRepository extends Repository {
  /**
   * Returns the URL that represents this repository. The meaning of the URL returned here is implementation specific
   * and is probably not suitable to resolve names to global objects.
   *
   * @return the repository's URL.
   * @throws MalformedURLException if the URL could not be computed.
   */
  public URL getURL() throws MalformedURLException;
}
