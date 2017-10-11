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

package org.pentaho.reporting.engine.classic.core.modules.parser.base;

import org.pentaho.reporting.engine.classic.core.util.beans.BeanPropertyLookupParser;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.Attributes;

public class PropertyAttributes extends BeanPropertyLookupParser implements Attributes {
  private Attributes backend;
  private RootXmlReadHandler rootXmlReadHandler;

  public PropertyAttributes( final RootXmlReadHandler rootXmlReadHandler, final Attributes backend ) {
    this.rootXmlReadHandler = rootXmlReadHandler;
    this.backend = backend;
  }

  /**
   * Look up the index of an attribute by XML 1.0 qualified name.
   *
   * @param qName
   *          The qualified (prefixed) name.
   * @return The index of the attribute, or -1 if it does not appear in the list.
   */
  public int getIndex( final String qName ) {
    return backend.getIndex( qName );
  }

  /**
   * Look up the index of an attribute by Namespace name.
   *
   * @param uri
   *          The Namespace URI, or the empty string if the name has no Namespace URI.
   * @param localName
   *          The attribute's local name.
   * @return The index of the attribute, or -1 if it does not appear in the list.
   */
  public int getIndex( final String uri, final String localName ) {
    return backend.getIndex( uri, localName );
  }

  /**
   * Return the number of attributes in the list.
   * <p/>
   * <p>
   * Once you know the number of attributes, you can iterate through the list.
   * </p>
   *
   * @return The number of attributes in the list.
   * @see #getURI(int)
   * @see #getLocalName(int)
   * @see #getQName(int)
   * @see #getType(int)
   * @see #getValue(int)
   */
  public int getLength() {
    return backend.getLength();
  }

  /**
   * Look up an attribute's local name by index.
   *
   * @param index
   *          The attribute index (zero-based).
   * @return The local name, or the empty string if Namespace processing is not being performed, or null if the index is
   *         out of range.
   * @see #getLength
   */
  public String getLocalName( final int index ) {
    return backend.getLocalName( index );
  }

  /**
   * Look up an attribute's XML 1.0 qualified name by index.
   *
   * @param index
   *          The attribute index (zero-based).
   * @return The XML 1.0 qualified name, or the empty string if none is available, or null if the index is out of range.
   * @see #getLength
   */
  public String getQName( final int index ) {
    return backend.getQName( index );
  }

  /**
   * Look up an attribute's type by index.
   * <p/>
   * <p>
   * The attribute type is one of the strings "CDATA", "ID", "IDREF", "IDREFS", "NMTOKEN", "NMTOKENS", "ENTITY",
   * "ENTITIES", or "NOTATION" (always in upper case).
   * </p>
   * <p/>
   * <p>
   * If the parser has not read a declaration for the attribute, or if the parser does not report attribute types, then
   * it must return the value "CDATA" as stated in the XML 1.0 Recommentation (clause 3.3.3, "Attribute-Value
   * Normalization").
   * </p>
   * <p/>
   * <p>
   * For an enumerated attribute that is not a notation, the parser will report the type as "NMTOKEN".
   * </p>
   *
   * @param index
   *          The attribute index (zero-based).
   * @return The attribute's type as a string, or null if the index is out of range.
   * @see #getLength
   */
  public String getType( final int index ) {
    return backend.getType( index );
  }

  /**
   * Look up an attribute's type by XML 1.0 qualified name.
   * <p/>
   * <p>
   * See {@link #getType(int) getType(int)} for a description of the possible types.
   * </p>
   *
   * @param qName
   *          The XML 1.0 qualified name.
   * @return The attribute type as a string, or null if the attribute is not in the list or if qualified names are not
   *         available.
   */
  public String getType( final String qName ) {
    return backend.getType( qName );
  }

  /**
   * Look up an attribute's type by Namespace name.
   * <p/>
   * <p>
   * See {@link #getType(int) getType(int)} for a description of the possible types.
   * </p>
   *
   * @param uri
   *          The Namespace URI, or the empty String if the name has no Namespace URI.
   * @param localName
   *          The local name of the attribute.
   * @return The attribute type as a string, or null if the attribute is not in the list or if Namespace processing is
   *         not being performed.
   */
  public String getType( final String uri, final String localName ) {
    return backend.getType( uri, localName );
  }

  /**
   * Look up an attribute's Namespace URI by index.
   *
   * @param index
   *          The attribute index (zero-based).
   * @return The Namespace URI, or the empty string if none is available, or null if the index is out of range.
   * @see #getLength
   */
  public String getURI( final int index ) {
    return backend.getURI( index );
  }

  /**
   * Look up an attribute's value by index.
   * <p/>
   * <p>
   * If the attribute value is a list of tokens (IDREFS, ENTITIES, or NMTOKENS), the tokens will be concatenated into a
   * single string with each token separated by a single space.
   * </p>
   *
   * @param index
   *          The attribute index (zero-based).
   * @return The attribute's value as a string, or null if the index is out of range.
   * @see #getLength
   */
  public String getValue( final int index ) {
    return translateAndLookup( backend.getValue( index ) );
  }

  /**
   * Look up an attribute's value by XML 1.0 qualified name.
   * <p/>
   * <p>
   * See {@link #getValue(int) getValue(int)} for a description of the possible values.
   * </p>
   *
   * @param qName
   *          The XML 1.0 qualified name.
   * @return The attribute value as a string, or null if the attribute is not in the list or if qualified names are not
   *         available.
   */
  public String getValue( final String qName ) {
    return translateAndLookup( backend.getValue( qName ) );
  }

  /**
   * Look up an attribute's value by Namespace name.
   * <p/>
   * <p>
   * See {@link #getValue(int) getValue(int)} for a description of the possible values.
   * </p>
   *
   * @param uri
   *          The Namespace URI, or the empty String if the name has no Namespace URI.
   * @param localName
   *          The local name of the attribute.
   * @return The attribute value as a string, or null if the attribute is not in the list.
   */
  public String getValue( final String uri, final String localName ) {
    if ( Boolean.TRUE.equals( getRootXmlReadHandler().getHelperObject( "property-expansion" ) ) ) {
      return translateAndLookup( backend.getValue( uri, localName ) );
    }
    return backend.getValue( uri, localName );
  }

  protected RootXmlReadHandler getRootXmlReadHandler() {
    return rootXmlReadHandler;
  }

  protected Object performInitialLookup( final String name ) {
    return rootXmlReadHandler.getHelperObject( name );
  }
}
