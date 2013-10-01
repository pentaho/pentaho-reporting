/*
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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.openformula.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.pentaho.openformula.ui.model2.FormulaElement;
import org.pentaho.openformula.ui.model2.FormulaTextElement;
import org.pentaho.openformula.ui.model2.FunctionInformation;
import org.pentaho.openformula.ui.util.SelectFieldAction;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.formula.function.FunctionDescription;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

public class DefaultFunctionParameterEditor extends JPanel implements FunctionParameterEditor, FieldDefinitionSource
{
  private class FieldSelectorUpdateHandler implements PropertyChangeListener
  {
    private int paramIndex;

    private FieldSelectorUpdateHandler(final int paramIndex)
    {
      this.paramIndex = paramIndex;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt)
    {
      final FieldDefinition value = (FieldDefinition) evt.getNewValue();
      //noinspection MagicCharacter,StringConcatenation
      if (value != null)
      {
        final String text = FormulaUtil.quoteReference(value.getName());
        final String parameterValue = getParameterValue(paramIndex);
        final JTextField field = getParameterField(paramIndex);

        final StringBuilder b = new StringBuilder(parameterValue);
        // remove the selected content, if any
        b.delete(field.getSelectionStart(), field.getSelectionEnd());
        // then insert the new content at the cursor position
        final int caretPosition = field.getCaretPosition();
        b.insert(caretPosition, text);
        field.setText(b.toString());
        field.setCaretPosition(caretPosition + text.length());
      }
    }
  }

  private class FocusListenerHandler implements FocusListener, Runnable
  {
    private JTextField paramTextField;
    private int parameterIndex;
    private String oldText;
    private Component focusIgnore;

    private FocusListenerHandler(final JTextField paramTextField, final int parameterIndex)
    {
      this.paramTextField = paramTextField;
      this.parameterIndex = parameterIndex;
      this.oldText = this.paramTextField.getText();
    }

    /**
     * @param toIgnoreFocus Focus loss to this component will not be treated as focus loss.
     */
    private void setFocusIgnore(Component toIgnoreFocus) {
      focusIgnore = toIgnoreFocus;
    }

    @Override
    public void focusGained(final FocusEvent e)
    {
      if (inSetupUpdate)
      {
        return;
      }
      parameterUpdatedCount = parameterIndex;
      parameterUpdateInProgress = true;
    }

    @Override
    public void focusLost(final FocusEvent e)
    {
      if (focusIgnore != null && focusIgnore.equals(e.getOppositeComponent())) {
        return;
      }
      if (parameterUpdateInProgress)
      {
        parameterUpdateInProgress = false;
        parameterUpdatedCount = -1;
        SwingUtilities.invokeLater(this);
      }
    }

    @Override
    public void run()
    {
        final String s = paramTextField.getText();
        if (ObjectUtilities.equal(s, oldText))
        {
          return;
        }
        fireParameterUpdate(parameterIndex, s);
        oldText = s;
    }
  }

  public static final int FIELDS_MAX_NUMBER=16;
  public static final int FIELDS_ADD=2;

  private static final JTextField[] EMPTY_FIELDS = new JTextField[0];
  private static final SelectFieldAction[] EMPTY_ACTIONS = new SelectFieldAction[0];
  private static final FieldDefinition[] EMPTY_FIELDDEF = new FieldDefinition[0];
  private static final FocusListenerHandler[] EMPTY_HANDLERS = new FocusListenerHandler[0];

  private FunctionDescription selectedFunction;
  private JPanel parameterPane;
  private FocusListenerHandler[] changeHandlers;
  private JTextField[] textFields;
  private SelectFieldAction[] selectFieldActions;
  private FieldDefinition[] fields;

  private boolean inSetupUpdate;
  private boolean parameterUpdateInProgress;
  private int parameterUpdatedCount=-1;

  /**
   * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
   */
  public DefaultFunctionParameterEditor()
  {
    parameterPane = new JPanel();
    parameterPane.setLayout(new GridBagLayout());

    this.inSetupUpdate = false;

    this.textFields = EMPTY_FIELDS;
    this.selectFieldActions = EMPTY_ACTIONS;
    this.fields = EMPTY_FIELDDEF;
    this.changeHandlers = EMPTY_HANDLERS;

    final JPanel parameterPaneCarrier = new JPanel();
    parameterPaneCarrier.setLayout(new BorderLayout());
    parameterPaneCarrier.add(parameterPane, BorderLayout.NORTH);

    final JScrollPane comp = new JScrollPane(parameterPaneCarrier);
    comp.setBorder(new EmptyBorder(0, 0, 0, 0));
    comp.setViewportBorder(new EmptyBorder(0, 0, 0, 0));

    setLayout(new CardLayout());
    add("2", comp);
    add("1", Box.createRigidArea(new Dimension(650, 250)));
  }

  public FunctionDescription getSelectedFunction()
  {
    return selectedFunction;
  }

  @Override
  public void clearSelectedFunction()
  {
    setSelectedFunction(new FunctionParameterContext());
  }


  /**
   * Determines whether the current context formula is the main one (the first
   * formula following the '=').  So '=COUNT(1;SUM(1;2;3))', COUNT would be
   * the main formula.  If context points to SUM then we return false.
   *
   * @param context
   * @return - true if the context points to the left most outer formula.
   */
  public boolean isMainFormula(final FunctionParameterContext context)
  {
    final FormulaEditorModel editorModel = context.getEditorModel();
    if ((editorModel == null) || (editorModel.getLength() < 1))
    {
      return true;
    }

    final FormulaElement mainFormulaElement = editorModel.getFormulaElementAt(1);
    final FunctionInformation currentFunction = editorModel.getCurrentFunction();
    if ((mainFormulaElement != null) && (currentFunction != null) && (currentFunction.getFunctionOffset() == 1) &&
        (((FormulaTextElement) mainFormulaElement).getText().compareTo(currentFunction.getCanonicalName()) == 0))
    {
      return true;
    }
    else
    {
      return false;
    }
  }


  /**
   * If user is typing in formula text-area, this method updates the appropriate parameter
   * field.  Note that the parameter fields are not always visible so if they are not visible
   * then return false.  Note that when user is typing in formula text-area and they are typing
   * an embedded formula, the parameter fields for that embedded formula don't get displayed.
   * They get displayed if user points cursor over the formula or arrows over the formula -
   * just not when typing.
   *
   * @param context
   * @return
   */
  private boolean updateCurrentParameterField(final FunctionParameterContext context)
  {
    final FunctionDescription selectedFunction = context.getFunction();
    final String[] parameterValues = context.getParameterValues();

    // Iterate over each parameter field looking to find the field associated with
    // the embedded formula.  If we find it, build up the formula in parameter field
    // to reflect what was typed into the formula text-area
    for (int i = 0; i < textFields.length; i++)
    {
      final String parameterValue = textFields[i].getText();
      if ((parameterValue != null) && (parameterValue.startsWith(selectedFunction.getCanonicalName()) == true))
      {
        String updatedFormula = selectedFunction.getCanonicalName() + "(";
        for (int paramIndex = 0; paramIndex < parameterValues.length; paramIndex++)
        {
          if (parameterValues[paramIndex] != null)
          {
            updatedFormula = updatedFormula + parameterValues[paramIndex];
            updatedFormula += ";";
          }
        }

        // Remove the trailing semicolon
        if (updatedFormula.endsWith(";"))
        {
          updatedFormula = updatedFormula.substring(0, updatedFormula.length() - 1);
        }

        if (parameterValue.endsWith(")"))
        {
          updatedFormula += ")";
        }

        textFields[i].setText(updatedFormula);
        textFields[i].setCaretPosition(updatedFormula.length());
        return true;
      }
    }

    // We did not find the corresponding parameter field as it is not being displayed
    return false;
  }

  private void updateParameterFields(final String[] parameterValues)
  {
    if (parameterValues != null && parameterValues.length <= textFields.length)
    {
      for (int i = 0; i < parameterValues.length; i++)
      {
        final String string = parameterValues[i];
        if (textFields[i] != null)
        {
          textFields[i].setText(string);
          if (string != null)
          {
            textFields[i].setCaretPosition(string.length());
          }
        }
      }
    }
  }

  @Override
  public void setSelectedFunction(final FunctionParameterContext context)
  {
    try
    {
      inSetupUpdate = true;

      final FunctionDescription fnDesc = context.getFunction();

      //this is empty function?
      if (fnDesc == null)
      {
        for (int i = 0; i < selectFieldActions.length; i++)
        {
          selectFieldActions[i].dispose();
        }

        this.selectFieldActions = EMPTY_ACTIONS;
        this.textFields = EMPTY_FIELDS;
        this.changeHandlers = EMPTY_HANDLERS;
        invalidate();
        revalidate();
        repaint();
        return;
      }

      this.selectedFunction = fnDesc;

      //currently editing one
      final String[] parameterValues = context.getParameterValues();
      final String[] parameterFieldValues = getParametersValues(context.getFunctionInformation(), context.getFunction());

      //recreate whole text fields
      if (textFields.length!=parameterFieldValues.length || context.isSwitchParameterEditor()){
        parameterPane.removeAll();

        this.selectFieldActions = new SelectFieldAction[parameterFieldValues.length];
        this.textFields = new JTextField[parameterFieldValues.length];
        this.changeHandlers = new FocusListenerHandler[parameterFieldValues.length];

        final int fieldFocus = parameterUpdatedCount<0?0:parameterUpdatedCount;

        for (int i=0;i<parameterFieldValues.length;i++){
            addTextField(parameterFieldValues[i],i, i==fieldFocus?true:false);
        }
      }

      if (isMainFormula(context) == true){
        updateParameterFields(parameterValues);
        //return;
      } else {
        // If we are in an embedded formula, update the main
        // formula's parameter field that is associated with
        // this embedded formula.
        if (updateCurrentParameterField(context) == false)
        {
          // The parameter field is pointing to the embedded
          // formula - update it
          updateParameterFields(parameterValues);
        }
      }

      invalidate();
      revalidate();
      repaint();
    }
    finally
    {
      inSetupUpdate = false;
    }
  }

  private void addTextField(final String parameterValue,
                            final int parameterPosition) {
    addTextField(parameterValue, parameterPosition, false);
  }

  private void addTextField(final String parameterValue,
                            final int parameterPosition,
                            final boolean requestFocus)
  {
    //this value is used to compute field hints.
    final int paramPos = Math.max(0, Math.min(selectedFunction.getParameterCount() - 1, parameterPosition));
    final String displayName = selectedFunction.getParameterDisplayName(paramPos, Locale.getDefault());
    final String description = selectedFunction.getParameterDescription(paramPos, Locale.getDefault());

    final JLabel paramNameLabel = new JLabel(displayName);
    final JTextField paramTextField = new JTextField();

    paramTextField.setText(parameterValue);
    if (parameterValue != null)
    {
      paramTextField.setCaretPosition(parameterValue.length());
    }
    paramTextField.setFont
        (new Font(Font.MONOSPACED, paramTextField.getFont().getStyle(), paramTextField.getFont().getSize()));

    final FocusListenerHandler handler = new FocusListenerHandler(paramTextField, parameterPosition);
    paramTextField.addFocusListener(handler);

    if (requestFocus){
      paramTextField.setFocusable(true);
      paramTextField.requestFocusInWindow();
    }

    final SelectFieldAction action =
        new SelectFieldAction(this, new FieldSelectorUpdateHandler(parameterPosition), this);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = parameterPosition;
    gbc.anchor = GridBagConstraints.WEST;
    this.parameterPane.add(paramNameLabel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = parameterPosition;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    this.parameterPane.add(paramTextField, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = parameterPosition;
    gbc.anchor = GridBagConstraints.WEST;
    Component actionButton = new BorderlessButton(action);
    this.parameterPane.add(actionButton, gbc);

    // treat insert field as parameter edit
    handler.setFocusIgnore(actionButton);
    action.setFocusReturn(paramTextField);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = parameterPosition;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(3, 5, 3, 5);
    final JLabel label = new TooltipLabel(description);
    label.setToolTipText(description);
    this.parameterPane.add(label, gbc);

    this.changeHandlers[parameterPosition] = handler;
    this.textFields[parameterPosition] = paramTextField;
    this.selectFieldActions[parameterPosition] = action;
  }

  private static class TooltipLabel extends JLabel
  {
    /**
     * Creates a <code>JLabel</code> instance with
     * no image and with an empty string for the title.
     * The label is centered vertically
     * in its display area.
     * The label's contents, once set, will be displayed on the leading edge
     * of the label's display area.
     *
     * @param description
     */
    private TooltipLabel(final String description)
    {
      final ImageIcon imageIcon = new ImageIcon(getClass().getResource("images/InfoIcon.png"));
      setIcon(imageIcon);
      setToolTipText(description);
      // ensure that the actions are registered ...
    }
  }

  //returns expected number of fields for formula editor
  int computeFunctionParameterCount(final FunctionInformation info, final FunctionDescription desc){
    if  (!desc.isInfiniteParameterCount()){
      return desc.getParameterCount();
    } else {
      return Math.min(FIELDS_MAX_NUMBER, info.getParameterCount()+FIELDS_ADD);
    }
  }

  public String[] getParametersValues(final FunctionInformation fnInfo, final FunctionDescription fnDesc){
    int paramCount = computeFunctionParameterCount(fnInfo, fnDesc);

    final String[] parameterValues = new String[paramCount];
    final int definedParameterCount = Math.min(fnInfo.getParameterCount(), paramCount);
    for (int i = 0; i < definedParameterCount; i++)
    {
      final String text = fnInfo.getParameterText(i);
      parameterValues[i] = text;
    }

    //if there is more than FIELDS_MAX_NUMBER parameters -
    if (definedParameterCount > 0 &&
        fnInfo.getParameterCount() > paramCount)
    {
      final StringBuilder lastParamEatsAllBuffer = new StringBuilder(100);
      final int lastParamIdx = definedParameterCount - 1;
      for (int i = lastParamIdx; i < fnInfo.getParameterCount(); i++)
      {
        if (i > lastParamIdx)
        {
          lastParamEatsAllBuffer.append(';');
        }
        lastParamEatsAllBuffer.append(fnInfo.getParameterText(i));
      }
      parameterValues[lastParamIdx] = lastParamEatsAllBuffer.toString();
    }

    return parameterValues;
  }

  protected JTextField getParameterField(final int field)
  {
    return textFields[field];
  }

  public String getParameterValue(final int param)
  {
    return textFields[param].getText();
  }

  @Override
  public void addParameterUpdateListener(final ParameterUpdateListener listener)
  {
    if (listenerList.getListenerCount(ParameterUpdateListener.class) == 0)
    {
      listenerList.add(ParameterUpdateListener.class, listener);
    }
  }

  @Override
  public void removeParameterUpdateListener(final ParameterUpdateListener listener)
  {
    listenerList.remove(ParameterUpdateListener.class, listener);
  }

  public boolean isCatchAll(final String text)
  {
    if (text != null)
    {
      FunctionDescription selectedFunction = getSelectedFunction();
      if (text != null && (selectedFunction != null) &&
          text.contains("(") && text.contains(")") && (text.contains(selectedFunction.getCanonicalName())))
      {
        return true;
      }
    }

    return false;
  }

  protected void fireParameterUpdate(final int param, final String text)
  {
    final boolean catchAllParameter = isCatchAll(text);
    final ParameterUpdateListener[] updateListeners = listenerList.getListeners(ParameterUpdateListener.class);
    for (int i = 0; i < updateListeners.length; i++)
    {
      final ParameterUpdateListener listener = updateListeners[i];
      listener.parameterUpdated(new ParameterUpdateEvent(this, param, text, catchAllParameter));
    }
  }

  @Override
  public void setFields(final FieldDefinition[] fields)
  {
    this.fields = fields.clone();
  }

  @Override
  public FieldDefinition[] getFields()
  {
    if (fields == null)
    {
      return new FieldDefinition[0];
    }
    return fields.clone();
  }

  @Override
  public Component getEditorComponent()
  {
    return this;
  }
}
