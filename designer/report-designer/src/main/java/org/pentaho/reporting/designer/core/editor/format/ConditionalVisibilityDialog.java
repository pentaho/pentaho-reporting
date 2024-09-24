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

package org.pentaho.reporting.designer.core.editor.format;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.util.ExpressionEditorPane;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import java.awt.*;

public class ConditionalVisibilityDialog extends CommonDialog {
  private ExpressionEditorPane editorPane;

  public ConditionalVisibilityDialog()
    throws HeadlessException {
    init();
  }

  public ConditionalVisibilityDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public ConditionalVisibilityDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    setTitle( Messages.getString( "ConditionalVisibilityDialog.HideObject" ) );
    setDefaultCloseOperation( DISPOSE_ON_CLOSE );
    setLayout( new BorderLayout() );

    editorPane = new ExpressionEditorPane();
    setModal( true );

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.ConditionalVisibility";
  }

  protected Component createContentPane() {
    final JPanel floatingPanel = new JPanel();
    floatingPanel.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    gbc.anchor = GridBagConstraints.WEST;
    floatingPanel.add( new JLabel( Messages.getString( "ConditionalVisibilityDialog.Condition" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    floatingPanel.add( editorPane, gbc );
    return floatingPanel;
  }

  public Expression performEdit( final Expression expression ) {
    editorPane.setValue( expression );

    if ( performEdit() == false ) {
      return null;
    }

    return editorPane.getValue();
  }

  public ReportDesignerContext getReportDesignerContext() {
    return editorPane.getReportDesignerContext();
  }

  public void setReportDesignerContext( final ReportDesignerContext reportDesignerContext ) {
    editorPane.setReportDesignerContext( reportDesignerContext );
  }
}
