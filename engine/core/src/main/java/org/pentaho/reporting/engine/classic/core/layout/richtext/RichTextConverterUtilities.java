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

package org.pentaho.reporting.engine.classic.core.layout.richtext;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.List;

public class RichTextConverterUtilities {
  private RichTextConverterUtilities() {
  }

  public static Document parseDocument( final EditorKit editorKit, final Object value )
    throws IOException, BadLocationException {
    if ( value instanceof Document ) {
      return (Document) value;
    }

    final InputStream inputStream = RichTextConverterUtilities.convertToStream( value );
    if ( inputStream != null ) {
      final Document doc = editorKit.createDefaultDocument();
      editorKit.read( inputStream, doc, 0 );
      return doc;
    }

    final Reader reader = RichTextConverterUtilities.convertToReader( value );
    if ( reader != null ) {
      final Document doc = editorKit.createDefaultDocument();
      editorKit.read( reader, doc, 0 );
      return doc;
    }

    return null;
  }

  public static boolean isValidReaderOrStream( final Object o ) {
    if ( o instanceof byte[] ) {
      return true;
    }
    if ( o instanceof Blob ) {
      return true;
    }
    if ( o instanceof InputStream ) {
      return true;
    }
    if ( o instanceof char[] ) {
      return true;
    }
    if ( o instanceof String ) {
      return true;
    }
    if ( o instanceof Clob ) {
      return true;
    }
    if ( o instanceof Reader ) {
      return true;
    }
    return false;
  }

  public static InputStream convertToStream( final Object o ) throws IOException {
    if ( o instanceof byte[] ) {
      return new ByteArrayInputStream( (byte[]) o );
    }
    if ( o instanceof Blob ) {
      final Blob b = (Blob) o;
      try {
        return b.getBinaryStream();
      } catch ( SQLException e ) {
        throw new IOException( "Failed to convert from BLOB" );
      }
    }
    if ( o instanceof InputStream ) {
      return (InputStream) o;
    }
    return null;
  }

  public static Reader convertToReader( final Object o ) throws IOException {
    if ( o instanceof char[] ) {
      return new StringReader( new String( (char[]) o ) );
    }
    if ( o instanceof String ) {
      return new StringReader( (String) o );
    }
    if ( o instanceof Clob ) {
      final Clob b = (Clob) o;
      try {
        return b.getCharacterStream();
      } catch ( SQLException e ) {
        throw new IOException( "Failed to convert from BLOB" );
      }
    }
    if ( o instanceof Reader ) {
      return (Reader) o;
    }
    return null;
  }

  public static Band convertToBand( final List<StyleKey> definedStyleKeys,
                                    final ReportElement element, final Element child ) {
    final Band b = new Band( element.getObjectID() );
    final ElementStyleSheet targetStyle = b.getStyle();
    final ElementStyleSheet sourceStyle = element.getStyle();
    for ( StyleKey key : definedStyleKeys ) {
      targetStyle.setStyleProperty( key, sourceStyle.getStyleProperty( key ) );
    }

    final String[] attrNs = element.getAttributeNamespaces();
    for ( int i = 0; i < attrNs.length; i++ ) {
      final String attrNamespace = attrNs[ i ];
      final String[] attrNames = element.getAttributeNames( attrNamespace );
      for ( int j = 0; j < attrNames.length; j++ ) {
        final String attrName = attrNames[ j ];
        final Object attrValue = element.getAttribute( attrNamespace, attrName );
        b.setAttribute( attrNamespace, attrName, attrValue );
      }
    }
    b.addElement( child );
    b.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "block" );
    return b;
  }

}
