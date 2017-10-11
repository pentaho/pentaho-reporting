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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.tools.configeditor.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.writer.CharacterEntityParser;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;
import org.pentaho.reporting.tools.configeditor.ConfigEditorBoot;
import org.pentaho.reporting.tools.configeditor.Messages;
import org.pentaho.reporting.tools.configeditor.util.DOMUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * This list model implementation collects all config description entries defined in JFreeReport. This model is used to
 * create a configuration key definition; it directly manipulates the metadata for the keys as stored in the
 * config-description.xml file.
 *
 * @author Thomas Morgner
 */
public class ConfigDescriptionModel extends AbstractListModel {
  private static final Log logger = LogFactory.getLog( ConfigDescriptionModel.class );

  /**
   * Compares an config description entry against an other entry. This simple implementation just compares the names of
   * the two entries.
   */
  private static class ConfigEntryComparator implements Comparator<ConfigDescriptionEntry>, Serializable {
    /**
     * DefaultConstructor.
     */
    protected ConfigEntryComparator() {
    }

    /**
     * Compares its two arguments for order.  Returns a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the second.<p>
     *
     * @param o1 the first object to compare
     * @param o2 the second object to compare
     * @return an integer indicating the comparison result.
     */
    public int compare( final ConfigDescriptionEntry o1, final ConfigDescriptionEntry o2 ) {
      if ( o1 == null && o2 == null ) {
        return 0;
      }
      if ( o1 == null ) {
        return -1;
      }
      if ( o2 == null ) {
        return 1;
      }
      return o1.getKeyName().compareTo( o2.getKeyName() );
    }
  }

  /**
   * The content of this list; all config description entries.
   */
  private final ArrayList<ConfigDescriptionEntry> content;

  /**
   * Provides access to externalized strings
   */
  private Messages messages;

  /**
   * Creates a new, initially empty ConfigDescriptionModel.
   */
  public ConfigDescriptionModel() {
    messages = Messages.getInstance();
    content = new ArrayList<ConfigDescriptionEntry>();
  }

  /**
   * Adds the given entry to the end of the list.
   *
   * @param entry the new entry.
   */
  public void add( final ConfigDescriptionEntry entry ) {
    if ( entry == null ) {
      throw new NullPointerException( messages.getString(
        "ConfigDescriptionModel.ERROR_0001_ENTRY_IS_NULL" ) ); //$NON-NLS-1$
    }
    // Only add unique elements ...
    final int index = findEntry( entry.getKeyName() );
    if ( index == -1 ) {
      content.add( entry );
      updated();
    } else {
      content.set( index, entry );
    }
  }

  private int findEntry( final String key ) {
    for ( int i = 0; i < content.size(); i++ ) {
      final ConfigDescriptionEntry configDescriptionEntry = content.get( i );
      if ( key.equals( configDescriptionEntry.getKeyName() ) ) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Removes the given entry from the list.
   *
   * @param entry the entry that should be removed.
   */
  public void remove( final ConfigDescriptionEntry entry ) {
    if ( entry == null ) {
      throw new NullPointerException( messages.getString(
        "ConfigDescriptionModel.ERROR_0002_ENTRY_IS_NULL" ) ); //$NON-NLS-1$
    }

    final int index = findEntry( entry.getKeyName() );
    if ( index != -1 ) {
      content.remove( index );
      updated();
    }
  }

  public void removeAll( final int[] indices ) {
    if ( indices.length == 0 ) {
      return;
    }

    final Object[] entries = new Object[ indices.length ];
    for ( int i = indices.length - 1; i >= 0; i-- ) {
      entries[ i ] = content.get( indices[ i ] );
    }
    for ( int i = 0; i < entries.length; i++ ) {
      content.remove( entries[ i ] );
    }
    updated();
  }

  /**
   * Returns the entry stored on the given list position.
   *
   * @param pos the position
   * @return the entry
   * @throws IndexOutOfBoundsException if the position is invalid.
   */
  public ConfigDescriptionEntry get( final int pos ) {
    return content.get( pos );
  }

  /**
   * Fires an contents changed event for all elements in the list.
   */
  public void updated() {
    fireContentsChanged( this, 0, getSize() );
  }

  /**
   * Returns the index of the given entry or -1, if the entry is not in the list.
   *
   * @param entry the entry whose position should be searched.
   * @return the position of the entry
   */
  public int indexOf( final ConfigDescriptionEntry entry ) {
    if ( entry == null ) {
      throw new NullPointerException( messages.getString(
        "ConfigDescriptionModel.ERROR_0003_ENTRY_IS_NULL" ) ); //$NON-NLS-1$
    }
    return findEntry( entry.getKeyName() );
  }

  /**
   * Checks whether the given entry is already contained in this list.
   *
   * @param entry the entry that should be checked.
   * @return true, if the entry is already added, false otherwise.
   */
  public boolean contains( final ConfigDescriptionEntry entry ) {
    if ( entry == null ) {
      throw new NullPointerException( messages.getString(
        "ConfigDescriptionModel.ERROR_0004_ENTRY_IS_NULL" ) ); //$NON-NLS-1$
    }
    return findEntry( entry.getKeyName() ) > -1;
  }

  /**
   * Sorts the entries of the list. Be aware that calling this method does not fire an updat event; you have to do this
   * manually.
   */
  public void sort() {
    Collections.sort( content, new ConfigEntryComparator() );
    updated();
  }

  /**
   * Returns the contents of this model as object array.
   *
   * @return the contents of the model as array.
   */
  public ConfigDescriptionEntry[] toArray() {
    return content.toArray( new ConfigDescriptionEntry[ content.size() ] );
  }

  /**
   * Returns the length of the list.
   *
   * @return the length of the list
   */
  public int getSize() {
    return content.size();
  }

  /**
   * Returns the value at the specified index.
   *
   * @param index the requested index
   * @return the value at <code>index</code>
   */
  public Object getElementAt( final int index ) {
    final ConfigDescriptionEntry entry = get( index );
    if ( entry == null ) {
      return null;
    }
    return entry.getKeyName();
  }

  /**
   * Imports all entries from the given report configuration. Only new entries will be added to the list. This does not
   * add report properties supplied via the System.properties.
   *
   * @param config the report configuration from where to add the entries.
   */
  public void importFromConfig( final Configuration config ) {
    final Iterator it = config.findPropertyKeys( "" ); //$NON-NLS-1$
    while ( it.hasNext() ) {
      final String keyname = (String) it.next();
      if ( System.getProperties().containsKey( keyname ) ) {
        continue;
      }

      final TextConfigDescriptionEntry entry = new TextConfigDescriptionEntry( keyname );
      if ( contains( entry ) == false ) {
        add( entry );
      }
    }
  }

  /**
   * Loads the entries from the given xml file. The file must be in the format of the config-description.xml file.
   *
   * @param in the inputstream from where to read the file
   * @throws IOException                  if an error occured while reading the file
   * @throws SAXException                 if an XML parse error occurs.
   * @throws ParserConfigurationException if the XML parser could not be initialized.
   */
  public void load( final InputStream in )
    throws IOException, SAXException, ParserConfigurationException {
    content.clear();
    final Document doc = DOMUtilities.parseInputStream( in );
    final Element e = doc.getDocumentElement();
    final NodeList list = e.getElementsByTagName( "key" ); //$NON-NLS-1$
    for ( int i = 0; i < list.getLength(); i++ ) {
      final Element keyElement = (Element) list.item( i );
      final String keyName = keyElement.getAttribute( "name" ); //$NON-NLS-1$
      final boolean keyGlobal = "true".equals( keyElement.getAttribute( "global" ) ); //$NON-NLS-1$
      final boolean keyHidden = "true".equals( keyElement.getAttribute( "hidden" ) ); //$NON-NLS-1$
      final String descr = getDescription( keyElement ).trim();

      final NodeList enumNodes = keyElement.getElementsByTagName( "enum" ); //$NON-NLS-1$
      if ( enumNodes.getLength() != 0 ) {
        final String[] alteratives = collectEnumEntries( (Element) enumNodes.item( 0 ) );
        final EnumConfigDescriptionEntry en = new EnumConfigDescriptionEntry( keyName );
        en.setDescription( descr );
        en.setGlobal( keyGlobal );
        en.setHidden( keyHidden );
        en.setOptions( alteratives );
        add( en );
        continue;
      }

      final NodeList classNodes = keyElement.getElementsByTagName( "class" ); //$NON-NLS-1$
      if ( classNodes.getLength() != 0 ) {
        final Element classElement = (Element) classNodes.item( 0 );
        final String className = classElement.getAttribute( "instanceof" ); //$NON-NLS-1$
        if ( className == null ) {
          throw new ParseException( messages.getString(
            "ConfigDescriptionModel.ERROR_0005_MISSING_INSTANCEOF" ) ); //$NON-NLS-1$
        }
        try {
          final ClassLoader classLoader = ObjectUtilities.getClassLoader( getClass() );
          final Class baseClass = Class.forName( className, false, classLoader );
          final ClassConfigDescriptionEntry ce = new ClassConfigDescriptionEntry( keyName );
          ce.setBaseClass( baseClass );
          ce.setDescription( descr );
          ce.setGlobal( keyGlobal );
          ce.setHidden( keyHidden );
          add( ce );
          continue;
        } catch ( Exception ex ) {
          final String message = messages.getString(
            "ConfigDescriptionModel.ERROR_0006_BASE_CLASS_LOAD_FAILED" ); //$NON-NLS-1$
          ConfigDescriptionModel.logger.error( message, ex );
          continue;
        }
      }

      final NodeList textNodes = keyElement.getElementsByTagName( "text" ); //$NON-NLS-1$
      if ( textNodes.getLength() != 0 ) {
        final TextConfigDescriptionEntry textEntry = new TextConfigDescriptionEntry( keyName );
        textEntry.setDescription( descr );
        textEntry.setGlobal( keyGlobal );
        textEntry.setHidden( keyHidden );
        add( textEntry );
      }
    }
  }

  /**
   * A parser helper method which collects all enumeration entries from the given element.
   *
   * @param element the element from where to read the enumeration entries.
   * @return the entries as string array.
   */
  private String[] collectEnumEntries( final Element element ) {
    final NodeList nl = element.getElementsByTagName( "text" ); //$NON-NLS-1$
    final String[] retval = new String[ nl.getLength() ];
    for ( int i = 0; i < nl.getLength(); i++ ) {
      retval[ i ] = DOMUtilities.getText( (Element) nl.item( i ) ).trim();
    }
    return retval;
  }

  /**
   * A parser helper method that returns the CDATA description of the given element.
   *
   * @param e the element from where to read the description.
   * @return the description text.
   */
  private String getDescription( final Element e ) {
    final NodeList descr = e.getElementsByTagName( "description" ); //$NON-NLS-1$
    if ( descr.getLength() == 0 ) {
      return ""; //$NON-NLS-1$
    }
    return DOMUtilities.getText( (Element) descr.item( 0 ) );
  }

  /**
   * Saves the model into an xml file.
   *
   * @param out      the target output stream.
   * @param encoding the encoding of the content.
   * @throws IOException if an error occurs.
   * @noinspection IOResourceOpenedButNotSafelyClosed
   */
  public void save( final OutputStream out, final String encoding )
    throws IOException {
    // This print-writer will be flushed, but not closed, as closing the underlying stream is not desired here.
    final PrintWriter writer = new PrintWriter( new OutputStreamWriter( out, encoding ) );
    final AttributeList attList = new AttributeList();
    attList.addNamespaceDeclaration( "", ConfigEditorBoot.NAMESPACE ); //$NON-NLS-1$

    final DefaultTagDescription tagDescription = new DefaultTagDescription();
    tagDescription.setDefaultNamespace( ConfigEditorBoot.NAMESPACE );
    tagDescription.setNamespaceHasCData( ConfigEditorBoot.NAMESPACE, false );
    tagDescription.setElementHasCData( ConfigEditorBoot.NAMESPACE, "text", true );
    tagDescription.setElementHasCData( ConfigEditorBoot.NAMESPACE, "description", true );

    final XmlWriter dwriter = new XmlWriter( writer, tagDescription );


    dwriter.writeXmlDeclaration( encoding );
    dwriter.writeTag( ConfigEditorBoot.NAMESPACE,
      "config-description", attList, XmlWriterSupport.OPEN ); //$NON-NLS-1$

    final CharacterEntityParser parser = CharacterEntityParser.createXMLEntityParser();
    for ( int i = 0; i < getSize(); i++ ) {
      final ConfigDescriptionEntry entry = get( i );
      final AttributeList p = new AttributeList();
      p.setAttribute( ConfigEditorBoot.NAMESPACE, "name", entry.getKeyName() ); //$NON-NLS-1$
      p.setAttribute( ConfigEditorBoot.NAMESPACE, "global", String.valueOf( entry.isGlobal() ) ); //$NON-NLS-1$
      p.setAttribute( ConfigEditorBoot.NAMESPACE, "hidden", String.valueOf( entry.isHidden() ) ); //$NON-NLS-1$
      dwriter.writeTag( ConfigEditorBoot.NAMESPACE, "key", p, XmlWriterSupport.OPEN ); //$NON-NLS-1$
      if ( entry.getDescription() != null ) {
        dwriter.writeTag( ConfigEditorBoot.NAMESPACE, "description", XmlWriterSupport.OPEN ); //$NON-NLS-1$
        writer.write( parser.encodeEntities( entry.getDescription() ) );
        dwriter.writeCloseTag();
      }
      if ( entry instanceof ClassConfigDescriptionEntry ) {
        final ClassConfigDescriptionEntry ce = (ClassConfigDescriptionEntry) entry;
        if ( ce.getBaseClass() != null ) {
          dwriter.writeTag( ConfigEditorBoot.NAMESPACE, "class", "instanceof", //$NON-NLS-1$ //$NON-NLS-2$
            ce.getBaseClass().getName(), XmlWriterSupport.CLOSE );
        } else {
          dwriter.writeTag( ConfigEditorBoot.NAMESPACE, "class", "instanceof", //$NON-NLS-1$ //$NON-NLS-2$
            "java.lang.Object", XmlWriterSupport.CLOSE ); //$NON-NLS-1$
        }
      } else if ( entry instanceof TextConfigDescriptionEntry ) {
        dwriter.writeTag( ConfigEditorBoot.NAMESPACE, "text", //$NON-NLS-1$
          new AttributeList(), XmlWriterSupport.CLOSE );
      } else if ( entry instanceof EnumConfigDescriptionEntry ) {
        final EnumConfigDescriptionEntry en = (EnumConfigDescriptionEntry) entry;
        dwriter.writeTag( ConfigEditorBoot.NAMESPACE, "enum", XmlWriterSupport.OPEN ); //$NON-NLS-1$

        final String[] alts = en.getOptions();
        if ( alts != null ) {
          for ( int optCount = 0; optCount < alts.length; optCount++ ) {
            dwriter.writeTag( ConfigEditorBoot.NAMESPACE, "text", XmlWriterSupport.OPEN ); //$NON-NLS-1$
            dwriter.writeTextNormalized( alts[ optCount ], false );
            dwriter.writeCloseTag();
          }
        }
        dwriter.writeCloseTag();
      }
      dwriter.writeCloseTag();
    }
    dwriter.writeCloseTag();
    writer.flush();
  }

}
