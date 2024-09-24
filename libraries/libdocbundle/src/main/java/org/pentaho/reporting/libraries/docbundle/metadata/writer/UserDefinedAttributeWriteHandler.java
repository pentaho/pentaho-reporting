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
import org.pentaho.reporting.libraries.docbundle.metadata.UserDefinedAttribute;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserDefinedAttributeWriteHandler implements BundleMetaDataEntryWriteHandler {
  public UserDefinedAttributeWriteHandler() {
  }

  public void write( final BundleMetaDataXmlWriter bundleWriter,
                     final XmlWriter writer,
                     final String entryNamespace,
                     final String entryName,
                     final Object entry ) throws IOException {
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
    if ( entry == null ) {
      throw new NullPointerException();
    }

    if ( entry.getClass().isArray() == false ) {
      return;
    }

    final int size = Array.getLength( entry );
    for ( int i = 0; i < size; i++ ) {
      final Object entryValue = Array.get( entry, i );
      if ( entryValue instanceof UserDefinedAttribute == false ) {
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

      final UserDefinedAttribute userDefinedAttribute = (UserDefinedAttribute) entryValue;
      final String valueType;
      final String value;
      final Object o = userDefinedAttribute.getValue();
      if ( o instanceof Time ) {
        valueType = "time";

        final SimpleDateFormat sdf = new SimpleDateFormat( "'PT'HH'H'mm'M'ss'S'" );
        value = sdf.format( o );
      } else if ( o instanceof Date ) {
        valueType = "date";

        final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'hh:mm:ss' 'z" );
        value = sdf.format( o );
      } else if ( o instanceof Number ) {
        value = o.toString();
        valueType = "float";
      } else if ( o instanceof Boolean ) {
        value = o.toString();
        valueType = "boolean";
      } else if ( o != null ) {
        value = o.toString();
        valueType = "string";
      } else {
        return;
      }


      attributeList.setAttribute( ODFMetaAttributeNames.Meta.NAMESPACE, "name", userDefinedAttribute.getName() );
      attributeList.setAttribute( ODFMetaAttributeNames.Office.NAMESPACE, "value-type", valueType );
      writer.writeTag( entryNamespace, entryName, attributeList, XmlWriterSupport.OPEN );
      writer.writeTextNormalized( value, false );
      writer.writeCloseTag();
    }
  }
}
