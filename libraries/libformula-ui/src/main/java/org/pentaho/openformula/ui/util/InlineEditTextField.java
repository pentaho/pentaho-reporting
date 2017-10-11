/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.openformula.ui.util;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;

public class InlineEditTextField extends JTextField {
  private class DocProxy implements Document {
    private Document document;
    private boolean suspendEvents;

    private DocProxy( final Document document ) {
      this.document = document;
    }

    public int getLength() {
      return document.getLength();
    }

    public void addDocumentListener( final DocumentListener listener ) {
      document.addDocumentListener( listener );
    }

    public void removeDocumentListener( final DocumentListener listener ) {
      document.removeDocumentListener( listener );
    }

    public void addUndoableEditListener( final UndoableEditListener listener ) {
      document.addUndoableEditListener( listener );
    }

    public void removeUndoableEditListener( final UndoableEditListener listener ) {
      document.removeUndoableEditListener( listener );
    }

    public Object getProperty( final Object key ) {
      return document.getProperty( key );
    }

    public void putProperty( final Object key, final Object value ) {
      document.putProperty( key, value );
    }

    public void remove( final int offs, final int len ) throws BadLocationException {
      String old = InlineEditTextField.this.getText();
      document.remove( offs, len );
      if ( suspendEvents == false ) {
        firePropertyChange( "text", old, InlineEditTextField.this.getText() );
      }
    }

    public void insertString( final int offset, final String str, final AttributeSet a ) throws BadLocationException {
      String old = InlineEditTextField.this.getText();
      document.insertString( offset, str, a );
      if ( suspendEvents == false ) {
        firePropertyChange( "text", old, InlineEditTextField.this.getText() );
      }
    }

    public String getText( final int offset, final int length ) throws BadLocationException {
      return document.getText( offset, length );
    }

    public void getText( final int offset, final int length, final Segment txt ) throws BadLocationException {
      document.getText( offset, length, txt );
    }

    public Position getStartPosition() {
      return document.getStartPosition();
    }

    public Position getEndPosition() {
      return document.getEndPosition();
    }

    public Position createPosition( final int offs ) throws BadLocationException {
      return document.createPosition( offs );
    }

    public Element[] getRootElements() {
      return document.getRootElements();
    }

    public Element getDefaultRootElement() {
      return document.getDefaultRootElement();
    }

    public void render( final Runnable r ) {
      document.render( r );
    }
  }

  private final DocProxy doc;
  private final boolean documentLocked;

  public InlineEditTextField() {
    super();
    doc = new DocProxy( getDocument() );
    setDocument( doc );
    documentLocked = true;
  }

  public void setDocument( final Document doc ) {
    if ( documentLocked ) {
      throw new IllegalStateException();
    }
    super.setDocument( doc );
  }


  public void setText( final String t ) {
    final String old = getText();
    try {
      doc.suspendEvents = true;
      super.setText( t );
    } finally {
      firePropertyChange( "text", old, getText() );
      doc.suspendEvents = false;
    }
  }
}
