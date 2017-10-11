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
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

public class HyperlinkEditorPane extends JPanel {
  private class DrillDownItemListener implements ChangeListener {
    private DrillDownItemListener() {
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    public void stateChanged( final ChangeEvent e ) {
      final DrillDownUiProfile uiProfile = drillDownSelector.getSelectedProfile();
      drillDownEditor.setDrillDownUiProfile( uiProfile );
      if ( uiProfile == null ) {
        drillDownEditor.setEnabled( false );
        basicLinkPanel.setEnabled( true );
        cardLayout.last( cardHolder );
      } else {
        drillDownEditor.setEnabled( true );
        basicLinkPanel.setEnabled( false );
        cardLayout.first( cardHolder );
      }
    }
  }

  private class DrillDownProfileChangeHandler implements PropertyChangeListener {
    private DrillDownProfileChangeHandler() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( DrillDownEditor.DRILL_DOWN_UI_PROFILE_PROPERTY.equals( evt.getPropertyName() ) == false ) {
        return;
      }

      drillDownSelector.setSelectedProfile( drillDownEditor.getDrillDownUiProfile() );

    }
  }

  private DrillDownSelector drillDownSelector;
  private DrillDownEditor drillDownEditor;
  private JPanel cardHolder;
  private BasicLinkPanel basicLinkPanel;
  private CardLayout cardLayout;

  public HyperlinkEditorPane() {
    drillDownSelector = new ComboBoxSelector( true );
    drillDownSelector.addChangeListener( new DrillDownItemListener() );


    drillDownEditor = new DrillDownEditor();
    drillDownEditor.addPropertyChangeListener
      ( DrillDownEditor.DRILL_DOWN_UI_PROFILE_PROPERTY, new DrillDownProfileChangeHandler() );
    basicLinkPanel = new BasicLinkPanel();

    cardLayout = new CardLayout();

    cardHolder = new JPanel();
    cardHolder.setLayout( cardLayout );
    cardHolder.add( "2", drillDownEditor );
    cardHolder.add( "1", basicLinkPanel );

    final JPanel selectorPanel = new JPanel();
    selectorPanel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );
    selectorPanel.setLayout( new BorderLayout() );
    selectorPanel.add( new JLabel( Messages.getString( "HyperlinkEditorPane.Location" ) ), BorderLayout.NORTH );
    selectorPanel.add( drillDownSelector.getComponent(), BorderLayout.WEST );

    setLayout( new BorderLayout() );
    add( selectorPanel, BorderLayout.NORTH );
    add( cardHolder, BorderLayout.CENTER );
  }


  public void initializeFromStyle( final ElementStyleSheet styleSheet,
                                   final Map<StyleKey, Expression> styleExpressions,
                                   final ReportDesignerContext designerContext ) {
    basicLinkPanel.initializeFromStyle( styleSheet, styleExpressions, designerContext );

    final String targetFormula = computeFormula
      ( styleExpressions.get( ElementStyleKeys.HREF_WINDOW ),
        (String) styleSheet.getStyleProperty( ElementStyleKeys.HREF_WINDOW ) );
    final String tooltipFormula = computeFormula
      ( styleExpressions.get( ElementStyleKeys.HREF_TITLE ),
        (String) styleSheet.getStyleProperty( ElementStyleKeys.HREF_TITLE ) );

    final String formula = computeFormula
      ( styleExpressions.get( ElementStyleKeys.HREF_TARGET ) );
    final boolean initializedWithDrillDown =
      drillDownEditor.initialize( designerContext, formula, tooltipFormula, targetFormula, new String[ 0 ] );
    if ( basicLinkPanel.isEmpty() == false && initializedWithDrillDown == false ) {
      drillDownSelector.setSelectedProfile( null );
    }
  }

  private String computeFormula( final Expression expression ) {
    if ( expression instanceof FormulaExpression ) {
      final FormulaExpression formulaExpression = (FormulaExpression) expression;
      return formulaExpression.getFormula();
    }
    if ( expression == null ) {
      return null;
    }
    return null;
  }

  private String computeFormula( final Expression expression, final String staticValue ) {
    if ( expression instanceof FormulaExpression ) {
      final FormulaExpression formulaExpression = (FormulaExpression) expression;
      return formulaExpression.getFormula();
    }
    if ( expression == null &&
      StringUtils.isEmpty( staticValue ) == false ) {
      return '=' + FormulaUtil.quoteString( staticValue );
    }
    return null;
  }


  public void commitValues( final ElementStyleSheet styleSheet, final Map<StyleKey, Expression> styleExpressions ) {
    basicLinkPanel.commitValues( styleSheet, styleExpressions );

    if ( drillDownSelector.getSelectedProfile() == null ) {
      return;
    }

    final String formulaText = drillDownEditor.getDrillDownFormula();
    if ( StringUtils.isEmpty( formulaText ) == false ) {
      final FormulaExpression formulaExpression = new FormulaExpression();
      formulaExpression.setFormula( formulaText );
      styleSheet.setStyleProperty( ElementStyleKeys.HREF_TARGET, null );
      styleExpressions.put( ElementStyleKeys.HREF_TARGET, formulaExpression );
    } else {
      styleExpressions.put( ElementStyleKeys.HREF_TARGET, null );
      styleSheet.setStyleProperty( ElementStyleKeys.HREF_TARGET, null );
    }

    final String targetText = drillDownEditor.getTargetFormula();
    if ( StringUtils.isEmpty( targetText ) == false ) {
      final String staticText = FormulaUtil.extractStaticTextFromFormula( targetText );
      if ( staticText != null ) {
        styleSheet.setStyleProperty( ElementStyleKeys.HREF_WINDOW, staticText );
        styleExpressions.put( ElementStyleKeys.HREF_WINDOW, null );
      } else {
        final FormulaExpression formulaExpression = new FormulaExpression();
        formulaExpression.setFormula( targetText );
        styleSheet.setStyleProperty( ElementStyleKeys.HREF_WINDOW, null );
        styleExpressions.put( ElementStyleKeys.HREF_WINDOW, formulaExpression );
      }
    } else {
      styleSheet.setStyleProperty( ElementStyleKeys.HREF_WINDOW, null );
      styleExpressions.put( ElementStyleKeys.HREF_WINDOW, null );
    }

    final String tooltipText = drillDownEditor.getTooltipFormula();
    if ( StringUtils.isEmpty( tooltipText ) == false ) {
      final String staticText = FormulaUtil.extractStaticTextFromFormula( tooltipText );
      if ( staticText != null ) {
        styleSheet.setStyleProperty( ElementStyleKeys.HREF_TITLE, staticText );
        styleExpressions.put( ElementStyleKeys.HREF_TITLE, null );
      } else {
        final FormulaExpression formulaExpression = new FormulaExpression();
        formulaExpression.setFormula( tooltipText );
        styleSheet.setStyleProperty( ElementStyleKeys.HREF_TITLE, null );
        styleExpressions.put( ElementStyleKeys.HREF_TITLE, formulaExpression );
      }
    } else {
      styleSheet.setStyleProperty( ElementStyleKeys.HREF_TITLE, null );
      styleExpressions.put( ElementStyleKeys.HREF_TITLE, null );
    }
  }
}
