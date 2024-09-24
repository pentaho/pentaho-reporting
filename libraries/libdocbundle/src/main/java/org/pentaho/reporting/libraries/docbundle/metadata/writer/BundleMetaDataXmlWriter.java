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

package org.pentaho.reporting.libraries.docbundle.metadata.writer;

import org.pentaho.reporting.libraries.docbundle.LibDocBundleBoot;
import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.reporting.libraries.docbundle.metadata.BundleMetaData;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.NamespaceCollection;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class BundleMetaDataXmlWriter {
  private static final String OFFICE_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";
  private static final String TAGDEF_CONFIG_PREFIX =
    "org.pentaho.reporting.libraries.docbundle.metadata.writer.metadata-tagdefinition.";
  private static final String HANDLER_CONFIG_PREFIX =
    "org.pentaho.reporting.libraries.docbundle.metadata.writer.metadata-writehandler.";

  private NamespaceCollection namespaceCollection;
  private BundleMetaData bundleMetaData;
  private BundleMetaDataWriteHandlerFactory writeHandlerFactory;

  public BundleMetaDataXmlWriter( final BundleMetaData bundleMetaData ) {
    if ( bundleMetaData == null ) {
      throw new NullPointerException();
    }
    this.bundleMetaData = bundleMetaData;
    this.writeHandlerFactory = new BundleMetaDataWriteHandlerFactory();
    this.writeHandlerFactory.configure( LibDocBundleBoot.getInstance().getGlobalConfig(), HANDLER_CONFIG_PREFIX );
  }

  public String getDefaultPrefix( final String uri ) {
    if ( uri == null ) {
      throw new NullPointerException();
    }

    if ( namespaceCollection == null ) {
      namespaceCollection = new NamespaceCollection();
      namespaceCollection.configure( LibDocBundleBoot.getInstance().getGlobalConfig(),
        "org.pentaho.reporting.libraries.docbundle.namespaces." );
    }

    return namespaceCollection.getPrefix( uri );
  }

  public void write( final OutputStream out ) throws IOException {
    if ( out == null ) {
      throw new NullPointerException();
    }

    final DefaultTagDescription tagDescription = new DefaultTagDescription();
    tagDescription.configure( LibDocBundleBoot.getInstance().getGlobalConfig(), TAGDEF_CONFIG_PREFIX );

    final XmlWriter writer = new XmlWriter
      ( new OutputStreamWriter( out, "UTF-8" ), tagDescription, "  ", "\n" );
    writer.setAlwaysAddNamespace( true );
    writer.setWriteFinalLinebreak( true );

    try {

      writer.writeXmlDeclaration( "UTF-8" );

      final AttributeList rootAttributes = new AttributeList();
      rootAttributes.addNamespaceDeclaration( "office", OFFICE_NAMESPACE );
      rootAttributes.addNamespaceDeclaration( "meta", ODFMetaAttributeNames.Meta.NAMESPACE );
      rootAttributes.addNamespaceDeclaration( "dc", ODFMetaAttributeNames.DublinCore.NAMESPACE );
      writer.writeTag( OFFICE_NAMESPACE, "document-meta", rootAttributes, XmlWriterSupport.OPEN );
      writer.writeTag( OFFICE_NAMESPACE, "meta", XmlWriterSupport.OPEN );

      final String[] namespaces = bundleMetaData.getNamespaces();
      final int namspacesLength = namespaces.length;
      for ( int namespaceIdx = 0; namespaceIdx < namspacesLength; namespaceIdx++ ) {
        final String namespace = namespaces[ namespaceIdx ];
        final String[] names = bundleMetaData.getNames( namespace );
        final int namesLength = names.length;
        for ( int nameIdx = 0; nameIdx < namesLength; nameIdx++ ) {
          final String name = names[ nameIdx ];
          final Object value = bundleMetaData.getBundleAttribute( namespace, name );
          if ( value == null ) {
            continue;
          }

          final BundleMetaDataEntryWriteHandler writeHandler = writeHandlerFactory.getHandler( namespace, name );
          if ( writeHandler == null ) {
            continue;
          }

          writeHandler.write( this, writer, namespace, name, value );
        }
      }

      writer.writeCloseTag();
      writer.writeCloseTag();
    } finally {
      writer.flush();
    }
  }
}
