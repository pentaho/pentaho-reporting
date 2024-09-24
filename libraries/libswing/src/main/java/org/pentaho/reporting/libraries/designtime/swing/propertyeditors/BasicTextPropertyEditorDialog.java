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

package org.pentaho.reporting.libraries.designtime.swing.propertyeditors;

import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.Messages;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.beans.PropertyEditor;

public class BasicTextPropertyEditorDialog extends CommonDialog {
  protected class DocumentUpdateHandler implements DocumentListener {
    public DocumentUpdateHandler() {
    }

    /**
     * Gives notification that there was an insert into the document.  The range given by the DocumentEvent bounds the
     * freshly inserted region.
     *
     * @param e the document event
     */
    public void insertUpdate( final DocumentEvent e ) {
      if ( propertyEditor != null ) {
        try {
          propertyEditor.setAsText( textArea.getText() );
          getConfirmAction().setEnabled( true );
        } catch ( Exception ex ) {
          // ignore ..
          getConfirmAction().setEnabled( false );
        }
      }

    }

    /**
     * Gives notification that a portion of the document has been removed.  The range is given in terms of what the view
     * last saw (that is, before updating sticky positions).
     *
     * @param e the document event
     */
    public void removeUpdate( final DocumentEvent e ) {
      insertUpdate( e );
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    public void changedUpdate( final DocumentEvent e ) {
      insertUpdate( e );
    }
  }

  private PropertyEditor propertyEditor;
  private JTextArea textArea;
  private Object originalValue;

  public BasicTextPropertyEditorDialog()
    throws HeadlessException {
    init();
  }

  public BasicTextPropertyEditorDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public BasicTextPropertyEditorDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    setModal( true );
    setTitle( Messages.getInstance().getString( "TextAreaPropertyEditorDialog.Title" ) );

    textArea = createTextArea();

    super.init();
  }

  protected String getDialogId() {
    return "LibSwing.TextAreaPropertyEditor";
  }

  protected JTextArea createTextArea() {
    final JTextArea textArea = new JTextArea();
    textArea.setColumns( 60 );
    textArea.setRows( 20 );
    textArea.getDocument().addDocumentListener( new DocumentUpdateHandler() );
    return textArea;
  }

  protected JTextArea getTextArea() {
    return textArea;
  }

  protected Component createContentPane() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( new JScrollPane( textArea ), BorderLayout.CENTER );
    return contentPane;
  }

  public boolean performEdit( final PropertyEditor editor ) {
    if ( editor == null ) {
      throw new NullPointerException();
    }
    this.propertyEditor = editor;
    this.originalValue = propertyEditor.getValue();
    if ( originalValue == null ) {
      this.textArea.setText( "" );
    } else {
      this.textArea.setText( propertyEditor.getAsText() );
    }

    if ( performEdit() ) {
      try {
        propertyEditor.setAsText( textArea.getText() );
      } catch ( Exception ex ) {
        // ignore ..
      }
      return true;
    } else {
      try {
        propertyEditor.setValue( originalValue );
      } catch ( Exception ex ) {
        // ignore ..
      }
      return false;
    }
  }

  public String performEdit( final String originalValue ) {
    this.originalValue = originalValue;
    if ( originalValue == null ) {
      this.textArea.setText( "" );
    } else {
      this.textArea.setText( originalValue );
    }
    if ( performEdit() ) {
      return textArea.getText();
    } else {
      return originalValue;
    }
  }
}
