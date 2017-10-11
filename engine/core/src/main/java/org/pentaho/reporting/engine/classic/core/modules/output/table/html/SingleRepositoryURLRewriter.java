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

import org.pentaho.reporting.libraries.repository.ContentEntity;
import org.pentaho.reporting.libraries.repository.ContentLocation;

import java.util.ArrayList;

/**
 * This URL-Rewriter assumes that both the content and data entity have been created from the same repository. This one
 * builds a relative URL connecting the data entity with the content.
 *
 * @author Thomas Morgner
 */
public class SingleRepositoryURLRewriter implements URLRewriter {
  public SingleRepositoryURLRewriter() {
  }

  public String rewrite( final ContentEntity sourceDocument, final ContentEntity dataEntity )
    throws URLRewriteException {
    if ( sourceDocument.getRepository().equals( dataEntity.getRepository() ) == false ) {
      // cannot proceed ..
      throw new URLRewriteException( "Content and data repository must be the same." );
    }

    final ArrayList<String> entityNames = new ArrayList<String>();
    entityNames.add( dataEntity.getName() );

    ContentLocation location = dataEntity.getParent();
    while ( location != null ) {
      entityNames.add( location.getName() );
      location = location.getParent();
    }

    if ( sourceDocument instanceof ContentLocation ) {
      location = (ContentLocation) sourceDocument;
    } else {
      location = sourceDocument.getParent();
    }

    final ArrayList<String> contentNames = new ArrayList<String>();
    while ( location != null ) {
      contentNames.add( location.getName() );
      location = location.getParent();
    }

    // now remove all path elements that are equal ..
    while ( contentNames.isEmpty() == false && entityNames.isEmpty() == false ) {
      final String lastEntity = entityNames.get( entityNames.size() - 1 );
      final String lastContent = contentNames.get( contentNames.size() - 1 );
      if ( lastContent.equals( lastEntity ) == false ) {
        break;
      }
      entityNames.remove( entityNames.size() - 1 );
      contentNames.remove( contentNames.size() - 1 );
    }

    final StringBuffer b = new StringBuffer();
    for ( int i = entityNames.size() - 1; i >= 0; i-- ) {
      final String name = entityNames.get( i );
      b.append( name );
      if ( i != 0 ) {
        b.append( '/' );
      }
    }
    return b.toString();
  }
}
