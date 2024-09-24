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
