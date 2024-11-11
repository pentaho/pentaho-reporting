/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
