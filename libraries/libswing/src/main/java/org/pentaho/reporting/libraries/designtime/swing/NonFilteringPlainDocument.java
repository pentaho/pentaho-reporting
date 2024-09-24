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

package org.pentaho.reporting.libraries.designtime.swing;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.Segment;

/**
 * A plain document that ignores any attempt to set the "filterNewLines" property.
 *
 * @author Thomas Morgner.
 */
public class NonFilteringPlainDocument implements Document {
  private PlainDocument backend;

  public NonFilteringPlainDocument() {
    backend = new PlainDocument();
  }

  public int getLength() {
    return backend.getLength();
  }

  public void addDocumentListener( final DocumentListener listener ) {
    backend.addDocumentListener( listener );
  }

  public void removeDocumentListener( final DocumentListener listener ) {
    backend.removeDocumentListener( listener );
  }

  public void addUndoableEditListener( final UndoableEditListener listener ) {
    backend.addUndoableEditListener( listener );
  }

  public void removeUndoableEditListener( final UndoableEditListener listener ) {
    backend.removeUndoableEditListener( listener );
  }

  public Object getProperty( final Object key ) {
    return backend.getProperty( key );
  }

  public void putProperty( final Object key, final Object value ) {
    if ( "filterNewlines".equals( key ) ) // NON-NLS
    {
      return;
    }
    backend.putProperty( key, value );
  }

  public void remove( final int offs, final int len ) throws BadLocationException {
    backend.remove( offs, len );
  }

  public void insertString( final int offset, final String str, final AttributeSet a ) throws BadLocationException {
    backend.insertString( offset, str, a );
  }

  public String getText( final int offset, final int length ) throws BadLocationException {
    return backend.getText( offset, length );
  }

  public void getText( final int offset, final int length, final Segment txt ) throws BadLocationException {
    backend.getText( offset, length, txt );
  }

  public Position getStartPosition() {
    return backend.getStartPosition();
  }

  public Position getEndPosition() {
    return backend.getEndPosition();
  }

  public Position createPosition( final int offs ) throws BadLocationException {
    return backend.createPosition( offs );
  }

  public Element[] getRootElements() {
    return backend.getRootElements();
  }

  public Element getDefaultRootElement() {
    return backend.getDefaultRootElement();
  }

  public void render( final Runnable r ) {
    backend.render( r );
  }
}
