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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.pentaho.openformula.ui.util.SelectFieldAction;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.formula.function.FunctionDescription;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

public class DefaultFunctionParameterEditor extends JPanel implements FunctionParameterEditor
{
  private class FieldSelectorUpdateHandler implements PropertyChangeListener
  {
    private int paramIndex;

    private FieldSelectorUpdateHandler(final int paramIndex)
    {
      this.paramIndex = paramIndex;
    }

    public void propertyChange(final PropertyChangeEvent evt)
    {
      final FieldDefinition value = (FieldDefinition) evt.getNewValue();
      //noinspection MagicCharacter,StringConcatenation
      final String text = FormulaUtil.quoteReference(value.getName());
      setParameterValue(paramIndex, text);
    }
  }

  private class FocusListenerHandler implements FocusListener, Runnable, DocumentListener
  {
    private JTextField paramTextField;
    private int parameterIndex;
    private String oldText;

    private FocusListenerHandler(final JTextField paramTextField, final int parameterIndex)
    {
      this.paramTextField = paramTextField;
      this.parameterIndex = parameterIndex;
      this.oldText = this.paramTextField.getText();
    }

    /**
     * Gives notification that there was an insert into the document.  The
     * range given by the DocumentEvent bounds the freshly inserted region.
     *
     * @param e the document event
     */
    public void insertUpdate(final DocumentEvent e)
    {
      if (inSetupUpdate)
      {
        return;
      }
      SwingUtilities.invokeLater(this);
    }

    /**
     * Gives notification that a portion of the document has been
     * removed.  The range is given in terms of what the view last
     * saw (that is, before updating sticky positions).
     *
     * @param e the document event
     */
    public void removeUpdate(final DocumentEvent e)
    {
      if (inSetupUpdate)
      {
        return;
      }
      SwingUtilities.invokeLater(this);
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    public void changedUpdate(final DocumentEvent e)
    {
      if (inSetupUpdate)
      {
        return;
      }
      SwingUtilities.invokeLater(this);
    }

    public void focusGained(final FocusEvent e)
    {
      if (inSetupUpdate)
      {
        return;
      }
      SwingUtilities.invokeLater(this);
    }

    public void focusLost(final FocusEvent e)
    {
      if (inSetupUpdate)
      {
        return;
      }
      SwingUtilities.invokeLater(this);
    }

    public void run()
    {
      final boolean oldParameterUpdate = inParameterUpdate;
      try
      {
        inParameterUpdate = true;
        if (parameterIndex < getParameterCount())
        {
          final String s = paramTextField.getText();
          final int caretPosition = paramTextField.getCaretPosition();
          if (ObjectUtilities.equal(s, oldText))
          {
            return;
          }
          fireParameterUpdate(parameterIndex, s);
          oldText = s;
          paramTextField.setCaretPosition(caretPosition);
        }
      }
      finally
      {
        inParameterUpdate = oldParameterUpdate;
      }
    }
  }

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
  private int functionStartIndex;
//  private boolean performingUpdate;

  private boolean inParameterUpdate;
  private boolean inSetupUpdate;

  /**
   * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
   */
  public DefaultFunctionParameterEditor()
  {
    parameterPane = new JPanel();
    parameterPane.setLayout(new GridBagLayout());

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

  public void clearSelectedFunction()
  {
    setSelectedFunction(new FunctionParameterContext());
  }

  public void setSelectedFunction(final FunctionParameterContext context)
  {
    if (inParameterUpdate)
    {
      return;
    }

    try
    {
      inSetupUpdate = true;

      final int functionStart = context.getFunctionParameterStartPosition();
      final FunctionDescription selectedFunction = context.getFunction();
      final String[] parameterValues = context.getParameterValues();
      final FunctionDescription old = this.selectedFunction;
      if (this.functionStartIndex == functionStart &&
          FunctionParameterContext.isSameFunctionDescription(old, selectedFunction))
      {
        if (parameterValues.length == textFields.length)
        {
          for (int i = 0; i < parameterValues.length; i++)
          {
            final String string = parameterValues[i];
            textFields[i].setText(string);
          }
          return;
        }
      }

      this.selectedFunction = selectedFunction;
      this.functionStartIndex = functionStart;
      parameterPane.removeAll();

      if (selectedFunction == null)
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

      final int count = computeFunctionParameterCount(selectedFunction);

      this.selectFieldActions = new SelectFieldAction[count];
      this.textFields = new JTextField[count];
      this.changeHandlers = new FocusListenerHandler[count];
      for (int i = 0; i < count; i++)
      {
        addTextField(parameterValues[i], i);
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
                            final int parameterPosition)
  {
    final int paramPos = Math.max(0, Math.min(selectedFunction.getParameterCount() - 1, parameterPosition));
    final String displayName = selectedFunction.getParameterDisplayName(paramPos, Locale.getDefault());
    final String description = selectedFunction.getParameterDescription(paramPos, Locale.getDefault());

    final JLabel paramNameLabel = new JLabel(displayName);
    final JTextField paramTextField = new JTextField();
    paramTextField.setText(parameterValue);
    paramTextField.setFont
        (new Font(Font.MONOSPACED, paramTextField.getFont().getStyle(), paramTextField.getFont().getSize()));

    final FocusListenerHandler handler = new FocusListenerHandler(paramTextField, parameterPosition);
    paramTextField.addFocusListener(handler);
    paramTextField.getDocument().addDocumentListener(handler);
    final SelectFieldAction action = new SelectFieldAction(this, new FieldSelectorUpdateHandler(parameterPosition));
    action.setFields(fields);

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
    this.parameterPane.add(new BorderlessButton(action), gbc);

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

  public static int computeFunctionParameterCount(final FunctionDescription selectedFunction)
  {
    final int count;
    if (selectedFunction.isInfiniteParameterCount())
    {
      count = Math.max(16, selectedFunction.getParameterCount());
    }
    else
    {
      count = selectedFunction.getParameterCount();
    }
    return count;
  }

  public void setParameterValue(final int param, final String value)
  {
    if (inSetupUpdate)
    {
      return;
    }

    try
    {
      inParameterUpdate = true;
//    changeHandlers[param].setIgnore(true);
      textFields[param].setText(value);
//    changeHandlers[param].setIgnore(false);
    }
    finally
    {
      inParameterUpdate = false;
    }
  }

  public String getParameterValue(final int param)
  {
    return textFields[param].getText();
  }

  public void addParameterUpdateListener(final ParameterUpdateListener listener)
  {
    listenerList.add(ParameterUpdateListener.class, listener);
  }

  public void removeParameterUpdateListener(final ParameterUpdateListener listener)
  {
    listenerList.remove(ParameterUpdateListener.class, listener);
  }

  protected void fireParameterUpdate(final int param, final String text)
  {
    final boolean catchAllParameter = (param == getParameterCount() - 1);
    final ParameterUpdateListener[] updateListeners = listenerList.getListeners(ParameterUpdateListener.class);
    for (int i = 0; i < updateListeners.length; i++)
    {
      final ParameterUpdateListener listener = updateListeners[i];
      listener.parameterUpdated(new ParameterUpdateEvent(this, param, text, catchAllParameter));
    }
  }

  public void setFields(final FieldDefinition[] fields)
  {
    this.fields = fields.clone();
  }

  public int getParameterCount()
  {
    return textFields.length;
  }

  public Component getEditorComponent()
  {
    return this;
  }
}
