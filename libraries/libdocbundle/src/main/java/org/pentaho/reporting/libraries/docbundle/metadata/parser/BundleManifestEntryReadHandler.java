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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.docbundle.metadata.parser;

import org.pentaho.reporting.libraries.docbundle.metadata.DefaultBundleManifest;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BundleManifestEntryReadHandler extends AbstractXmlReadHandler {
  private DefaultBundleManifest manifest;

  public BundleManifestEntryReadHandler( final DefaultBundleManifest manifest ) {
    if ( manifest == null ) {
      throw new NullPointerException();
    }

    this.manifest = manifest;
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    final String fullPath = attrs.getValue( BundleManifestXmlFactoryModule.NAMESPACE, "full-path" );
    if ( fullPath == null ) {
      throw new ParseException( "Required attribute 'full-path' is missing.", getLocator() );
    }
    final String mediaType = attrs.getValue( BundleManifestXmlFactoryModule.NAMESPACE, "media-type" );
    manifest.addEntry( fullPath, mediaType );

    final int length = attrs.getLength();
    for ( int i = 0; i < length; i++ ) {
      final String attr = attrs.getValue( i );
      final String name = attrs.getLocalName( i );
      if ( "media-type".equals( name ) ) {
        continue;
      }
      if ( "full-path".equals( name ) ) {
        continue;
      }
      if ( BundleManifestXmlFactoryModule.NAMESPACE.equals( attrs.getURI( i ) ) == false ) {
        continue;
      }
      manifest.setAttribute( fullPath, name, attr );
    }

  }

  public Object getObject() throws SAXException {
    return null;
  }
}
