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

import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.reporting.libraries.docbundle.metadata.OfficeDocumentStatistic;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

public class DocumentStatisticWriteHandler implements BundleMetaDataEntryWriteHandler {
  public DocumentStatisticWriteHandler() {
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
    if ( entryValue == null ) {
      throw new NullPointerException();
    }

    if ( entryValue instanceof OfficeDocumentStatistic == false ) {
      return;
    }

    final AttributeList attributeList = new AttributeList();
    if ( writer.isNamespaceDefined( entryNamespace ) == false ) {
      final String defaultNamespace = bundleWriter.getDefaultPrefix( entryNamespace );
      if ( defaultNamespace != null && writer.isNamespacePrefixDefined( defaultNamespace ) == false ) {
        attributeList.addNamespaceDeclaration( defaultNamespace, entryNamespace );
      } else {
        attributeList.addNamespaceDeclaration( "autoGenNs", entryNamespace );
      }
    }

    final OfficeDocumentStatistic statistic = (OfficeDocumentStatistic) entryValue;
    attributeList.setAttribute
      ( ODFMetaAttributeNames.Meta.NAMESPACE, "table-count", String.valueOf( statistic.getTableCount() ) );
    attributeList.setAttribute
      ( ODFMetaAttributeNames.Meta.NAMESPACE, "image-count", String.valueOf( statistic.getImageCount() ) );
    attributeList.setAttribute
      ( ODFMetaAttributeNames.Meta.NAMESPACE, "object-count", String.valueOf( statistic.getObjectCount() ) );
    attributeList.setAttribute
      ( ODFMetaAttributeNames.Meta.NAMESPACE, "page-count", String.valueOf( statistic.getPageCount() ) );
    attributeList.setAttribute
      ( ODFMetaAttributeNames.Meta.NAMESPACE, "paragraph-count", String.valueOf( statistic.getParagraphCount() ) );
    attributeList.setAttribute
      ( ODFMetaAttributeNames.Meta.NAMESPACE, "word-count", String.valueOf( statistic.getWordCount() ) );
    attributeList.setAttribute
      ( ODFMetaAttributeNames.Meta.NAMESPACE, "character-count", String.valueOf( statistic.getCharacterCount() ) );

    writer.writeTag( entryNamespace, entryName, attributeList, XmlWriterSupport.CLOSE );

  }
}
