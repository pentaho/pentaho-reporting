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

package org.pentaho.reporting.designer.core.util;

import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.openformula.ui.FormulaEditorDialog;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.expressions.ExpressionUtil;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.table.CellEditorUtility;
import org.pentaho.reporting.designer.core.util.table.expressions.ExpressionPropertiesDialog;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ExpressionEditorPane extends JPanel {
  private static final String POPUP_EDITOR = "popupEditor";

  private class ExtendedEditorAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ExtendedEditorAction() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final Window w = LibSwingUtil.getWindowAncestor( ExpressionEditorPane.this );

      final JComboBox expressionEditor = getExpressionEditor();
      final Object selectedItem = expressionEditor.getSelectedItem();
      if ( selectedItem instanceof FormulaExpression ) {
        final FormulaExpression fe = (FormulaExpression) selectedItem;
        final FormulaEditorDialog dialog =
          GUIUtils.createFormulaEditorDialog( getReportDesignerContext(), ExpressionEditorPane.this );

        final String formula = dialog.editFormula( fe.getFormula(), getFields() );
        if ( formula == null ) {
          // cancel pressed ... do nothing ...
          return;
        }

        final FormulaExpression derived = (FormulaExpression) fe.getInstance();
        derived.setFormula( formula );
        expressionEditor.setSelectedItem( derived );
      } else if ( selectedItem instanceof Expression ) {
        final ExpressionPropertiesDialog dialog;
        if ( w instanceof Frame ) {
          dialog = new ExpressionPropertiesDialog( (Frame) w );
        } else if ( w instanceof Dialog ) {
          dialog = new ExpressionPropertiesDialog( (Dialog) w );
        } else {
          dialog = new ExpressionPropertiesDialog();
        }
        final Expression expression = dialog.editExpression( (Expression) selectedItem, reportDesignerContext );
        if ( expression != selectedItem ) {
          expressionEditor.setSelectedItem( expression );
        }
      } else if ( selectedItem instanceof ExpressionMetaData ) {
        try {
          final ExpressionMetaData emd = (ExpressionMetaData) selectedItem;
          final Expression expression = (Expression) emd.getExpressionType().newInstance();
          final ExpressionPropertiesDialog dialog;
          if ( w instanceof Frame ) {
            dialog = new ExpressionPropertiesDialog( (Frame) w );
          } else if ( w instanceof Dialog ) {
            dialog = new ExpressionPropertiesDialog( (Dialog) w );
          } else {
            dialog = new ExpressionPropertiesDialog();
          }

          final Expression resultexpression = dialog.editExpression( expression, reportDesignerContext );
          if ( resultexpression != expression ) {
            expressionEditor.setSelectedItem( resultexpression );
          }
        } catch ( Throwable e1 ) {
          UncaughtExceptionsModel.getInstance().addException( e1 );
        }
      } else {
        // assume that we want to edit a formula ..
        final FormulaEditorDialog dialog =
          GUIUtils.createFormulaEditorDialog( getReportDesignerContext(), ExpressionEditorPane.this );
        final String formula = dialog.editFormula( null, getFields() );
        if ( formula == null ) {
          // cancel pressed ... do nothing ...
          return;
        }

        final FormulaExpression derived = new FormulaExpression();
        derived.setFormula( formula );
        expressionEditor.setSelectedItem( derived );
      }
    }
  }

  private JComboBox expressionEditor;
  private ReportDesignerContext reportDesignerContext;
  private static final FieldDefinition[] EMPTY_FIELDS = new FieldDefinition[ 0 ];

  public ExpressionEditorPane() {
    final JButton ellipsisButton = new JButton( "..." );
    ellipsisButton.setDefaultCapable( false );
    ellipsisButton.setMargin( new Insets( 0, 0, 0, 0 ) );
    ellipsisButton.addActionListener( new ExtendedEditorAction() );

    final DefaultComboBoxModel model =
      new DefaultComboBoxModel( ExpressionUtil.getInstance().getKnownExpressions() );
    model.insertElementAt( null, 0 );
    expressionEditor = new JComboBox( model );
    expressionEditor.setEditable( true );
    expressionEditor.setRenderer( new ExpressionListCellRenderer() );
    expressionEditor.setEditor( new ExpressionComboBoxEditor( true ) );
    expressionEditor.getInputMap().put( UtilMessages.getInstance().getOptionalKeyStroke
      ( "AbstractStringValueCellEditor.Popup.Accelerator" ), POPUP_EDITOR );
    expressionEditor.getActionMap().put( POPUP_EDITOR, new ExtendedEditorAction() );

    setLayout( new BorderLayout() );
    add( expressionEditor, BorderLayout.CENTER );
    add( ellipsisButton, BorderLayout.EAST );

  }

  protected JComboBox getExpressionEditor() {
    return expressionEditor;
  }

  public ReportDocumentContext getRenderContext() {
    if ( reportDesignerContext == null ) {
      return null;
    }
    ReportDesignerDocumentContext activeContext = reportDesignerContext.getActiveContext();
    if ( activeContext instanceof ReportDocumentContext ) {
      return (ReportDocumentContext) activeContext;
    }
    return null;
  }

  public ReportDesignerContext getReportDesignerContext() {
    return reportDesignerContext;
  }

  public void setReportDesignerContext( final ReportDesignerContext reportDesignerContext ) {
    this.reportDesignerContext = reportDesignerContext;
  }

  public FieldDefinition[] getFields() {
    return CellEditorUtility.getFields( getRenderContext(), new String[ 0 ] );
  }

  public void setValue( final Expression expression ) {
    this.expressionEditor.setSelectedItem( expression );
  }

  /**
   * Returns the value contained in the editor.
   *
   * @return the value contained in the editor
   */
  public Expression getValue() {
    final Object o = this.expressionEditor.getSelectedItem();
    if ( o instanceof ExpressionMetaData ) {
      try {
        final ExpressionMetaData emd = (ExpressionMetaData) o;
        return (Expression) emd.getExpressionType().newInstance();
      } catch ( Throwable t ) {
        UncaughtExceptionsModel.getInstance().addException( t );
        return null;
      }

    } else if ( o instanceof Expression ) {
      return (Expression) o;
    } else {
      return null;
    }
  }

}
