/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.docbundle.metadata.parser;

import org.pentaho.reporting.libraries.docbundle.metadata.DefaultBundleManifest;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Thomas Morgner
 */
public class BundleManifestReadHandler extends AbstractXmlReadHandler {
  private DefaultBundleManifest manifest;

  public BundleManifestReadHandler() {
    manifest = new DefaultBundleManifest();
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( BundleManifestXmlFactoryModule.NAMESPACE.equals( uri ) == false ) {
      return null;
    }
    if ( "file-entry".equals( tagName ) == false ) {
      return null;
    }
    return new BundleManifestEntryReadHandler( manifest );
  }

  public Object getObject() throws SAXException {
    return manifest;
  }
}
