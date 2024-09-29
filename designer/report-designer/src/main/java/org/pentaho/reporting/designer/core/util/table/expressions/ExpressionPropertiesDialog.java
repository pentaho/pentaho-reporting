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


package org.pentaho.reporting.designer.core.util.table.expressions;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.expressions.ExpressionPropertiesEditorPanel;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import java.awt.*;

public class ExpressionPropertiesDialog extends CommonDialog {
  private ExpressionPropertiesEditorPanel expressionEditorPanel;

  /**
   * Creates a non-modal dialog without a title and without a specified <code>Frame</code> owner.  A shared, hidden
   * frame will be set as the owner of the dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see GraphicsEnvironment#isHeadless
   * @see JComponent#getDefaultLocale
   */
  public ExpressionPropertiesDialog()
    throws HeadlessException {
    init();
  }

  /**
   * Creates a non-modal dialog without a title with the specified <code>Frame</code> as its owner.  If
   * <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of the dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner the <code>Frame</code> from which the dialog is displayed
   * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see GraphicsEnvironment#isHeadless
   * @see JComponent#getDefaultLocale
   */
  public ExpressionPropertiesDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  /**
   * Creates a non-modal dialog without a title with the specified <code>Dialog</code> as its owner.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
   * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see GraphicsEnvironment#isHeadless
   * @see JComponent#getDefaultLocale
   */
  public ExpressionPropertiesDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    super.init();
    setTitle( Messages.getString( "ExpressionPropertiesDialog.Title" ) );
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.ExpressionProperties";
  }


  protected Component createContentPane() {
    expressionEditorPanel = new ExpressionPropertiesEditorPanel();
    return expressionEditorPanel;
  }

  public Expression editExpression( final Expression input, final ReportDesignerContext context ) {
    expressionEditorPanel.setReportDesignerContext( context );
    expressionEditorPanel.setData( new Expression[] { input.getInstance() } );
    setConfirmed( false );
    if ( performEdit() ) {
      expressionEditorPanel.stopEditing();
      final Expression[] data = expressionEditorPanel.getData();
      return data[ 0 ];
    } else {
      return input;
    }
  }
}
