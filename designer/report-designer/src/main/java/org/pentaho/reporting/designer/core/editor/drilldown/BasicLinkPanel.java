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

package org.pentaho.reporting.designer.core.editor.drilldown;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.util.ExpressionEditorPane;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * A panel to facilitate classic style linking which allows to use both static values and all existing expressions.
 *
 * @author Thomas Morgner.
 */
public class BasicLinkPanel extends JPanel {
  private ExpressionEditorPane targetFormula;
  private JTextField targetField;
  private ExpressionEditorPane titleFormula;
  private ExpressionEditorPane windowFormula;
  private JComboBox windowComboBox;
  private JTextField titleField;

  public BasicLinkPanel() {
    targetField = new JTextField();
    targetFormula = new ExpressionEditorPane();
    windowComboBox = new JComboBox( new String[] { "", "_blank", "_self", "_parent", "_top" } ); // NON-NLS
    windowComboBox.setEditable( true );

    titleField = new JTextField();
    titleFormula = new ExpressionEditorPane();
    windowFormula = new ExpressionEditorPane();

    setLayout( new BorderLayout() );

    final JPanel theMainPanel = new JPanel();
    theMainPanel.setLayout( new GridLayout( 3, 1 ) );
    theMainPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

    final JPanel theUpperMainPanel = new JPanel();
    theUpperMainPanel.setLayout( new BorderLayout() );
    theUpperMainPanel
      .setBorder( BorderFactory.createTitledBorder( Messages.getString( "HyperlinkPropertiesPane.Link" ) ) );
    final JPanel theUpperPanel = new JPanel();
    theUpperPanel.setLayout( new GridLayout( 4, 1 ) );
    theUpperPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    theUpperPanel.add( new JLabel( Messages.getString( "HyperlinkPropertiesPane.Value" ) ) );
    theUpperPanel.add( targetField );
    theUpperPanel.add( new JLabel( Messages.getString( "HyperlinkPropertiesPane.Formula" ) ) );
    theUpperPanel.add( targetFormula );
    theUpperMainPanel.add( theUpperPanel, BorderLayout.CENTER );
    theMainPanel.add( theUpperMainPanel );

    final JPanel theMidMainPanel = new JPanel();
    theMidMainPanel.setLayout( new BorderLayout() );
    theMidMainPanel
      .setBorder( BorderFactory.createTitledBorder( Messages.getString( "HyperlinkPropertiesPane.Title" ) ) );
    final JPanel theMidPanel = new JPanel();
    theMidPanel.setLayout( new GridLayout( 4, 1 ) );
    theMidPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    theMidPanel.add( new JLabel( Messages.getString( "HyperlinkPropertiesPane.Value" ) ) );
    theMidPanel.add( titleField );
    theMidPanel.add( new JLabel( Messages.getString( "HyperlinkPropertiesPane.Formula" ) ) );
    theMidPanel.add( titleFormula );
    theMidMainPanel.add( theMidPanel, BorderLayout.CENTER );
    theMainPanel.add( theMidMainPanel );

    final JPanel theBottomMainPanel = new JPanel();
    theBottomMainPanel.setLayout( new BorderLayout() );
    theBottomMainPanel
      .setBorder( BorderFactory.createTitledBorder( Messages.getString( "HyperlinkPropertiesPane.Window" ) ) );
    final JPanel theBottomPanel = new JPanel();
    theBottomPanel.setLayout( new GridLayout( 4, 1 ) );
    theBottomPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    theBottomPanel.add( new JLabel( Messages.getString( "HyperlinkPropertiesPane.Value" ) ) );
    theBottomPanel.add( windowComboBox );
    theBottomPanel.add( new JLabel( Messages.getString( "HyperlinkPropertiesPane.Formula" ) ) );
    theBottomPanel.add( windowFormula );
    theBottomMainPanel.add( theBottomPanel, BorderLayout.CENTER );
    theMainPanel.add( theBottomMainPanel );

    add( theMainPanel, BorderLayout.NORTH );
  }

  public void commitValues( final ElementStyleSheet styleSheet, final Map<StyleKey, Expression> styleExpressions ) {
    if ( styleExpressions == null ) {
      throw new NullPointerException();
    }

    final Expression formulaValue = targetFormula.getValue();
    if ( formulaValue != null ) {
      styleExpressions.put( ElementStyleKeys.HREF_TARGET, formulaValue );
    } else {
      styleExpressions.remove( ElementStyleKeys.HREF_TARGET );
    }

    final String targetRawValue = targetField.getText();
    if ( targetRawValue.trim().length() > 0 ) {
      styleSheet.setStyleProperty( ElementStyleKeys.HREF_TARGET, targetRawValue );
    } else {
      styleSheet.setStyleProperty( ElementStyleKeys.HREF_TARGET, null );
    }


    final Expression windowFormulaValue = windowFormula.getValue();
    if ( windowFormulaValue != null ) {
      styleExpressions.put( ElementStyleKeys.HREF_WINDOW, windowFormulaValue );
    } else {
      styleExpressions.remove( ElementStyleKeys.HREF_WINDOW );
    }

    final String windowValueRaw = (String) windowComboBox.getSelectedItem();
    if ( StringUtils.isEmpty( windowValueRaw, true ) == false ) {
      styleSheet.setStyleProperty( ElementStyleKeys.HREF_WINDOW, windowValueRaw );
    } else {
      styleSheet.setStyleProperty( ElementStyleKeys.HREF_WINDOW, null );
    }

    final Expression titleFormulaValue = titleFormula.getValue();
    if ( titleFormulaValue != null ) {
      styleExpressions.put( ElementStyleKeys.HREF_TITLE, titleFormulaValue );
    } else {
      styleExpressions.remove( ElementStyleKeys.HREF_TITLE );
    }

    final String titleValueRaw = titleField.getText();
    if ( StringUtils.isEmpty( titleValueRaw, true ) == false ) {
      styleSheet.setStyleProperty( ElementStyleKeys.HREF_TITLE, titleValueRaw );
    } else {
      styleSheet.setStyleProperty( ElementStyleKeys.HREF_TITLE, null );
    }
  }

  public void initializeFromStyle( final ElementStyleSheet styleSheet,
                                   final Map styleExpressions,
                                   final ReportDesignerContext renderContext ) {
    targetFormula.setReportDesignerContext( renderContext );
    windowFormula.setReportDesignerContext( renderContext );
    titleFormula.setReportDesignerContext( renderContext );

    if ( styleExpressions.containsKey( ElementStyleKeys.HREF_TARGET ) ) {
      targetFormula.setValue( (Expression) styleExpressions.get( ElementStyleKeys.HREF_TARGET ) );
      targetField.setText( "" );
    } else {
      targetField.setText( (String) styleSheet.getStyleProperty( ElementStyleKeys.HREF_TARGET ) );
      targetFormula.setValue( null );
    }

    if ( styleExpressions.containsKey( ElementStyleKeys.HREF_WINDOW ) ) {
      windowFormula.setValue( (Expression) styleExpressions.get( ElementStyleKeys.HREF_WINDOW ) );
      windowComboBox.setSelectedIndex( -1 );
    } else {
      windowComboBox.setSelectedItem( styleSheet.getStyleProperty( ElementStyleKeys.HREF_WINDOW ) );
      windowFormula.setValue( null );
    }

    if ( styleExpressions.containsKey( ElementStyleKeys.HREF_TITLE ) ) {
      titleFormula.setValue( (Expression) styleExpressions.get( ElementStyleKeys.HREF_TITLE ) );
      titleField.setText( "" );
    } else {
      titleField.setText( (String) styleSheet.getStyleProperty( ElementStyleKeys.HREF_TITLE ) );
      titleFormula.setValue( null );
    }

  }

  public String getFormulaText() {
    final Expression value = targetFormula.getValue();
    if ( value instanceof FormulaExpression ) {
      final FormulaExpression formulaExpression = (FormulaExpression) value;
      return formulaExpression.getFormula();
    }
    return null;
  }

  public void setFormulaText( final String text ) {
    if ( StringUtils.isEmpty( text, true ) ) {
      return;
    }

    if ( text.equals( getFormulaText() ) ) {
      return;
    }

    final FormulaExpression fe = new FormulaExpression();
    fe.setFormula( text );
    targetFormula.setValue( fe );

  }

  public boolean isEmpty() {
    return targetFormula.getValue() == null && StringUtils.isEmpty( targetField.getText(), true );
  }
}
