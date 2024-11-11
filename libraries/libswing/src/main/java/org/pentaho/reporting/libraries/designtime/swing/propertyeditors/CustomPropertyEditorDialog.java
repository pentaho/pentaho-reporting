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


package org.pentaho.reporting.libraries.designtime.swing.propertyeditors;

import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.Messages;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;

public class CustomPropertyEditorDialog extends CommonDialog {
  private class ValidationHandler implements PropertyChangeListener {
    private ValidationHandler() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( validatingView == null ) {
        return;
      }
      getConfirmAction().setEnabled( validatingView.isValidEditorValue() );
    }
  }

  private ValidatingPropertyEditorComponent validatingView;
  private ValidationHandler validationHandler;
  private JPanel contentPane;

  public CustomPropertyEditorDialog()
    throws HeadlessException {
    setModal( true );
    init();
  }

  public CustomPropertyEditorDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public CustomPropertyEditorDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    validationHandler = new ValidationHandler();
    contentPane = new JPanel( new BorderLayout() );

    setDefaultCloseOperation( DISPOSE_ON_CLOSE );
    setTitle( Messages.getInstance().getString( "CustomPropertyEditorDialog.Title" ) );
    setPreferredSize( new Dimension( 500, 550 ) );
    super.init();
  }

  protected String getDialogId() {
    return "LibSwing.CustomPropertyEditor";
  }

  protected Component createContentPane() {
    return contentPane;
  }

  public boolean performEdit( final PropertyEditor editor ) {
    if ( editor == null ) {
      throw new NullPointerException();
    }

    final Object originalValue = editor.getValue();
    final Component view = editor.getCustomEditor();
    if ( view instanceof ValidatingPropertyEditorComponent ) {
      validatingView = (ValidatingPropertyEditorComponent) view;
      validatingView.addPropertyChangeListener( validationHandler );
    } else {
      validatingView = null;
    }

    contentPane.removeAll();
    contentPane.add( new JScrollPane( view ), BorderLayout.CENTER );

    if ( super.performEdit() == false ) {
      try {
        editor.setValue( originalValue );
      } catch ( Exception ex ) {
        // ignore ..
      }
    }
    if ( validatingView != null ) {
      validatingView.removePropertyChangeListener( validationHandler );
    }
    return isConfirmed();
  }
}
