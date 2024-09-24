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

package org.pentaho.reporting.engine.classic.extensions.datasources.cda;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CdaResponseParser extends DefaultHandler {
  private static final Log logger = LogFactory.getLog( CdaResponseParser.class );

  private TypedTableModel model = new TypedTableModel();
  private ArrayList<String> columnRawData;
  private StringBuffer characterBuffer;
  private int rowNumber;
  private boolean nullValue;
  private SimpleDateFormat format;

  public CdaResponseParser() {
    format = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US );
    model = new TypedTableModel();
  }

  public TypedTableModel getResult() {
    return model;
  }

  public void startElement( final String uri,
                            final String localName,
                            final String qName,
                            final Attributes attributes ) throws SAXException {
    if ( "ColumnMetaData".equals( qName ) ) {
      final String type = attributes.getValue( "type" );
      final String name = attributes.getValue( "name" );
      if ( StringUtils.isEmpty( name ) ) {
        throw new ParseException( "Column name is not given" );
      }
      if ( StringUtils.isEmpty( type ) ) {
        throw new ParseException( "Column type is not given" );
      }

      final Class colType;
      if ( "Numeric".equals( type ) ||
        "Integer".equals( type ) ) {
        colType = Number.class;
      } else if ( "Date".equals( type ) ) {
        colType = Date.class;
      } else {
        colType = String.class;
      }
      model.addColumn( name, colType );

    } else if ( "Row".equals( qName ) ) {
      columnRawData = new ArrayList<String>();
    } else if ( "Col".equals( qName ) ) {
      if ( "true".equals( attributes.getValue( "isNull" ) ) ) {
        nullValue = true;
        characterBuffer = null;
      } else {
        nullValue = false;
        characterBuffer = new StringBuffer();
      }
    }
  }

  public void endElement( final String uri, final String localName, final String qName ) throws SAXException {
    try {
      if ( "Row".equals( qName ) ) {
        final int size = Math.min( columnRawData.size(), model.getColumnCount() );
        for ( int i = 0; i < size; i++ ) {
          final String value = columnRawData.get( i );
          if ( value == null ) {
            model.setValueAt( value, rowNumber, i );
          } else {
            final Class columnClass = model.getColumnClass( i );
            if ( Date.class == columnClass ) {
              model.setValueAt( format.parse( value ), rowNumber, i );
            } else if ( Number.class == columnClass ) {
              model.setValueAt( new BigDecimal( value ), rowNumber, i );
            } else {
              model.setValueAt( value, rowNumber, i );
            }
          }
        }

        rowNumber += 1;
        columnRawData = null;
      } else if ( "Col".equals( qName ) ) {
        if ( nullValue ) {
          columnRawData.add( null );
        } else {
          columnRawData.add( characterBuffer.toString() );
        }

        nullValue = false;
        characterBuffer = null;
      }
    } catch ( Exception e ) {
      throw new ParseException( e );
    }
  }

  public void characters( final char[] ch, final int start, final int length ) throws SAXException {
    if ( characterBuffer != null ) {
      characterBuffer.append( ch, start, length );
    }
  }

  public static TypedTableModel performParse( final InputStream postResult )
    throws IOException, ReportDataFactoryException {
    try {
      final CdaResponseParser contentHandler = new CdaResponseParser();
      final SAXParserFactory factory = SAXParserFactory.newInstance();
      final SAXParser parser = factory.newSAXParser();
      final XMLReader reader = parser.getXMLReader();

      try {
        reader.setFeature( "http://xml.org/sax/features/xmlns-uris", false );
      } catch ( SAXException e ) {
        // ignored
      }
      try {
        reader.setFeature( "http://xml.org/sax/features/namespaces", false );
        reader.setFeature( "http://xml.org/sax/features/namespace-prefixes", false );
      } catch ( final SAXException e ) {
        logger.warn( "No Namespace features will be available. (Yes, this is serious)", e );
      }

      reader.setContentHandler( contentHandler );
      reader.parse( new InputSource( postResult ) );

      return ( contentHandler.getResult() );
    } catch ( final ParserConfigurationException e ) {
      throw new ReportDataFactoryException( "Failed to init XML system", e );
    } catch ( final SAXException e ) {
      throw new ReportDataFactoryException( "Failed to parse document", e );
    }
  }
}
