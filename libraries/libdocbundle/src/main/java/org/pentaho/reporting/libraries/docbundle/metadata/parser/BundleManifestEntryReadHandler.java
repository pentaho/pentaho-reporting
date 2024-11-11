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
