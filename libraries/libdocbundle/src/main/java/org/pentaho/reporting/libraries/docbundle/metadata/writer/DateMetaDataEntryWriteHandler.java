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


package org.pentaho.reporting.libraries.docbundle.metadata.writer;

import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateMetaDataEntryWriteHandler implements BundleMetaDataEntryWriteHandler {
  public DateMetaDataEntryWriteHandler() {
  }

  public void write( final BundleMetaDataXmlWriter bundleWriter,
                     final XmlWriter writer,
                     final String entryNamespace,
                     final String entryName,
                     final Object entryValue ) throws IOException {
    if ( bundleWriter == null ) {
      throw new NullPointerException();
    }
    if ( writer == null ) {
      throw new NullPointerException();
    }
    if ( entryName == null ) {
      throw new NullPointerException();
    }
    if ( entryNamespace == null ) {
      throw new NullPointerException();
    }
    if ( entryValue instanceof Date == false ) {
      return;
    }

    if ( writer.isNamespaceDefined( entryNamespace ) ) {
      writer.writeTag( entryNamespace, entryName, XmlWriterSupport.OPEN );
    } else {
      final AttributeList attributeList = new AttributeList();
      final String defaultNamespace = bundleWriter.getDefaultPrefix( entryNamespace );
      if ( defaultNamespace != null && writer.isNamespacePrefixDefined( defaultNamespace ) == false ) {
        attributeList.addNamespaceDeclaration( defaultNamespace, entryNamespace );
      } else {
        attributeList.addNamespaceDeclaration( "autoGenNs", entryNamespace );
      }
      writer.writeTag( entryNamespace, entryName, attributeList, XmlWriterSupport.OPEN );
    }

    final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'hh:mm:ss' 'z" );
    writer.writeTextNormalized( String.valueOf( sdf.format( entryValue ) ), false );
    writer.writeCloseTag();
  }
}
