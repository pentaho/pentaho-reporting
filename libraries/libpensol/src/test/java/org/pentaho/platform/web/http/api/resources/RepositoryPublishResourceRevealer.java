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

package org.pentaho.platform.web.http.api.resources;

/**
 * A dummy class to make package-local method public
 *
 * @author Andrey Khayrutdinov
 */
public class RepositoryPublishResourceRevealer extends RepositoryPublishResource {

  @Override
  public boolean invalidPath( String path ) {
    char[] prohibited = new char[] { '\n', '\t', '\r' };
    for ( char c : prohibited ) {
      if ( path.contains( Character.toString( c ) ) ) {
        return true;
      }
    }
    return false;
  }
}
