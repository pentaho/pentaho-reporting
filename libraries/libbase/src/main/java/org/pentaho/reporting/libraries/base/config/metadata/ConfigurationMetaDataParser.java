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

package org.pentaho.reporting.libraries.base.config.metadata;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.boot.Module;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.base.util.XMLParserFactoryProducer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class ConfigurationMetaDataParser {
  /**
   * Parses the given input stream to form a document.
   *
   * @param instream the input stream that should be parsed.
   * @return the parsed document or <code>null</code>, when an error occured
   * @throws javax.xml.parsers.ParserConfigurationException if the parser could not be initalized.
   * @throws org.xml.sax.SAXException                       if the parsing failed due to errors in the xml document
   * @throws java.io.IOException                            if reading from the input stream failed.
   */
  private static Document parseInputStream( final InputStream instream )
    throws ParserConfigurationException, SAXException, IOException {
    final DocumentBuilderFactory dbf = XMLParserFactoryProducer.createSecureDocBuilderFactory();
    final DocumentBuilder db = dbf.newDocumentBuilder();
    return db.parse( new InputSource( instream ) );
  }

  public void parse( final InputStream in, final String domain ) throws IOException {
    try {
      final ConfigurationDomain d = ConfigurationMetaData.getInstance().createDomain( domain );
      final Document doc = parseInputStream( in );
      final Element documentElement = doc.getDocumentElement();
      final NodeList keys = documentElement.getElementsByTagName( "key" );
      for ( int i = 0; i < keys.getLength(); i += 1 ) {
        final ConfigurationMetaDataEntry entr = parseEntry( (Element) keys.item( i ) );
        d.add( entr );
      }
    } catch ( ParserConfigurationException e ) {
      throw new IOException( e );
    } catch ( SAXException e ) {
      throw new IOException( e );
    }
  }

  private ConfigurationMetaDataEntry parseEntry( final Element item ) throws IOException {
    final String name = item.getAttribute( "name" );
    if ( StringUtils.isEmpty( name ) ) {
      throw new IOException( "Name for entry is null" );
    }
    final ConfigurationMetaDataEntry entry = new ConfigurationMetaDataEntry( name );
    entry.setDescription( getText( item, "description" ) );
    entry.setClassName( getClass( item ) );
    entry.setGlobal( "true".equals( item.getAttribute( "global" ) ) );
    entry.setHidden( "true".equals( item.getAttribute( "hidden" ) ) );
    parseEnum( item, entry );
    return entry;
  }

  private void parseEnum( final Element item, final ConfigurationMetaDataEntry entry ) {
    final NodeList nl = item.getElementsByTagName( "enum" );
    if ( nl.getLength() > 0 ) {
      final Element enumElement = (Element) nl.item( 0 );
      final NodeList textNl = enumElement.getElementsByTagName( "text" );
      for ( int i = 0; i < textNl.getLength(); i++ ) {
        final Element text = (Element) textNl.item( i );
        entry.addTag( text.getTextContent(), text.getTextContent() );
      }

      final NodeList entryNl = enumElement.getElementsByTagName( "entry" );
      for ( int i = 0; i < entryNl.getLength(); i++ ) {
        final Element text = (Element) entryNl.item( i );
        entry.addTag( getText( text, "text" ), getText( text, "display-name" ) );
      }
    }
  }

  private String getClass( final Element base ) {
    final NodeList nl = base.getElementsByTagName( "class" );
    if ( nl.getLength() > 0 ) {
      final Element descElement = (Element) nl.item( 0 );
      final String attr = descElement.getAttribute( "instanceof" );
      if ( StringUtils.isEmpty( attr ) == false ) {
        return attr;
      }
    }
    return null;
  }

  private String getText( final Element base, final String elementName ) {
    final NodeList nl = base.getElementsByTagName( elementName );
    if ( nl.getLength() > 0 ) {
      final Element descElement = (Element) nl.item( 0 );
      return descElement.getTextContent();
    }
    return null;
  }

  public void parseConfiguration( final AbstractBoot boot ) throws IOException {
    final String domain = boot.getConfigurationDomain();
    final Module[] activeModules = boot.getPackageManager().getActiveModules();
    for ( int i = 0; i < activeModules.length; i++ ) {
      final Module activeModule = activeModules[ i ];
      final InputStream resourceAsStream = activeModule.getClass().getResourceAsStream( "config-description.xml" );
      if ( resourceAsStream != null ) {
        try {
          parse( resourceAsStream, domain );
        } finally {
          resourceAsStream.close();
        }
      }
    }
  }
}
