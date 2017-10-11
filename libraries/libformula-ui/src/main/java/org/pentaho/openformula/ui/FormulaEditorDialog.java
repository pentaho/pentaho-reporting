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

package org.pentaho.openformula.ui;

import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

import javax.swing.*;
import java.awt.*;

public class FormulaEditorDialog extends CommonDialog {
  private FormulaEditorPanel panel;

  public FormulaEditorDialog() {
    init();
  }

  public FormulaEditorDialog( final Frame owner ) {
    super( owner );
    init();
  }

  public FormulaEditorDialog( final Dialog owner ) {
    super( owner );
    init();
  }

  protected void init() {
    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
    setTitle( Messages.getInstance().getString( "FormulaEditorDialog.Title" ) );
    setModal( true );
    setResizable( true );

    panel = new FormulaEditorPanel();

    super.init();
  }

  protected String getDialogId() {
    return "LibFormula.FormulaEditor";
  }

  public JToolBar getOperatorPanel() {
    return panel.getOperatorPanel();
  }

  protected Component createContentPane() {
    return panel;
  }

  public void insertText( final String text ) {
    panel.insertText( text );
  }

  public void setEditor( final String function, final FunctionParameterEditor editor ) {
    panel.setEditor( function, editor );
  }

  public FunctionParameterEditor getEditor( final String function ) {
    return panel.getEditor( function );
  }

  @Deprecated
  public String getFormulaText() {
    return panel.getFormulaText();
  }

  @Deprecated
  public void setFormulaText( final String formulaText ) {
    panel.setFormulaText( formulaText );
  }

  @Deprecated
  public void setFields( final FieldDefinition[] fields ) {
    panel.setFields( fields );
  }

  public FieldDefinition[] getFields() {
    return panel.getFields();
  }

  public String editFormula( final String formula, final FieldDefinition[] fields ) {
    if ( fields == null ) {
      throw new NullPointerException();
    }

    panel.setFields( fields );

    if ( StringUtils.isEmpty( formula, true ) ) {
      panel.setFormulaText( "=" );
    } else {
      panel.setFormulaText( formula );
    }
    if ( !super.performEdit() ) {
      return null;
    }

    final String formulaText = panel.getFormulaText();
    if ( StringUtils.isEmpty( formulaText, true ) || formulaText.trim().equals( "=" ) ) {
      return "";
    }
    return formulaText;
  }

  @Deprecated
  public String editFormulaFragment( final String formula, final FieldDefinition[] fields ) {
    if ( fields == null ) {
      throw new NullPointerException();
    }

    if ( StringUtils.isEmpty( formula, true ) ) {
      panel.setFormulaText( "=" );
    } else {
      panel.setFormulaText( "=" + formula );
    }
    panel.setFields( fields );
    setVisible( true );
    if ( !performEdit() ) {
      return null;
    }

    final String formulaText = panel.getFormulaText();
    if ( StringUtils.isEmpty( formulaText, true ) || formulaText.trim().equals( "=" ) ) {
      return null;
    }
    return FormulaUtil.extractFormula( formulaText );
  }

}
