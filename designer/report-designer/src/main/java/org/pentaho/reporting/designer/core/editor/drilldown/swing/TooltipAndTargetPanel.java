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

import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.util.FormulaEditorPanel;

/**
 * Panel with "Tooltip" combo box and "Panel" formula editor for drill down forms.
 *
 * @author Aleksandr Kozlov
 */
public class TooltipAndTargetPanel extends JPanel {

  /** Label with "Target" field. */
  private JLabel targetLabel;

  /** Combo box for "Target" value. */
  private JComboBox targetComboBox;

  /** Label with "Tooltip" field. */
  private JLabel tooltipLabel;

  /** Editor for "Target" formula. */
  private FormulaEditorPanel tooltipPanel;

  /**
   * Create default panel with "Tooltip" combo box and "Panel" formula editor for drill down forms.
   */
  public TooltipAndTargetPanel() {
    super();
    createComponents();
    layoutComponents();
  }

  /**
   * Create and initialize inner components.
   */
  private void createComponents() {
    targetLabel = createTargetLabel();
    targetComboBox = createTargetComboBox();
    tooltipLabel = createTooltipLabel();
    tooltipPanel = createTooltipPanel();
  }

  /**
   * Create label with "Target" field.
   *
   * @return created label
   */
  private JLabel createTargetLabel() {
    return new JLabel( Messages.getString( "DrillDownDialog.TargetInput.Label" ) );
  }

  /**
   * Create combo box for "Target" value.
   *
   * @return created combo box
   */
  private JComboBox createTargetComboBox() {
    JComboBox comboBox = new JComboBox();
    comboBox.addItem( "" );
    comboBox.addItem( "=\"_TOP\"" );
    comboBox.addItem( "=\"_BLANK\"" );
    comboBox.addItem( "=\"_PARENT\"" );
    comboBox.setEditable( true );
    return comboBox;
  }

  /**
   * Create label with "Tooltip" field.
   *
   * @return created label
   */
  private JLabel createTooltipLabel() {
    return new JLabel( Messages.getString( "DrillDownDialog.TooltipInput.Label" ) );
  }

  /**
   * Create editor for "Target" formula.
   *
   * @return created editor
   */
  private FormulaEditorPanel createTooltipPanel() {
    return new FormulaEditorPanel();
  }

  /**
   * Layout all inner components.
   */
  public void layoutComponents() {
    final int basicIndent = 0;
    final int verticalGap = 2;
    final int horizontalGap = 4;

    setLayout( new GridLayout( 1, 2 ) );

    JPanel tgPanel = new JPanel();
    JPanel ttPanel = new JPanel();
    add( tgPanel );
    add( ttPanel );

    SpringLayout targetLayout = new SpringLayout();
    tgPanel.setLayout( targetLayout );
    tgPanel.add( targetLabel );
    targetLayout.putConstraint( SpringLayout.NORTH, targetLabel, basicIndent, SpringLayout.NORTH, tgPanel );
    targetLayout.putConstraint( SpringLayout.EAST, targetLabel, -horizontalGap, SpringLayout.EAST, tgPanel );
    targetLayout.putConstraint( SpringLayout.WEST, targetLabel, basicIndent, SpringLayout.WEST, tgPanel );
    tgPanel.add( targetComboBox );
    targetLayout.putConstraint( SpringLayout.NORTH, targetComboBox, verticalGap, SpringLayout.SOUTH, targetLabel );
    targetLayout.putConstraint( SpringLayout.EAST, targetComboBox, -horizontalGap, SpringLayout.EAST, tgPanel );
    targetLayout.putConstraint( SpringLayout.WEST, targetComboBox, basicIndent, SpringLayout.WEST, tgPanel );
    targetLayout.putConstraint( SpringLayout.SOUTH, tgPanel, basicIndent, SpringLayout.SOUTH, targetComboBox ); // Order is important! --Kaa

    SpringLayout tooltipLayout = new SpringLayout();
    ttPanel.setLayout( tooltipLayout );
    ttPanel.add( tooltipLabel );
    tooltipLayout.putConstraint( SpringLayout.NORTH, tooltipLabel, basicIndent, SpringLayout.NORTH, ttPanel );
    tooltipLayout.putConstraint( SpringLayout.EAST, tooltipLabel, basicIndent, SpringLayout.EAST, ttPanel );
    tooltipLayout.putConstraint( SpringLayout.WEST, tooltipLabel, 0, SpringLayout.WEST, ttPanel );
    ttPanel.add( tooltipPanel );
    tooltipLayout.putConstraint( SpringLayout.NORTH, tooltipPanel, verticalGap, SpringLayout.SOUTH, tooltipLabel );
    tooltipLayout.putConstraint( SpringLayout.EAST, tooltipPanel, basicIndent, SpringLayout.EAST, ttPanel );
    tooltipLayout.putConstraint( SpringLayout.WEST, tooltipPanel, 0, SpringLayout.WEST, ttPanel );
    tooltipLayout.putConstraint( SpringLayout.SOUTH, ttPanel, basicIndent, SpringLayout.SOUTH, tooltipPanel ); // Order is important! --Kaa
  }

  /**
   * Get combo box for "Target" value.
   *
   * @return combo box for "Target" value
   */
  public JComboBox getTargetComboBox() {
    return targetComboBox;
  }

  /**
   * Get editor for "Target" formula.
   *
   * @return created editor
   */
  public FormulaEditorPanel getTooltipPanel() {
    return tooltipPanel;
  }

  /**
   * Hide content in the case if DrillDownModel.isLimitedEditor() is true.
   */
  public void hideContent() {
    setLayout( new SpringLayout() );
    removeAll();
  }
}
