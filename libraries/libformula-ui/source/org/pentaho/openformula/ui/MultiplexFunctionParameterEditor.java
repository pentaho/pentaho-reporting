package org.pentaho.openformula.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import org.pentaho.reporting.libraries.formula.function.FunctionDescription;

public class MultiplexFunctionParameterEditor implements FunctionParameterEditor
{
  private class ParameterUpdateHandler implements ParameterUpdateListener
  {
    private ParameterUpdateHandler()
    {
    }

    public void parameterUpdated(final ParameterUpdateEvent event)
    {
      final ParameterUpdateListener[] updateListeners = listeners.getListeners(ParameterUpdateListener.class);
      for (int i = 0; i < updateListeners.length; i++)
      {
        final ParameterUpdateListener listener = updateListeners[i];
        listener.parameterUpdated(event);
      }
    }
  }

  private HashMap<String, FunctionParameterEditor> editors;
  private JPanel panel;
  private EventListenerList listeners;
  private FunctionParameterEditor activeEditor;
  private DefaultFunctionParameterEditor defaultEditor;
  private ParameterUpdateHandler parameterUpdateHandler;
  private FieldDefinition[] fieldDefinitions;
  public static final FieldDefinition[] EMPTY_FIELDS = new FieldDefinition[0];

  private FunctionDescription selectedFunction;
  private int functionStartIndex;
  private JPanel rootPanel;

  public MultiplexFunctionParameterEditor()
  {
    parameterUpdateHandler = new ParameterUpdateHandler();
    listeners = new EventListenerList();
    fieldDefinitions = EMPTY_FIELDS;
    panel = new JPanel();
    panel.setLayout(new BorderLayout());

    rootPanel = new JPanel();
    rootPanel.setLayout(new CardLayout());
    rootPanel.add("2", panel);
    rootPanel.add("1", Box.createRigidArea(new Dimension(650, 250)));

    editors = new HashMap<String, FunctionParameterEditor>();
    defaultEditor = new DefaultFunctionParameterEditor();
  }

  public void addParameterUpdateListener(final ParameterUpdateListener parameterUpdateListener)
  {
    listeners.add(ParameterUpdateListener.class, parameterUpdateListener);
  }

  public void removeParameterUpdateListener(final ParameterUpdateListener parameterUpdateListener)
  {
    listeners.remove(ParameterUpdateListener.class, parameterUpdateListener);
  }

  public Component getEditorComponent()
  {
    return rootPanel;
  }

  public void setFields(final FieldDefinition[] fieldDefinitions)
  {
    this.fieldDefinitions = fieldDefinitions.clone();
  }

  public DefaultFunctionParameterEditor getDefaultEditor()
  {
    return defaultEditor;
  }

  public void clearSelectedFunction()
  {
    if (activeEditor != null)
    {
      panel.removeAll();
      activeEditor.clearSelectedFunction();
      activeEditor.removeParameterUpdateListener(parameterUpdateHandler);
      activeEditor.setFields(EMPTY_FIELDS);
      activeEditor = null;
      selectedFunction = null;
      
      rootPanel.invalidate();
      rootPanel.revalidate();
      rootPanel.repaint();
    }
  }

  public void setSelectedFunction(final FunctionParameterContext context)
  {
    final FunctionDescription selectedFunction = context.getFunction();
    final int functionStart = context.getFunctionInformation().getFunctionOffset();

    if (activeEditor != null)
    {
      activeEditor.setSelectedFunction(context);
    }

    // Ensure that the parameter field editor has been initialized. This can
    // happen if user manually types in the whole formula in text-area.
    final boolean switchParameterEditor;
    if (this.selectedFunction == null)
    {
      switchParameterEditor = true;
      context.setSwitchParameterEditor(true);
    }
    else
    {
      switchParameterEditor = context.isSwitchParameterEditor();
    }

    this.selectedFunction = selectedFunction;
    this.functionStartIndex = functionStart;

    final String name = selectedFunction.getCanonicalName();
    if ((activeEditor != null) && (switchParameterEditor == true))
    {
      activeEditor.removeParameterUpdateListener(parameterUpdateHandler);
    }

    activeEditor = getEditor(name);
    if (activeEditor == null)
    {
      activeEditor = defaultEditor;
    }

    if (switchParameterEditor)
    {
      panel.removeAll();
      panel.add(activeEditor.getEditorComponent());

      activeEditor.addParameterUpdateListener(parameterUpdateHandler);
      activeEditor.setFields(fieldDefinitions.clone());
      activeEditor.setSelectedFunction(context);

      rootPanel.invalidate();
      rootPanel.revalidate();
      rootPanel.repaint();
    }
  }

  public void setEditor(final String function, final FunctionParameterEditor editor)
  {
    editors.put(function, editor);
  }

  public FunctionParameterEditor getEditor(final String function)
  {
    return editors.get(function);
  }
}
