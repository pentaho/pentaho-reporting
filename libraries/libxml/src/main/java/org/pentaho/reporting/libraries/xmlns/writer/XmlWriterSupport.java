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
* Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.xmlns.writer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.FastStack;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList.AttributeEntry;

/**
 * A support class for writing XML files.
 *
 * @author Thomas Morgner
 */
public class XmlWriterSupport {

  private static final Log LOGGER = LogFactory.getLog( XmlWriterSupport.class );

  /**
   * An internal state-management class containing the state for nested tags.
   */
  private static class ElementLevel {
    private String namespace;
    private String prefix;
    private final String tagName;
    private final DeclaredNamespaces namespaces;

    /**
     * Creates a new ElementLevel object.
     *
     * @param namespace  the namespace of the current tag (can be null).
     * @param prefix     the namespace prefix of the current tag (can be null).
     * @param tagName    the tagname (never null).
     * @param namespaces the collection of all currently known namespaces (never null).
     */
    protected ElementLevel( final String namespace,
                            final String prefix,
                            final String tagName,
                            final DeclaredNamespaces namespaces ) {
      if ( tagName == null ) {
        throw new NullPointerException();
      }
      if ( namespaces == null ) {
        throw new NullPointerException();
      }
      this.prefix = prefix;
      this.namespace = namespace;
      this.tagName = tagName;
      this.namespaces = namespaces;
    }

    /**
     * Creates a new ElementLevel with no namespace information.
     *
     * @param tagName    the xml-tagname.
     * @param namespaces the currently known namespaces.
     */
    protected ElementLevel( final String tagName,
                            final DeclaredNamespaces namespaces ) {
      if ( tagName == null ) {
        throw new NullPointerException();
      }
      if ( namespaces == null ) {
        throw new NullPointerException();
      }
      this.namespaces = namespaces;
      this.tagName = tagName;
    }

    /**
     * Returns the defined namespace prefix for this entry.
     *
     * @return the namespace prefix.
     */
    public String getPrefix() {
      return prefix;
    }

    /**
     * Returns the defined namespace uri for this entry.
     *
     * @return the namespace uri.
     */
    public String getNamespace() {
      return namespace;
    }

    /**
     * Returns the tagname for this entry.
     *
     * @return the tagname.
     */
    public String getTagName() {
      return tagName;
    }

    /**
     * Returns the map of defined namespace for this entry.
     *
     * @return the namespaces.
     */
    public DeclaredNamespaces getNamespaces() {
      return namespaces;
    }
  }


  /**
   * A constant for controlling the indent function.
   */
  public static final int OPEN_TAG_INCREASE = 1;

  /**
   * A constant for controlling the indent function.
   */
  public static final int CLOSE_TAG_DECREASE = 2;

  /**
   * A constant for controlling the indent function.
   */
  public static final int INDENT_ONLY = 3;

  /**
   * A constant for close.
   */
  public static final boolean CLOSE = true;

  /**
   * A constant for open.
   */
  public static final boolean OPEN = false;

  /**
   * A list of safe tags.
   */
  private final TagDescription safeTags;

  /**
   * The indent level for that writer.
   */
  private final FastStack openTags;

  /**
   * The indent string.
   */
  private final String indentString;

  private boolean lineEmpty;
  private int additionalIndent;

  private boolean alwaysAddNamespace;
  private boolean assumeDefaultNamespace;
  private HashMap impliedNamespaces;
  private boolean writeFinalLinebreak;
  private boolean htmlCompatiblityMode;
  private final String lineSeparator;
  private CharsetEncoder charsetEncoder;

  /**
   * Default Constructor. The created XMLWriterSupport will not have no safe tags and starts with an indention level of
   * 0.
   */
  public XmlWriterSupport() {
    this( new DefaultTagDescription(), "  " );
  }


  /**
   * Creates a new support instance.
   *
   * @param safeTags     the tags that are safe for line breaks.
   * @param indentString the indent string.
   */
  public XmlWriterSupport( final TagDescription safeTags,
                           final String indentString ) {
    this( safeTags, indentString, StringUtils.getLineSeparator() );
  }

  /**
   * Create a new XmlWriterSupport instance.
   *
   * @param safeTags      the tags that are safe for line breaks.
   * @param indentString  the indent string.
   * @param lineseparator the lineseparator that should be used for writing XML files.
   */
  public XmlWriterSupport( final TagDescription safeTags,
                           final String indentString,
                           final String lineseparator ) {
    if ( indentString == null ) {
      throw new NullPointerException( "IndentString must not be null" );
    }
    if ( safeTags == null ) {
      throw new NullPointerException( "SafeTags must not be null" );
    }
    if ( lineseparator == null ) {
      throw new NullPointerException( "LineSeparator must not be null" );
    }

    this.safeTags = safeTags;
    openTags = new FastStack();
    this.indentString = indentString;
    lineEmpty = true;
    writeFinalLinebreak = true;
    lineSeparator = lineseparator;

    addImpliedNamespace( "http://www.w3.org/XML/1998/namespace", "xml" );
  }


  /**
   * Checks, whether the HTML compatibility mode is enabled. In HTML compatibility mode, closed empty tags will have a
   * space between the tagname and the close-indicator.
   *
   * @return true, if the HTML compatiblity mode is enabled, false otherwise.
   */
  public boolean isHtmlCompatiblityMode() {
    return htmlCompatiblityMode;
  }

  /**
   * Enables or disables the HTML Compatibility mode. In HTML compatibility mode, closed empty tags will have a space
   * between the tagname and the close-indicator.
   *
   * @param htmlCompatiblityMode true, if the HTML compatiblity mode is enabled, false otherwise.
   */
  public void setHtmlCompatiblityMode( final boolean htmlCompatiblityMode ) {
    this.htmlCompatiblityMode = htmlCompatiblityMode;
  }

  /**
   * Checks, whether the XML writer should always add a namespace prefix to the attributes. The XML specification leaves
   * it up to the application on how to handle unqualified attributes. If this mode is enabled, all attributes will
   * always be fully qualified - which removed the ambugity but may not be compatible with simple, non namespace aware
   * parsers.
   *
   * @return true, if all attributes should be qualified, false otherwise.
   */
  public boolean isAlwaysAddNamespace() {
    return alwaysAddNamespace;
  }

  /**
   * Defines, whether the XML writer should always add a namespace prefix to the attributes. The XML specification
   * leaves it up to the application on how to handle unqualified attributes. If this mode is enabled, all attributes
   * will always be fully qualified - which removed the ambuigity but may not be compatible with simple, non namespace
   * aware parsers.
   *
   * @param alwaysAddNamespace set to true, if all attributes should be qualified, false otherwise.
   */
  public void setAlwaysAddNamespace( final boolean alwaysAddNamespace ) {
    this.alwaysAddNamespace = alwaysAddNamespace;
  }

  /**
   * Returns the indent level that should be added to the automaticly computed indentation.
   *
   * @return the indent level.
   */
  public int getAdditionalIndent() {
    return additionalIndent;
  }

  /**
   * Defines the indent level that should be added to the automaticly computed indentation.
   *
   * @param additionalIndent the indent level.
   */
  public void setAdditionalIndent( final int additionalIndent ) {
    this.additionalIndent = additionalIndent;
  }

  /**
   * Returns the line separator.
   *
   * @return the line separator.
   */
  public String getLineSeparator() {
    return lineSeparator;
  }


  /**
   * Writes the XML declaration that usually appears at the top of every XML file.
   *
   * @param encoding the encoding that should be declared (this has to match the encoding of the writer, or funny things
   *                 may happen when parsing the xml file later).
   * @throws java.io.IOException if there is a problem writing to the character stream.
   */
  public void writeXmlDeclaration( final Writer writer, final String encoding )
    throws IOException {
    if ( encoding == null ) {
      writer.write( "<?xml version=\"1.0\"?>" );
      writer.write( getLineSeparator() );
      return;
    }

    writer.write( "<?xml version=\"1.0\" encoding=\"" );
    writer.write( encoding );
    writer.write( "\"?>" );
    writer.write( getLineSeparator() );
    setEncoding( encoding );
  }

  public void setEncoding( final String encoding ) {
    final Charset charset = Charset.forName( encoding );
    charsetEncoder = charset.newEncoder();
  }

  /**
   * Writes an opening XML tag that has no attributes.
   *
   * @param w            the writer.
   * @param namespaceUri the namespace URI for the element.
   * @param name         the tag name.
   * @throws java.io.IOException if there is an I/O problem.
   */
  public void writeTag( final Writer w,
                        final String namespaceUri,
                        final String name )
    throws IOException {
    writeTag( w, namespaceUri, name, null, XmlWriterSupport.OPEN );
  }

  /**
   * Writes a closing XML tag.
   *
   * @param w the writer.
   * @throws java.io.IOException if there is an I/O problem.
   */
  public void writeCloseTag( final Writer w )
    throws IOException {
    indentForClose( w );
    final ElementLevel level = (ElementLevel) openTags.pop();

    setLineEmpty( false );

    w.write( "</" );
    final String prefix = level.getPrefix();
    if ( prefix != null ) {
      w.write( prefix );
      w.write( ":" );
      w.write( level.getTagName() );
    } else {
      w.write( level.getTagName() );
    }
    w.write( ">" );
    doEndOfLine( w );
  }

  /**
   * Writes a linebreak to the writer.
   *
   * @param writer the writer.
   * @throws IOException if there is a problem writing to the character stream.
   */
  public void writeNewLine( final Writer writer )
    throws IOException {
    if ( isLineEmpty() == false ) {
      writer.write( lineSeparator );
      setLineEmpty( true );
    }
  }

  /**
   * Checks, whether the currently generated line of text is empty.
   *
   * @return true, if the line is empty, false otherwise.
   */
  public boolean isLineEmpty() {
    return lineEmpty;
  }

  /**
   * A marker flag to track, wether the current line is empty. This influences the indention.
   *
   * @param lineEmpty defines, whether the current line should be treated as empty line.
   */
  public void setLineEmpty( final boolean lineEmpty ) {
    this.lineEmpty = lineEmpty;
  }

  /**
   * Writes an opening XML tag with an attribute/value pair.
   *
   * @param w              the writer.
   * @param namespace      the namespace URI for the element
   * @param name           the tag name.
   * @param attributeName  the attribute name.
   * @param attributeValue the attribute value.
   * @param close          controls whether the tag is closed.
   * @throws java.io.IOException if there is an I/O problem.
   */
  public void writeTag( final Writer w,
                        final String namespace,
                        final String name,
                        final String attributeName,
                        final String attributeValue,
                        final boolean close )
    throws IOException {
    if ( attributeName != null ) {
      final AttributeList attr = new AttributeList();
      attr.setAttribute( namespace, attributeName, attributeValue );
      writeTag( w, namespace, name, attr, close );
    } else {
      writeTag( w, namespace, name, null, close );
    }
  }

  /**
   * Adds an implied namespace to the document. Such a namespace is not explicitly declared, it is assumed that the
   * xml-parser knows the prefix by some other means. Using implied namespaces for standalone documents is almost always
   * a bad idea.
   *
   * @param uri    the uri of the namespace.
   * @param prefix the defined prefix.
   */
  public void addImpliedNamespace( final String uri, final String prefix ) {
    if ( openTags.isEmpty() == false ) {
      throw new IllegalStateException( "Cannot modify the implied namespaces in the middle of the processing" );
    }

    if ( prefix == null ) {
      if ( impliedNamespaces == null ) {
        return;
      }
      impliedNamespaces.remove( uri );
    } else {
      if ( impliedNamespaces == null ) {
        impliedNamespaces = new HashMap();
      }
      impliedNamespaces.put( uri, prefix );
    }
  }

  /**
   * Copies all currently declared namespaces of the given XmlWriterSupport instance as new implied namespaces into this
   * instance.
   *
   * @param writerSupport the Xml-writer from where to copy the declared namespaces.
   */
  public void copyNamespaces( final XmlWriterSupport writerSupport ) {
    if ( openTags.isEmpty() == false ) {
      throw new IllegalStateException( "Cannot modify the implied namespaces in the middle of the processing" );
    }

    if ( impliedNamespaces == null ) {
      impliedNamespaces = new HashMap();
    }

    if ( writerSupport.openTags.isEmpty() == false ) {
      final ElementLevel parent = (ElementLevel) writerSupport.openTags.peek();
      impliedNamespaces.putAll( parent.getNamespaces().getNamespaces() );
    }

    if ( writerSupport.impliedNamespaces != null ) {
      impliedNamespaces.putAll( writerSupport.impliedNamespaces );
    }
  }

  /**
   * Checks, whether the given URI is defined as valid namespace.
   *
   * @param uri the uri of the namespace.
   * @return true, if there's a namespace defined, false otherwise.
   */
  public boolean isNamespaceDefined( final String uri ) {
    if ( impliedNamespaces != null ) {
      if ( impliedNamespaces.containsKey( uri ) ) {
        return true;
      }
    }
    if ( openTags.isEmpty() ) {
      return false;
    }
    final ElementLevel parent = (ElementLevel) openTags.peek();
    return parent.getNamespaces().isNamespaceDefined( uri );
  }

  /**
   * Checks, whether the given namespace prefix is defined.
   *
   * @param prefix the namespace prefix.
   * @return true, if the prefix is defined, false otherwise.
   */
  public boolean isNamespacePrefixDefined( final String prefix ) {
    if ( impliedNamespaces != null ) {
      if ( impliedNamespaces.containsValue( prefix ) ) {
        return true;
      }
    }
    if ( openTags.isEmpty() ) {
      return false;
    }
    final ElementLevel parent = (ElementLevel) openTags.peek();
    return parent.getNamespaces().isPrefixDefined( prefix );
  }

  /**
   * Returns all namespaces as properties-collection. This reflects the currently defined namespaces, therefore calls to
   * writeOpenTag(..) might cause this method to return different collections.
   *
   * @return the defined namespaces.
   */
  public Properties getNamespaces() {
    final Properties namespaces = new Properties();
    if ( openTags.isEmpty() ) {
      if ( impliedNamespaces != null ) {
        //noinspection UseOfPropertiesAsHashtable
        namespaces.putAll( impliedNamespaces );
      }
      return namespaces;
    }

    final ElementLevel parent = (ElementLevel) openTags.peek();
    //noinspection UseOfPropertiesAsHashtable
    namespaces.putAll( parent.getNamespaces().getNamespaces() );
    return namespaces;
  }

  /**
   * Computes the current collection of defined namespaces.
   *
   * @return the namespaces declared at this writing position.
   */
  protected DeclaredNamespaces computeNamespaces() {
    if ( openTags.isEmpty() ) {
      final DeclaredNamespaces namespaces = new DeclaredNamespaces();
      if ( impliedNamespaces != null ) {
        return namespaces.add( impliedNamespaces );
      }
      return namespaces;
    }

    final ElementLevel parent = (ElementLevel) openTags.peek();
    return parent.getNamespaces();
  }

  /**
   * Writes an opening XML tag along with a list of attribute/value pairs.
   *
   * @param w            the writer.
   * @param namespaceUri the namespace uri for the element (can be null).
   * @param name         the tag name.
   * @param attributes   the attributes.
   * @param close        controls whether the tag is closed.
   * @throws java.io.IOException if there is an I/O problem.
   */
  public void writeTag( final Writer w,
                        final String namespaceUri,
                        final String name,
                        final AttributeList attributes,
                        final boolean close )
    throws IOException {
    if ( name == null ) {
      throw new NullPointerException();
    }

    indent( w );
    setLineEmpty( false );

    DeclaredNamespaces namespaces = computeNamespaces();

    if ( attributes != null ) {
      namespaces = namespaces.add( attributes );
    }

    w.write( "<" );

    if ( namespaceUri == null ) {
      w.write( name );
      openTags.push( new ElementLevel( name, namespaces ) );
    } else {
      final String nsPrefix = namespaces.getPrefix( namespaceUri );
      if ( nsPrefix == null ) {
        throw new IllegalArgumentException( "Namespace " + namespaceUri + " is not defined." );
      }
      if ( "".equals( nsPrefix ) ) {
        w.write( name );
        openTags.push( new ElementLevel( namespaceUri, null, name, namespaces ) );
      } else {
        w.write( nsPrefix );
        w.write( ":" );
        w.write( name );
        openTags.push( new ElementLevel( namespaceUri, nsPrefix, name, namespaces ) );
      }
    }

    if ( attributes != null ) {
      final AttributeList.AttributeEntry[] entries = attributes.toArray();
      for ( final AttributeEntry entry : entries ) {
        w.write( " " );

        buildAttributeName( entry, namespaces, w );
        w.write( "=\"" );
        writeTextNormalized( w, entry.getValue(), true );
        w.write( "\"" );
      }
    }

    if ( close ) {
      if ( isHtmlCompatiblityMode() ) {
        w.write( " />" );
      } else {
        w.write( "/>" );
      }

      openTags.pop();
      doEndOfLine( w );
    } else {
      w.write( ">" );
      doEndOfLine( w );
    }
  }

  /**
   * Conditionally writes an end-of-line character. The End-Of-Line is only written, if the tag description indicates
   * that the currently open element does not expect any CDATA inside. Writing a newline for CDATA-elements may have
   * sideeffects.
   *
   * @param w the writer.
   * @throws java.io.IOException if there is an I/O problem.
   */
  private void doEndOfLine( final Writer w )
    throws IOException {
    if ( openTags.isEmpty() ) {
      if ( isWriteFinalLinebreak() ) {
        writeNewLine( w );
      }
    } else {
      final ElementLevel level = (ElementLevel) openTags.peek();
      if ( getTagDescription().hasCData( level.getNamespace(), level.getTagName() ) == false ) {
        writeNewLine( w );
      }
    }
  }

  /**
   * Processes a single attribute and searches for namespace declarations. If a namespace declaration is found, it is
   * returned in a normalized way. If namespace processing is active, the attribute name will be fully qualified with
   * the prefix registered for the attribute's namespace URI.
   *
   * @param entry      the attribute enty.
   * @param namespaces the currently known namespaces.
   * @param writer     the writer that should receive the formatted attribute name.
   * @throws IOException if an IO error occured.
   */
  private void buildAttributeName( final AttributeList.AttributeEntry entry,
                                   final DeclaredNamespaces namespaces,
                                   final Writer writer ) throws IOException {
    final ElementLevel currentElement = (ElementLevel) openTags.peek();
    final String name = entry.getName();
    final String namespaceUri = entry.getNamespace();

    if ( ( isAlwaysAddNamespace() == false ) && ObjectUtilities.equal( currentElement.getNamespace(), namespaceUri ) ) {
      writer.write( name );
      return;
    }

    if ( namespaceUri == null ) {
      writer.write( name );
      return;
    }

    if ( AttributeList.XMLNS_NAMESPACE.equals( namespaceUri ) ) {
      // its a namespace declaration.
      if ( "".equals( name ) ) {
        writer.write( "xmlns" );
        return;
      }

      writer.write( "xmlns:" );
      writer.write( name );
      return;
    }

    final String namespacePrefix = namespaces.getPrefix( namespaceUri );
    if ( ( namespacePrefix != null ) && ( "".equals( namespacePrefix ) == false ) ) {
      writer.write( namespacePrefix );
      writer.write( ':' );
      writer.write( name );
    } else {
      writer.write( name );
    }
  }

  /**
   * Normalizes the given string using a shared buffer.
   *
   * @param s                the string that should be XML-Encoded.
   * @param transformNewLine a flag controling whether to transform newlines into character-entities.
   * @return the transformed string.
   */
  public String normalizeLocal( final String s, final boolean transformNewLine ) throws IOException {
    return normalize( s, transformNewLine );
  }


  /**
   * Normalizes the given string and writes the result directly to the stream.
   *
   * @param writer           the writer that should receive the normalized content.
   * @param s                the string that should be XML-Encoded.
   * @param transformNewLine a flag controling whether to transform newlines into character-entities.
   * @throws IOException if writing to the stream failed.
   */
  public void writeTextNormalized( final Writer writer, final String s, final boolean transformNewLine )
    throws IOException {
    writeTextNormalized( writer, s, charsetEncoder, transformNewLine );
  }

  private static void writeTextNormalized( final Writer writer, final String s, final CharsetEncoder encoder,
      final boolean transformNewLine ) throws IOException {

    if ( s == null ) {
      return;
    }

    final StringBuilder strB = new StringBuilder( s.length() );
    for ( int offset = 0; offset < s.length(); ) {
      final int cp = s.codePointAt( offset );

      switch ( cp ) {
        case 9: // \t
          strB.appendCodePoint( cp );
          break;
        case 10: // \n
          if ( transformNewLine ) {
            strB.append( "&#10;" );
            break;
          }
          strB.appendCodePoint( cp );
          break;
        case 13: // \r
          if ( transformNewLine ) {
            strB.append( "&#13;" );
            break;
          }
          strB.appendCodePoint( cp );
          break;
        case 60: // <
          strB.append( "&lt;" );
          break;
        case 62: // >
          strB.append( "&gt;" );
          break;
        case 34: // "
          strB.append( "&quot;" );
          break;
        case 38: // &
          strB.append( "&amp;" );
          break;
        case 39: // '
          strB.append( "&apos;" );
          break;
        default:
          if ( cp >= 0x20 ) {
            final String cpStr = new String( new int[] { cp }, 0, 1 );
            if ( ( encoder != null ) && !encoder.canEncode( cpStr ) ) {
              strB.append( "&#x" + Integer.toHexString( cp ) );
            } else {
              strB.appendCodePoint( cp );
            }
          }
      }

      offset += Character.charCount( cp );
    }

    writer.write( strB.toString() );
  }

  /**
   * Normalises a string, replacing certain characters with their escape sequences so that the XML text is not
   * corrupted.
   *
   * @param s                the string.
   * @param transformNewLine true, if a newline in the string should be converted into a character entity.
   * @return the normalised string.
   */
  public static String normalize( final String s, final boolean transformNewLine ) {
    if ( s == null ) {
      return "";
    }

    final StringWriter writer = new StringWriter( s.length() );

    try {
      writeTextNormalized( writer, s, null, transformNewLine );
    } catch ( final IOException e ) {
      LOGGER.error( e );
      return s;
    }
    return writer.toString();
  }

  /**
   * Indent the line. Called for proper indenting in various places.
   *
   * @param writer the writer which should receive the indentention.
   * @throws java.io.IOException if writing the stream failed.
   */
  public void indent( final Writer writer )
    throws IOException {
    if ( openTags.isEmpty() ) {
      for ( int i = 0; i < additionalIndent; i++ ) {
        writer.write( indentString );
      }
      return;
    }

    final ElementLevel level = (ElementLevel) openTags.peek();
    if ( getTagDescription().hasCData( level.getNamespace(),
      level.getTagName() ) == false ) {
      doEndOfLine( writer );

      for ( int i = 0; i < openTags.size(); i++ ) {
        writer.write( indentString );
      }

      for ( int i = 0; i < additionalIndent; i++ ) {
        writer.write( indentString );
      }
    }
  }

  /**
   * Indent the line. Called for proper indenting in various places.
   *
   * @param writer the writer which should receive the indentention.
   * @throws java.io.IOException if writing the stream failed.
   */
  public void indentForClose( final Writer writer )
    throws IOException {
    if ( openTags.isEmpty() ) {
      for ( int i = 0; i < additionalIndent; i++ ) {
        writer.write( indentString );
      }
      return;
    }

    final ElementLevel level = (ElementLevel) openTags.peek();
    if ( getTagDescription().hasCData( level.getNamespace(),
      level.getTagName() ) == false ) {
      doEndOfLine( writer );

      for ( int i = 1; i < openTags.size(); i++ ) {
        writer.write( indentString );
      }
      for ( int i = 0; i < additionalIndent; i++ ) {
        writer.write( indentString );
      }
    }
  }

  /**
   * Returns the list of safe tags.
   *
   * @return The list.
   */
  public TagDescription getTagDescription() {
    return safeTags;
  }

  /**
   * Writes a comment into the generated xml file.
   *
   * @param writer  the writer.
   * @param comment the comment text
   * @throws IOException if there is a problem writing to the character stream.
   */
  public void writeComment( final Writer writer, final String comment )
    throws IOException {
    if ( openTags.isEmpty() == false ) {
      final ElementLevel level = (ElementLevel) openTags.peek();
      if ( getTagDescription().hasCData( level.getNamespace(), level.getTagName() ) == false ) {
        indent( writer );
      }
    }

    setLineEmpty( false );

    writer.write( "<!-- " );
    writeTextNormalized( writer, comment, false );
    writer.write( " -->" );
    doEndOfLine( writer );
  }

  /**
   * Checks, whether attributes of the same namespace as the current element should be written without a prefix.
   * Attributes without a prefix are considered to be not in any namespace at all. How to treat such attributes is
   * implementation dependent. (Appendix A; Section 6.2 of the XmlNamespaces recommendation)
   *
   * @return true, if attributes in the element's namespace should be written without a prefix, false to write all
   * attributes with a prefix.
   */
  public boolean isAssumeDefaultNamespace() {
    return assumeDefaultNamespace;
  }

  /**
   * Defines, whether attributes of the same namespace as the current element should be written without a prefix.
   * Attributes without a prefix are considered to be not in any namespace at all. How to treat such attributes is
   * implementation dependent. (Appendix A; Section 6.2 of the XmlNamespaces recommendation)
   *
   * @param assumeDefaultNamespace true, if attributes in the element's namespace should be written without a prefix,
   *                               false to write all attributes with a prefix.
   */
  public void setAssumeDefaultNamespace( final boolean assumeDefaultNamespace ) {
    this.assumeDefaultNamespace = assumeDefaultNamespace;
  }

  /**
   * Returns the current indention level.
   *
   * @return the indention level.
   */
  public int getCurrentIndentLevel() {
    return additionalIndent + openTags.size();
  }

  /**
   * Defines, whether the written XML file should end with an empty line.
   *
   * @param writeFinalLinebreak true, if an linebreak should be added at the end of the file, false otherwise.
   */
  public void setWriteFinalLinebreak( final boolean writeFinalLinebreak ) {
    this.writeFinalLinebreak = writeFinalLinebreak;
  }

  /**
   * Checks, whether the written XML file should end with an empty line.
   *
   * @return true, if an linebreak should be added at the end of the file, false otherwise.
   */
  public boolean isWriteFinalLinebreak() {
    return writeFinalLinebreak;
  }
}
