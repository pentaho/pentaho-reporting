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
