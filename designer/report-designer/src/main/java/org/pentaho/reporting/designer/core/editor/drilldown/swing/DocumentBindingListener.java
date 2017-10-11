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

package org.pentaho.reporting.designer.core.editor.drilldown.swing;

import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * DocumentListener for JTextField binding implementation.
 *
 * @author Aleksandr Kozlov
 */
public abstract class DocumentBindingListener implements DocumentListener {

  /**
   * {@inheritDoc}
   */
  @Override
  public void insertUpdate( DocumentEvent e ) {
    dataUpdated( e.getDocument() );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeUpdate( DocumentEvent e ) {
    dataUpdated( e.getDocument() );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void changedUpdate( DocumentEvent e ) {
    dataUpdated( e.getDocument() );
  }

  /**
   * Extract text from the document of the JTextField and update connected field.
   *
   * @param document document itself.
   */
  private void dataUpdated( Document document ) {
    try {
      String data = document.getText(
              document.getStartPosition().getOffset(),
              document.getEndPosition().getOffset() - 1 );
      setData( data );
    } catch ( BadLocationException ex ) {
      UncaughtExceptionsModel.getInstance().addException( ex );
    }
  }

  /**
   * Update connected field.
   *
   * @param data data from the document.
   */
  protected abstract void setData( String data );
}
