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

package org.pentaho.reporting.libraries.xmlns.writer;

import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;


/**
 * A class for writing XML to a character stream.
 *
 * @author Thomas Morgner
 */
public class XmlWriter {
  /**
   * A constant for close.
   */
  public static final boolean CLOSE = true;

  /**
   * A constant for open.
   */
  public static final boolean OPEN = false;

  /**
   * The character stream.
   */
  private Writer writer;
  private XmlWriterSupport support;

  /**
   * Creates a new XML writer for the specified character stream.  By default, four spaces are used for indentation.
   *
   * @param writer the character stream.
   */
  public XmlWriter( final Writer writer ) {
    this( writer, "  " );
  }

  /**
   * Default Constructor. The created XMLWriterSupport will not have no safe tags and starts with an indention level of
   * 0.
   *
   * @param writer         the character stream.
   * @param tagDescription the tags that are safe for line breaks.
   */
  public XmlWriter( final Writer writer, final TagDescription tagDescription ) {
    this( writer, tagDescription, "  " );
  }

  /**
   * Creates a new XML writer for the specified character stream.
   *
   * @param writer       the character stream.
   * @param indentString the string used for indentation (should contain white space, for example four spaces).
   */
  public XmlWriter( final Writer writer, final String indentString ) {
    this( writer, new DefaultTagDescription(), indentString );
  }

  /**
   * Creates a new XMLWriter instance.
   *
   * @param writer         the character stream.
   * @param tagDescription the tags that are safe for line breaks.
   * @param indentString   the indent string.
   */
  public XmlWriter( final Writer writer,
                    final TagDescription tagDescription,
                    final String indentString ) {
    if ( writer == null ) {
      throw new NullPointerException( "Writer must not be null." );
    }

    this.support = new XmlWriterSupport( tagDescription, indentString );
    this.writer = writer;
  }

  /**
   * Creates a new XMLWriter instance.
   *
   * @param writer         the character stream.
   * @param tagDescription the tags that are safe for line breaks.
   * @param indentString   the indent string.
   * @param lineSeparator  the line separator to be used.
   */
  public XmlWriter( final Writer writer,
                    final TagDescription tagDescription,
                    final String indentString,
                    final String lineSeparator ) {
    if ( writer == null ) {
      throw new NullPointerException( "Writer must not be null." );
    }

    this.support = new XmlWriterSupport( tagDescription, indentString, lineSeparator );
    this.writer = writer;
  }

  /**
   * Writes the XML declaration that usually appears at the top of every XML file.
   *
   * @param encoding the encoding that should be declared (this has to match the encoding of the writer, or funny things
   *                 may happen when parsing the xml file later).
   * @throws java.io.IOException if there is a problem writing to the character stream.
   */
  public void writeXmlDeclaration( final String encoding )
    throws IOException {
    support.writeXmlDeclaration( writer, encoding );
  }

  /**
   * Writes an opening XML tag that has no attributes.
   *
   * @param namespace the namespace URI for the element
   * @param name      the tag name.
   * @param close     a flag that controls whether or not the tag is closed immediately.
   * @throws java.io.IOException if there is an I/O problem.
   */
  public void writeTag( final String namespace,
                        final String name,
                        final boolean close )
    throws IOException {
    if ( close ) {
      support.writeTag( this.writer, namespace, name, null, XmlWriterSupport.CLOSE );
    } else {
      support.writeTag( this.writer, namespace, name, null, XmlWriterSupport.OPEN );
    }
  }

  /**
   * Writes a closing XML tag.
   *
   * @throws java.io.IOException if there is an I/O problem.
   */
  public void writeCloseTag()
    throws IOException {
    support.writeCloseTag( this.writer );
  }

  /**
   * Writes an opening XML tag with an attribute/value pair.
   *
   * @param namespace      the namespace URI for the element
   * @param name           the tag name.
   * @param attributeName  the attribute name.
   * @param attributeValue the attribute value.
   * @param close          controls whether the tag is closed.
   * @throws java.io.IOException if there is an I/O problem.
   */
  public void writeTag( final String namespace,
                        final String name,
                        final String attributeName,
                        final String attributeValue,
                        final boolean close )
    throws IOException {
    support.writeTag( this.writer, namespace, name, attributeName, attributeValue, close );
  }

  /**
   * Writes an opening XML tag along with a list of attribute/value pairs.
   *
   * @param namespace  the namespace URI for the element
   * @param name       the tag name.
   * @param attributes the attributes.
   * @param close      controls whether the tag is closed.
   * @throws java.io.IOException if there is an I/O problem.
   */
  public void writeTag( final String namespace,
                        final String name,
                        final AttributeList attributes,
                        final boolean close )
    throws IOException {
    support.writeTag( this.writer, namespace, name, attributes, close );
  }

  /**
   * Writes some text to the character stream.
   *
   * @param text the text.
   * @throws IOException if there is a problem writing to the character stream.
   */
  public void writeText( final String text )
    throws IOException {
    this.writer.write( text );
    support.setLineEmpty( false );
  }

  /**
   * Writes the given text into the stream using a streaming xml-normalization method.
   *
   * @param s                the string to be written.
   * @param transformNewLine whether to encode newlines using character-entities.
   * @throws IOException if an IO error occured.
   */
  public void writeTextNormalized( final String s,
                                   final boolean transformNewLine ) throws IOException {
    support.writeTextNormalized( writer, s, transformNewLine );
  }

  /**
   * Copies the given reader to the character stream. This method should be used for large chunks of data.
   *
   * @param reader the reader providing the text.
   * @throws IOException if there is a problem writing to the character stream.
   */
  public void writeStream( final Reader reader ) throws IOException {
    IOUtils.getInstance().copyWriter( reader, writer );
    support.setLineEmpty( false );
  }

  /**
   * Closes the underlying character stream.
   *
   * @throws IOException if there is a problem closing the character stream.
   */
  public void close()
    throws IOException {
    this.writer.close();
  }

  /**
   * Writes a comment into the generated xml file.
   *
   * @param comment the comment text
   * @throws IOException if there is a problem writing to the character stream.
   */
  public void writeComment( final String comment )
    throws IOException {
    support.writeComment( writer, comment );
  }

  /**
   * Writes a linebreak to the writer.
   *
   * @throws IOException if there is a problem writing to the character stream.
   */
  public void writeNewLine()
    throws IOException {
    support.writeNewLine( writer );
  }

  /**
   * Flushs the underlying writer.
   *
   * @throws IOException if something goes wrong.
   */
  public void flush()
    throws IOException {
    this.writer.flush();
  }

  public boolean isNamespaceDefined( final String namespace ) {
    return support.isNamespaceDefined( namespace );
  }

  public void setAlwaysAddNamespace( final boolean b ) {
    support.setAlwaysAddNamespace( b );
  }

  public void addImpliedNamespace( final String uri, final String prefix ) {
    support.addImpliedNamespace( uri, prefix );
  }

  public void setAssumeDefaultNamespace( final boolean assumeDefaultNamespace ) {
    support.setAssumeDefaultNamespace( assumeDefaultNamespace );
  }

  public boolean isAssumeDefaultNamespace() {
    return support.isAssumeDefaultNamespace();
  }

  public boolean isAlwaysAddNamespace() {
    return support.isAlwaysAddNamespace();
  }

  public void setHtmlCompatiblityMode( final boolean htmlCompatiblityMode ) {
    support.setHtmlCompatiblityMode( htmlCompatiblityMode );
  }

  public boolean isHtmlCompatiblityMode() {
    return support.isHtmlCompatiblityMode();
  }

  public void setWriteFinalLinebreak( final boolean writeFinalLinebreak ) {
    support.setWriteFinalLinebreak( writeFinalLinebreak );
  }

  public boolean isWriteFinalLinebreak() {
    return support.isWriteFinalLinebreak();
  }

  public void setEncoding( final String encoding ) {
    support.setEncoding( encoding );
  }

  public void setLineEmpty( final boolean lineEmpty ) {
    support.setLineEmpty( lineEmpty );
  }

  public boolean isLineEmpty() {
    return support.isLineEmpty();
  }

  public boolean isNamespacePrefixDefined( final String prefix ) {
    return support.isNamespacePrefixDefined( prefix );
  }

  public int getAdditionalIndent() {
    return support.getAdditionalIndent();
  }

  public void setAdditionalIndent( final int additionalIndent ) {
    support.setAdditionalIndent( additionalIndent );
  }

  public TagDescription getTagDescription() {
    return support.getTagDescription();
  }
}
