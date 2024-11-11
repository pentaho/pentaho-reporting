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


package org.pentaho.reporting.libraries.docbundle.metadata.writer;

import org.pentaho.reporting.libraries.docbundle.LibDocBundleBoot;
import org.pentaho.reporting.libraries.docbundle.metadata.BundleManifest;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class BundleManifestXmlWriter {
  public static final String NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0";
  private static final String CONFIG_PREFIX = "org.pentaho.reporting.libraries.docbundle.metadata.writer.manifest.";

  private BundleManifest bundleManifest;

  public BundleManifestXmlWriter( final BundleManifest bundleManifest ) {
    if ( bundleManifest == null ) {
      throw new NullPointerException();
    }

    this.bundleManifest = bundleManifest;
  }

  public void write( final OutputStream out ) throws IOException {
    final DefaultTagDescription tagDescription = new DefaultTagDescription();
    tagDescription.configure( LibDocBundleBoot.getInstance().getGlobalConfig(), CONFIG_PREFIX );

    final XmlWriter writer = new XmlWriter
      ( new OutputStreamWriter( out, "UTF-8" ), tagDescription, "  ", "\n" );
    writer.setAlwaysAddNamespace( true );
    writer.setWriteFinalLinebreak( true );

    try {

      writer.writeXmlDeclaration( "UTF-8" );

      final AttributeList rootAttributes = new AttributeList();
      rootAttributes.addNamespaceDeclaration( "manifest", NAMESPACE );
      writer.writeTag( NAMESPACE, "manifest", rootAttributes, XmlWriterSupport.OPEN );

      final String[] entries = bundleManifest.getEntries();
      Arrays.sort( entries );

      final int length = entries.length;
      for ( int i = 0; i < length; i++ ) {
        final String fullPath = entries[ i ];
        final String mediaType = bundleManifest.getMimeType( fullPath );
        if ( mediaType == null ) {
          continue;
        }

        final AttributeList entryAttrs = new AttributeList();
        final String[] attributeNames = bundleManifest.getAttributeNames( fullPath );

        for ( int j = 0; j < attributeNames.length; j++ ) {
          final String attributeName = attributeNames[ j ];
          final String attr = bundleManifest.getAttribute( fullPath, attributeName );
          if ( attr != null ) {
            entryAttrs.setAttribute( NAMESPACE, attributeName, attr );
          }
        }
        entryAttrs.setAttribute( NAMESPACE, "full-path", fullPath );
        entryAttrs.setAttribute( NAMESPACE, "media-type", mediaType );
        writer.writeTag( NAMESPACE, "file-entry", entryAttrs, XmlWriterSupport.CLOSE );
      }

      writer.writeCloseTag();
    } finally {
      writer.flush();
    }
  }
}
