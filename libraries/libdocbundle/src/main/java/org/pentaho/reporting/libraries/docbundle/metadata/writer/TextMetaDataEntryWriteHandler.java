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

import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

public class TextMetaDataEntryWriteHandler implements BundleMetaDataEntryWriteHandler {
  public TextMetaDataEntryWriteHandler() {
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
    writer.writeTextNormalized( String.valueOf( entryValue ), false );
    writer.writeCloseTag();
  }
}
