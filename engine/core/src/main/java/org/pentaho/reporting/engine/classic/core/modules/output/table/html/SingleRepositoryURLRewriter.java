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
