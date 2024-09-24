/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.libraries.xmlns.parser;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import java.util.ArrayList;

/**
 * The comment handler is used to collect all XML comments from the SAX parser. The parser implementation must support
 * comments to make this feature work.
 *
 * @author Thomas Morgner
 */
public class CommentHandler implements LexicalHandler {

  /**
   * A constant marking a comment on the opening tag.
   */
  public static final String OPEN_TAG_COMMENT = "parser.comment.open";

  /**
   * A constant marking a comment on the closing tag.
   */
  public static final String CLOSE_TAG_COMMENT = "parser.comment.close";

  /**
   * A list containing all collected comments.
   */
  private final ArrayList comment;

  /**
   * a flag marking whether the SAX parser is currently working in the DTD.
   */
  private boolean inDTD;
  private static final String[] EMPTY_COMMENTS = new String[ 0 ];

  /**
   * DefaultConstructor.
   */
  public CommentHandler() {
    this.comment = new ArrayList();
  }

  /**
   * Report the start of DTD declarations, if any. <p/> <p>This method is empty.</p>
   *
   * @param name     The document type name.
   * @param publicId The declared public identifier for the external DTD subset, or null if none was declared.
   * @param systemId The declared system identifier for the external DTD subset, or null if none was declared.
   * @throws org.xml.sax.SAXException The application may raise an exception.
   * @see #endDTD()
   * @see #startEntity(String)
   */
  public void startDTD( final String name, final String publicId,
                        final String systemId ) throws SAXException {
    this.inDTD = true;
  }

  /**
   * Report the end of DTD declarations. <p/> <p>This method is empty.</p>
   *
   * @throws SAXException The application may raise an exception.
   */
  public void endDTD()
    throws SAXException {
    this.inDTD = false;
  }

  /**
   * Report the beginning of some internal and external XML entities. <p/> <p>This method is empty.</p>
   *
   * @param name The name of the entity.  If it is a parameter entity, the name will begin with '%', and if it is the
   *             external DTD subset, it will be "[dtd]".
   * @throws SAXException The application may raise an exception.
   * @see #endEntity(String)
   * @see org.xml.sax.ext.DeclHandler#internalEntityDecl
   * @see org.xml.sax.ext.DeclHandler#externalEntityDecl
   */
  public void startEntity( final String name )
    throws SAXException {
    // do nothing
  }

  /**
   * Report the end of an entity. <p/> <p>This method is empty.</p>
   *
   * @param name The name of the entity that is ending.
   * @throws SAXException The application may raise an exception.
   * @see #startEntity(String)
   */
  public void endEntity( final String name ) throws SAXException {
    // do nothing
  }

  /**
   * Report the start of a CDATA section. <p/> <p>This method is empty.</p>
   *
   * @throws SAXException The application may raise an exception.
   * @see #endCDATA()
   */
  public void startCDATA() throws SAXException {
    // do nothing
  }

  /**
   * Report the end of a CDATA section. <p/> <p>This method is empty.</p>
   *
   * @throws SAXException The application may raise an exception.
   * @see #startCDATA()
   */
  public void endCDATA() throws SAXException {
    // do nothing
  }

  /**
   * Report an XML comment anywhere in the document. <p/> <p>This callback will be used for comments inside or outside
   * the document element, including comments in the external DTD subset (if read).  Comments in the DTD must be
   * properly nested inside start/endDTD and start/endEntity events (if used).</p>
   *
   * @param ch     An array holding the characters in the comment.
   * @param start  The starting position in the array.
   * @param length The number of characters to use from the array.
   * @throws SAXException The application may raise an exception.
   */
  public void comment( final char[] ch, final int start, final int length ) throws SAXException {
    if ( !this.inDTD ) {
      this.comment.add( new String( ch, start, length ) );
    }
  }

  /**
   * Returns all collected comments as string array.
   *
   * @return the array containing all comments.
   */
  public String[] getComments() {
    if ( this.comment.isEmpty() ) {
      return EMPTY_COMMENTS;
    }
    return (String[]) this.comment.toArray( new String[ this.comment.size() ] );
  }

  /**
   * Clears all comments.
   */
  public void clearComments() {
    this.comment.clear();
  }
}
