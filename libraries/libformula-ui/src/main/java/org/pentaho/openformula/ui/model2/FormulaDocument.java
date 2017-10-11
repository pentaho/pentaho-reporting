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

package org.pentaho.openformula.ui.model2;

import org.pentaho.reporting.libraries.base.util.FastStack;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import java.util.ArrayList;
import java.util.HashMap;

public class FormulaDocument implements Document {
  private static class FormulaDocumentEvent implements DocumentEvent {
    private Document document;
    private EventType type;
    private int offset;
    private int length;
    private HashMap<Element, ElementChange> changes;

    private FormulaDocumentEvent( final Document document,
                                  final EventType type,
                                  final int offset, final int length ) {
      this.document = document;
      this.type = type;
      this.offset = offset;
      this.length = length;
    }

    public void addChange( final Element element, final ElementChange change ) {
      if ( changes == null ) {
        changes = new HashMap<Element, ElementChange>();
      }
      changes.put( element, change );
    }

    /**
     * Returns the offset within the document of the start of the change.
     *
     * @return the offset >= 0
     */
    public int getOffset() {
      return offset;
    }

    /**
     * Returns the length of the change.
     *
     * @return the length >= 0
     */
    public int getLength() {
      return length;
    }

    /**
     * Gets the document that sourced the change event.
     *
     * @return the document
     */
    public Document getDocument() {
      return document;
    }

    /**
     * Gets the type of event.
     *
     * @return the type
     */
    public EventType getType() {
      return type;
    }

    /**
     * Gets the change information for the given element. The change information describes what elements were added and
     * removed and the location.  If there were no changes, null is returned.
     * <p/>
     * This method is for observers to discover the structural changes that were made.  This means that only elements
     * that existed prior to the mutation (and still exist after the mutatino) need to have ElementChange records. The
     * changes made available need not be recursive.
     * <p/>
     * For example, if the an element is removed from it's parent, this method should report that the parent changed and
     * provide an ElementChange implementation that describes the change to the parent.  If the child element removed
     * had children, these elements do not need to be reported as removed.
     * <p/>
     * If an child element is insert into a parent element, the parent element should report a change.  If the child
     * element also had elements inserted into it (grandchildren to the parent) these elements need not report change.
     *
     * @param elem the element
     * @return the change information, or null if the element was not modified
     */
    public ElementChange getChange( final Element elem ) {
      if ( changes == null ) {
        return null;
      }
      return changes.get( elem );
    }
  }

  private FormulaRootElement rootElement;
  private EventListenerList listenerList;
  private HashMap properties;
  private boolean needRevalidateStructure;

  public FormulaDocument() {
    this.rootElement = new FormulaRootElement( this );
    this.properties = new HashMap();
    this.listenerList = new EventListenerList();
  }

  /**
   * Returns number of characters of content currently in the document.
   *
   * @return number of characters >= 0
   */
  public int getLength() {
    return rootElement.getEndOffset();
  }

  /**
   * Registers the given observer to begin receiving notifications when changes are made to the document.
   *
   * @param listener the observer to register
   * @see Document#removeDocumentListener
   */
  public void addDocumentListener( final DocumentListener listener ) {
    listenerList.add( DocumentListener.class, listener );
  }

  /**
   * Unregisters the given observer from the notification list so it will no longer receive change updates.
   *
   * @param listener the observer to register
   * @see Document#addDocumentListener
   */
  public void removeDocumentListener( final DocumentListener listener ) {
    listenerList.remove( DocumentListener.class, listener );
  }

  protected void fireInsertEvent( final DocumentEvent event ) {
    final DocumentListener[] listeners = listenerList.getListeners( DocumentListener.class );
    for ( int i = 0; i < listeners.length; i++ ) {
      final DocumentListener documentListener = listeners[ i ];
      documentListener.insertUpdate( event );
    }
  }

  protected void fireRemoveEvent( final DocumentEvent event ) {
    final DocumentListener[] listeners = listenerList.getListeners( DocumentListener.class );
    for ( int i = 0; i < listeners.length; i++ ) {
      final DocumentListener documentListener = listeners[ i ];
      documentListener.removeUpdate( event );
    }
  }

  protected void fireChangeEvent( final DocumentEvent event ) {
    final DocumentListener[] listeners = listenerList.getListeners( DocumentListener.class );
    for ( int i = 0; i < listeners.length; i++ ) {
      final DocumentListener documentListener = listeners[ i ];
      documentListener.changedUpdate( event );
    }
  }

  /**
   * Registers the given observer to begin receiving notifications when undoable edits are made to the document.
   *
   * @param listener the observer to register
   * @see UndoableEditEvent
   */
  public void addUndoableEditListener( final UndoableEditListener listener ) {
    listenerList.add( UndoableEditListener.class, listener );
  }

  /**
   * Unregisters the given observer from the notification list so it will no longer receive updates.
   *
   * @param listener the observer to register
   * @see UndoableEditEvent
   */
  public void removeUndoableEditListener( final UndoableEditListener listener ) {
    listenerList.remove( UndoableEditListener.class, listener );
  }

  /**
   * Gets the properties associated with the document.
   *
   * @param key a non-<code>null</code> property key
   * @return the properties
   * @see #putProperty(Object, Object)
   */
  public Object getProperty( final Object key ) {
    return properties.get( key );
  }

  /**
   * Associates a property with the document.  Two standard property keys provided are: <a
   * href="#StreamDescriptionProperty"> <code>StreamDescriptionProperty</code></a> and <a
   * href="#TitleProperty"><code>TitleProperty</code></a>. Other properties, such as author, may also be defined.
   *
   * @param key   the non-<code>null</code> property key
   * @param value the property value
   * @see #getProperty(Object)
   */
  public void putProperty( final Object key, final Object value ) {
    if ( value == null ) {
      properties.remove( key );
    } else {
      properties.put( key, value );
    }
  }

  /**
   * Returns a position that represents the start of the document.  The position returned can be counted on to track
   * change and stay located at the beginning of the document.
   *
   * @return the position
   */
  public Position getStartPosition() {
    try {
      return new FormulaDocumentPosition( rootElement, 0, true );
    } catch ( BadLocationException e ) {
      throw new IllegalStateException( "Should never happen" );
    }
  }

  /**
   * Returns a position that represents the end of the document.  The position returned can be counted on to track
   * change and stay located at the end of the document.
   *
   * @return the position
   */
  public Position getEndPosition() {
    try {
      return new FormulaDocumentPosition( rootElement, 0, false );
    } catch ( BadLocationException e ) {
      throw new IllegalStateException( "Should never happen" );
    }
  }

  /**
   * This method allows an application to mark a place in a sequence of character content. This mark can then be used to
   * tracks change as insertions and removals are made in the content. The policy is that insertions always occur prior
   * to the current position (the most common case) unless the insertion location is zero, in which case the insertion
   * is forced to a position that follows the original position.
   *
   * @param offs the offset from the start of the document >= 0
   * @return the position
   * @throws BadLocationException if the given position does not represent a valid location in the associated document
   */
  public Position createPosition( final int offs ) throws BadLocationException {
    final int elementIndex = rootElement.getElementIndex( offs );
    final FormulaElement element = (FormulaElement) rootElement.getElement( elementIndex );
    return new FormulaDocumentPosition( element, offs - element.getStartOffset(), true );
  }

  /**
   * Returns all of the root elements that are defined. <p> Typically there will be only one document structure, but the
   * interface supports building an arbitrary number of structural projections over the text data. The document can have
   * multiple root elements to support multiple document structures.  Some examples might be: </p> <ul> <li>Text
   * direction. <li>Lexical token streams. <li>Parse trees. <li>Conversions to formats other than the native format.
   * <li>Modification specifications. <li>Annotations. </ul>
   *
   * @return the root element
   */
  public Element[] getRootElements() {
    return new Element[] { rootElement };
  }

  /**
   * Returns the root element that views should be based upon, unless some other mechanism for assigning views to
   * element structures is provided.
   *
   * @return the root element
   */
  public Element getDefaultRootElement() {
    return rootElement;
  }

  public FormulaRootElement getRootElement() {
    return rootElement;
  }

  /**
   * Allows the model to be safely rendered in the presence of concurrency, if the model supports being updated
   * asynchronously. The given runnable will be executed in a way that allows it to safely read the model with no
   * changes while the runnable is being executed.  The runnable itself may <em>not</em> make any mutations.
   *
   * @param r a <code>Runnable</code> used to render the model
   */
  public synchronized void render( final Runnable r ) {
    r.run();
  }


  /**
   * Removes a portion of the content of the document. This will cause a DocumentEvent of type
   * DocumentEvent.EventType.REMOVE to be sent to the registered DocumentListeners, unless an exception is thrown. The
   * notification will be sent to the listeners by calling the removeUpdate method on the DocumentListeners.
   * <p/>
   * To ensure reasonable behavior in the face of concurrency, the event is dispatched after the mutation has occurred.
   * This means that by the time a notification of removal is dispatched, the document has already been updated and any
   * marks created by <code>createPosition</code> have already changed. For a removal, the end of the removal range is
   * collapsed down to the start of the range, and any marks in the removal range are collapsed down to the start of the
   * range. <p align=center><img src="doc-files/Document-remove.gif" alt="Diagram shows removal of 'quick' from 'The
   * quick brown fox.'">
   * <p/>
   * If the Document structure changed as result of the removal, the details of what Elements were inserted and removed
   * in response to the change will also be contained in the generated DocumentEvent. It is up to the implementation of
   * a Document to decide how the structure should change in response to a remove.
   * <p/>
   * If the Document supports undo/redo, an UndoableEditEvent will also be generated.
   *
   * @param offs the offset from the beginning >= 0
   * @param len  the number of characters to remove >= 0
   * @throws BadLocationException some portion of the removal range was not a valid part of the document.  The location
   *                              in the exception is the first bad position encountered.
   * @see DocumentEvent
   * @see DocumentListener
   * @see UndoableEditEvent
   * @see UndoableEditListener
   */
  public void remove( final int offs, final int len ) throws BadLocationException {
    if ( len == 0 ) {
      return;
    }

    final int endPos = offs + len;
    if ( endPos > getLength() ) {
      throw new BadLocationException( "Document Size invalid", endPos );
    }

    final String orgText = getText( 0, getLength() );
    final StringBuffer str = new StringBuffer( orgText );
    str.delete( offs, offs + len );
    rootElement.clear();

    final FormulaElement[] formulaElements = FormulaParser.parseText( this, str.toString() );
    for ( int i = 0; i < formulaElements.length; i++ ) {
      final FormulaElement element = formulaElements[ i ];
      rootElement.insertElement( i, element );
    }
    rootElement.revalidateStructure();
    fireRemoveEvent( new FormulaDocumentEvent( this, DocumentEvent.EventType.REMOVE, offs, len ) );
  }

  /**
   * Inserts a string of content.  This will cause a DocumentEvent of type DocumentEvent.EventType.INSERT to be sent to
   * the registered DocumentListers, unless an exception is thrown. The DocumentEvent will be delivered by calling the
   * insertUpdate method on the DocumentListener. The offset and length of the generated DocumentEvent will indicate
   * what change was actually made to the Document. <p align=center><img src="doc-files/Document-insert.gif"
   * alt="Diagram shows insertion of 'quick' in 'The quick brown fox'">
   * <p/>
   * If the Document structure changed as result of the insertion, the details of what Elements were inserted and
   * removed in response to the change will also be contained in the generated DocumentEvent.  It is up to the
   * implementation of a Document to decide how the structure should change in response to an insertion.
   * <p/>
   * If the Document supports undo/redo, an UndoableEditEvent will also be generated.
   *
   * @param offset the offset into the document to insert the content >= 0. All positions that track change at or after
   *               the given location will move.
   * @param str    the string to insert
   * @param a      the attributes to associate with the inserted content.  This may be null if there are no attributes.
   * @throws BadLocationException the given insert position is not a valid position within the document
   * @see DocumentEvent
   * @see DocumentListener
   * @see UndoableEditEvent
   * @see UndoableEditListener
   */
  public void insertString( final int offset, final String str, final AttributeSet a ) throws BadLocationException {

    final String orgText = getText( 0, getLength() );
    final StringBuffer str2 = new StringBuffer( orgText );
    str2.insert( offset, str );
    rootElement.clear();

    final FormulaElement[] formulaElements = FormulaParser.parseText( this, str2.toString() );
    for ( int i = 0; i < formulaElements.length; i++ ) {
      final FormulaElement element = formulaElements[ i ];
      rootElement.insertElement( i, element );
    }
    rootElement.revalidateStructure();
    fireInsertEvent( new FormulaDocumentEvent( this, DocumentEvent.EventType.INSERT, offset, str.length() ) );

  }

  /**
   * Fetches the text contained within the given portion of the document.
   *
   * @param offset the offset into the document representing the desired start of the text >= 0
   * @param length the length of the desired string >= 0
   * @return the text, in a String of length >= 0
   * @throws BadLocationException some portion of the given range was not a valid part of the document.  The location in
   *                              the exception is the first bad position encountered.
   */
  public String getText( final int offset, final int length ) throws BadLocationException {
    if ( offset + length > getLength() ) {
      throw new BadLocationException( "Document Size invalid", offset + length );
    }

    if ( rootElement.getElementCount() == 0 ) {
      return "";
    }

    final String s = rootElement.getText();
    return s.substring( offset, offset + length );
  }

  public String getText() {
    return rootElement.getText();
  }


  /**
   * Fetches the text contained within the given portion of the document.
   * <p/>
   * If the partialReturn property on the txt parameter is false, the data returned in the Segment will be the entire
   * length requested and may or may not be a copy depending upon how the data was stored. If the partialReturn property
   * is true, only the amount of text that can be returned without creating a copy is returned.  Using partial returns
   * will give better performance for situations where large parts of the document are being scanned. The following is
   * an example of using the partial return to access the entire document:
   * <p/>
   * <pre><code>
   * <p/>
   * &nbsp; int nleft = doc.getDocumentLength();
   * &nbsp; Segment text = new Segment();
   * &nbsp; int offs = 0;
   * &nbsp; text.setPartialReturn(true);
   * &nbsp; while (nleft > 0) {
   * &nbsp;     doc.getText(offs, nleft, text);
   * &nbsp;     // do someting with text
   * &nbsp;     nleft -= text.count;
   * &nbsp;     offs += text.count;
   * &nbsp; }
   * <p/>
   * </code></pre>
   *
   * @param offset the offset into the document representing the desired start of the text >= 0
   * @param length the length of the desired string >= 0
   * @param txt    the Segment object to return the text in
   * @throws BadLocationException Some portion of the given range was not a valid part of the document.  The location in
   *                              the exception is the first bad position encountered.
   */
  public void getText( final int offset, final int length, final Segment txt ) throws BadLocationException {
    final String text = getText( offset, length );
    txt.array = text.toCharArray();
    txt.offset = 0;
    txt.count = text.length();
  }

  public FunctionInformation getFunctionForPosition( final int offset ) {
    final FormulaFunctionElement fn = getFunction( offset );
    if ( fn == null ) {
      return null;
    }

    final ArrayList<String> params = new ArrayList<String>();
    final ArrayList<Integer> paramsStart = new ArrayList<Integer>();
    final ArrayList<Integer> paramsEnd = new ArrayList<Integer>();
    int parenCount = 0;
    int paramStart = 0;
    int paramEnd = 0;
    int globalStart = -1;
    int globalEnd = -1;
    final int count = rootElement.getElementCount();
    boolean found = false;
    final StringBuffer b = new StringBuffer( rootElement.getEndOffset() - fn.getStartOffset() );
    for ( int i = 0; i < count; i++ ) {
      final FormulaElement node = (FormulaElement) rootElement.getElement( i );
      if ( found == false ) {
        if ( node == fn ) {
          found = true;
        }
        continue;
      }

      if ( node instanceof FormulaOpenParenthesisElement ) {
        if ( parenCount > 0 ) {
          b.append( '(' ); // NON-NLS
        } else {
          globalStart = node.getEndOffset();
          paramStart = node.getEndOffset();
        }
        parenCount += 1;
      } else if ( node instanceof FormulaClosingParenthesisElement ) {
        parenCount -= 1;
        if ( parenCount > 0 ) {
          b.append( ')' ); // NON-NLS
        } else {
          paramEnd = node.getStartOffset();
          globalEnd = node.getEndOffset();
          break;
        }
      } else if ( node instanceof FormulaSemicolonElement ) {
        if ( parenCount == 1 ) {
          paramEnd = node.getStartOffset();
          params.add( b.toString() );

          if ( paramEnd < paramStart ) {
            throw new IllegalStateException();
          }
          paramsStart.add( paramStart );
          paramsEnd.add( paramEnd );
          b.delete( 0, b.length() );
          paramStart = node.getEndOffset();
        } else {
          b.append( ';' );
        }
      } else if ( node != null ) {
        b.append( node.getText() );
      }
    }

    if ( paramEnd < paramStart ) {
      paramEnd = rootElement.getEndOffset();
      globalEnd = rootElement.getEndOffset();
    }

    if ( globalEnd < offset ) {
      return null;
    }

    paramsStart.add( paramStart );
    paramsEnd.add( paramEnd );

    final int[] starts = new int[ paramsStart.size() ];
    final int[] ends = new int[ paramsEnd.size() ];
    for ( int i = 0; i < ends.length; i++ ) {
      final Integer endVal = paramsEnd.get( i );
      ends[ i ] = endVal.intValue();
      final Integer startVal = paramsStart.get( i );
      starts[ i ] = startVal.intValue();
    }

    params.add( b.toString() );
    String functionImage = null;
    try {
      functionImage = getText( fn.getStartOffset(), globalEnd - fn.getStartOffset() );
    } catch ( BadLocationException e ) {
      e.printStackTrace();
    }
    return new FunctionInformation
      ( fn.getNormalizedFunctionName(), fn.getStartOffset(),
        globalStart, globalEnd, functionImage, params.toArray( new String[ params.size() ] ),
        starts, ends );
  }

  private FormulaFunctionElement getFunction( final int offset ) {
    FormulaFunctionElement function = null;
    final FastStack functionsStack = new FastStack();
    final int count = rootElement.getElementCount();
    boolean haveCloseParentheses = false;
    for ( int i = 0; i < count; i++ ) {

      final FormulaElement node = (FormulaElement) rootElement.getElement( i );
      if ( ( node != null ) && ( node.getStartOffset() > offset ) ) {
        if ( function == null ) {
          return null;
        }
        return function;
      }

      if ( haveCloseParentheses ) {
        if ( functionsStack.isEmpty() == false ) {
          functionsStack.pop();
        }
        if ( functionsStack.isEmpty() ) {
          function = null;
        } else {
          function = (FormulaFunctionElement) functionsStack.peek();
        }
        haveCloseParentheses = false;
      }

      if ( node instanceof FormulaFunctionElement ) {
        function = (FormulaFunctionElement) node;
      }
      if ( node instanceof FormulaOpenParenthesisElement ) {
        functionsStack.push( function );
      }
      if ( node instanceof FormulaClosingParenthesisElement ) {
        haveCloseParentheses = true;
      }
    }

    if ( functionsStack.isEmpty() == false ) {
      final FormulaElement lastElement = ( count >= 1 ) ? (FormulaElement) rootElement.getElement( count - 1 ) : null;
      if ( ( lastElement != null ) && ( lastElement.getEndOffset() >= offset ) ) {
        return (FormulaFunctionElement) functionsStack.get( 0 );
      } else {
        return (FormulaFunctionElement) functionsStack.peek();
      }
    }
    return function;
  }

  public void setText( final String text ) {
    rootElement.clear();

    final FormulaElement[] formulaElements = FormulaParser.parseText( this, text );
    for ( int i = 0; i < formulaElements.length; i++ ) {
      final FormulaElement element = formulaElements[ i ];
      rootElement.insertElement( i, element );
    }
    rootElement.revalidateStructure();
    rootElement.revalidateNodePositions();
    needRevalidateStructure = false;
    fireInsertEvent( new FormulaDocumentEvent( this, DocumentEvent.EventType.INSERT, 0, text.length() ) );
  }

  /**
   * Retrieve the element at specified position.  Note, the index is not the cursor index but rather the tokenized
   * element position.  So '=COUNT(1;2;3)' would contain 9 elements starting with element '=' at 0 index upto ')' at
   * index 8.
   *
   * @param index
   * @return FormulaElement specified at index.  If index is invalid then return null.
   */
  public FormulaElement getElementAtPosition( final int index ) {
    return (FormulaElement) rootElement.getElement( index );
  }

  public void revalidateStructure() {
    if ( needRevalidateStructure ) {
      setText( getText() );
    }
  }
}
