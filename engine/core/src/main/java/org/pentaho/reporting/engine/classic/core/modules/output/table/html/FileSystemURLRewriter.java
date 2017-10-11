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

package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.repository.ContentEntity;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.UrlRepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * This URL rewriter assumes that the content repository is an URL based repository and that each content entity can be
 * resolved to an URL.
 *
 * @author Thomas Morgner
 */
public class FileSystemURLRewriter implements URLRewriter {
  public FileSystemURLRewriter() {
  }

  public String rewrite( final ContentEntity sourceDocument, final ContentEntity dataEntity )
    throws URLRewriteException {
    final Repository dataRepository = dataEntity.getRepository();
    if ( dataRepository instanceof UrlRepository == false ) {
      // cannot proceed ..
      throw new URLRewriteException( "DataRepository is no URL-Repository." );
    }

    final UrlRepository dataUrlRepo = (UrlRepository) dataRepository;
    final String dataPath = buildPath( dataEntity );
    final URL dataItemUrl;
    try {
      dataItemUrl = new URL( dataUrlRepo.getURL(), dataPath );
    } catch ( MalformedURLException e ) {
      // cannot proceed ..
      throw new URLRewriteException( "DataEntity has no valid URL." );
    }

    final Repository documentRepository = sourceDocument.getRepository();
    if ( documentRepository instanceof UrlRepository == false ) {
      // If at least the data entity has an URL, we can always fall back
      // to an global URL..
      return dataItemUrl.toExternalForm();
    }

    try {
      final UrlRepository documentUrlRepo = (UrlRepository) documentRepository;
      final String documentPath = buildPath( sourceDocument );
      final URL documentUrl = new URL( documentUrlRepo.getURL(), documentPath );
      return IOUtils.getInstance().createRelativeURL( dataItemUrl, documentUrl );
    } catch ( MalformedURLException e ) {
      // If at least the data entity has an URL, we can always fall back
      // to an global URL..
      return dataItemUrl.toExternalForm();
    }
  }

  private String buildPath( final ContentEntity entity ) {
    final ArrayList entityNames = new ArrayList();
    entityNames.add( entity.getName() );

    ContentLocation location = entity.getParent();
    int size = 0;
    while ( location != null ) {
      final ContentLocation parent = location.getParent();
      if ( parent != null ) {
        final String locationName = location.getName();
        size += locationName.length();
        size += 1;
        entityNames.add( locationName );
      }
      location = location.getParent();
    }

    final StringBuffer b = new StringBuffer( size );
    for ( int i = entityNames.size() - 1; i >= 0; i-- ) {
      final String name = (String) entityNames.get( i );
      b.append( name );
      if ( i != 0 ) {
        b.append( '/' );
      }
    }
    return b.toString();
  }
}
