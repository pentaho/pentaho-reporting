/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.imagemap.parser;

import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMapEntry;
import org.pentaho.reporting.libraries.formatting.FastDecimalFormat;
import org.pentaho.reporting.libraries.xmlns.LibXmlInfo;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

public class ImageMapWriter {
  private ImageMapWriter() {
  }

  public static String writeImageMapAsString( final ImageMap imageMap ) {
    try {
      final DefaultTagDescription tagDescription = new DefaultTagDescription();
      tagDescription.setDefaultNamespace( LibXmlInfo.XHTML_NAMESPACE );
      tagDescription.setNamespaceHasCData( LibXmlInfo.XHTML_NAMESPACE, false );
      final StringWriter sbwriter = new StringWriter( 5000 );
      final XmlWriter writer = new XmlWriter( sbwriter );
      writer.setHtmlCompatiblityMode( true );
      writer.setWriteFinalLinebreak( true );
      writeImageMap( writer, imageMap, 1 );
      writer.close();
      return sbwriter.toString();
    } catch ( IOException ioe ) {
      // now where does a StringWriter get its IO troubles from?
      throw new IllegalStateException( "Failed to write ImageMap - I am confused." );
    }
  }

  public static void writeImageMap( final XmlWriter writer, final ImageMap imageMap, final double scale )
    throws IOException {
    final AttributeList attrs = new AttributeList();
    if ( writer.isNamespaceDefined( LibXmlInfo.XHTML_NAMESPACE ) == false ) {
      attrs.addNamespaceDeclaration( "", LibXmlInfo.XHTML_NAMESPACE );
    }

    final String[] mapNamespaces = imageMap.getNameSpaces();
    for ( int i = 0; i < mapNamespaces.length; i++ ) {
      final String namespace = mapNamespaces[i];
      final String[] names = imageMap.getNames( namespace );
      for ( int j = 0; j < names.length; j++ ) {
        final String name = names[j];
        final String value = imageMap.getAttribute( namespace, name );
        attrs.setAttribute( namespace, name, value );
      }
    }

    writer.writeTag( LibXmlInfo.XHTML_NAMESPACE, "map", attrs, XmlWriter.OPEN );
    final ImageMapEntry[] imageMapEntries = imageMap.getMapEntries();
    for ( int i = 0; i < imageMapEntries.length; i++ ) {
      final ImageMapEntry mapEntry = imageMapEntries[i];
      writeMapEntry( writer, mapEntry, scale );
    }
    writer.writeCloseTag();
  }

  private static void writeMapEntry( final XmlWriter writer, final ImageMapEntry mapEntry, final double scale )
    throws IOException {
    final AttributeList attrs = new AttributeList();
    final String[] mapNamespaces = mapEntry.getNameSpaces();
    for ( int i = 0; i < mapNamespaces.length; i++ ) {
      final String namespace = mapNamespaces[i];
      final String[] names = mapEntry.getNames( namespace );
      for ( int j = 0; j < names.length; j++ ) {
        final String name = names[j];
        final String value = mapEntry.getAttribute( namespace, name );
        attrs.setAttribute( namespace, name, value );
      }
    }

    final String areaType = mapEntry.getAreaType();
    attrs.setAttribute( LibXmlInfo.XHTML_NAMESPACE, "shape", areaType );
    if ( "default".equals( areaType ) == false ) {
      final float[] coordinates = mapEntry.getAreaCoordinates();
      attrs.setAttribute( LibXmlInfo.XHTML_NAMESPACE, "coords", convertCoordinates( coordinates, scale ) );
    }

    writer.writeTag( LibXmlInfo.XHTML_NAMESPACE, "area", attrs, XmlWriter.CLOSE );
  }

  private static String convertCoordinates( final float[] coordinates, final double scale ) {
    if ( coordinates == null || coordinates.length == 0 ) {
      return null;
    }

    final FastDecimalFormat decimalFormat = new FastDecimalFormat( "######################0.0########", Locale.US );
    final StringBuffer b = new StringBuffer( coordinates.length * 10 );
    b.append( decimalFormat.format( coordinates[0] * scale ) );
    for ( int i = 1; i < coordinates.length; i++ ) {
      final float coordinate = coordinates[i];
      b.append( ',' );
      b.append( decimalFormat.format( coordinate * scale ) );
    }
    return b.toString();
  }
}
