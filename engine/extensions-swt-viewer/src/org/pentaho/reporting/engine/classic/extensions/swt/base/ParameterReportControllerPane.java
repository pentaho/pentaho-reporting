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

package org.pentaho.reporting.engine.classic.extensions.swt.base;

/**
 * =========================================================
 * Pentaho-Reporting-Classic : a free Java reporting library
 * =========================================================
 *
 * Project Info:  http://reporting.pentaho.org/
 *
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * ParameterReportControllerPane.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironment;
import org.pentaho.reporting.engine.classic.core.DefaultResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ReportEnvironmentDataRow;
import org.pentaho.reporting.engine.classic.core.ReportParameterValidationException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.internal.MinimalScrollPane;
import org.pentaho.reporting.engine.classic.core.parameters.CompoundDataRow;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterValues;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterValidator;
import org.pentaho.reporting.engine.classic.core.parameters.ValidationMessage;
import org.pentaho.reporting.engine.classic.core.parameters.ValidationResult;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentMetaData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ParameterReportControllerPane extends JPanel
{

  private class TextComponentEditHandler implements DocumentListener
  {
    private Class type;
    private String keyName;
    private JTextComponent textComponent;
    private Color color;

    private TextComponentEditHandler(final Class type,
                                     final String keyName,
                                     final JTextComponent textComponent)
    {
      this.type = type;
      this.keyName = keyName;
      this.textComponent = textComponent;
      this.color = this.textComponent.getBackground();
    }

    /**
     * Gives notification that there was an insert into the document.  The
     * range given by the DocumentEvent bounds the freshly inserted region.
     *
     * @param e the document event
     */
    public void insertUpdate(final DocumentEvent e)
    {
      convertParameterValue(keyName, textComponent.getText());
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
      convertParameterValue(keyName, textComponent.getText());
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    public void changedUpdate(final DocumentEvent e)
    {
      convertParameterValue(keyName, textComponent.getText());
    }

    private void convertParameterValue(final String key, final String text)
    {
      try
      {
        final Object o = ConverterRegistry.toPropertyValue(text, type);
        ParameterReportControllerPane.this.updateParameterValue(key, text);
        textComponent.setBackground(color);
      }
      catch (BeanException e)
      {
        // ignore, do not update (yet).
        textComponent.setBackground(ERROR_COLOR);
      }
    }
  }


  private class MultiValueListParameterHandler implements ListSelectionListener
  {
    private String key;
    private JList list;

    private MultiValueListParameterHandler(final String key, final JList list)
    {
      this.key = key;
      this.list = list;
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(final ListSelectionEvent e)
    {
      final KeyedComboBoxModel listModel = (KeyedComboBoxModel) list.getModel();
      final int[] indices = list.getSelectedIndices();
      final Object[] keys = new Object[indices.length];
      for (int i = 0; i < keys.length; i++)
      {
        final int index = indices[i];
        keys[i] = listModel.getKeyAt(index);
      }
      updateParameterValue(key, keys);
    }
  }

  private class SingleValueListParameterHandler implements ActionListener
  {
    private String key;
    private JComboBox list;

    private SingleValueListParameterHandler(final String key,
                                            final JComboBox list)
    {
      this.key = key;
      this.list = list;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      final KeyedComboBoxModel listModel = (KeyedComboBoxModel) list.getModel();
      updateParameterValue(key, listModel.getSelectedKey());
    }
  }

  private static class InternalParameterContext implements ParameterContext
  {
    private DataRow parameterData;
    private DataFactory dataFactory;
    private ResourceBundleFactory resourceBundleFactory;
    private Configuration configuration;
    private ResourceKey contentBase;
    private ResourceManager resourceManager;
    private boolean open;
    private DocumentMetaData documentMetaData;
    private ReportEnvironment reportEnvironment;

    private InternalParameterContext()
    {
      this.resourceManager = new ResourceManager();
      this.resourceManager.registerDefaults();
      this.open = true;

      dataFactory = new TableDataFactory();
      resourceBundleFactory = new DefaultResourceBundleFactory();
      configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
      documentMetaData = new MemoryDocumentMetaData();
      reportEnvironment = new DefaultReportEnvironment(configuration);

      final ReportEnvironmentDataRow envDataRow = new ReportEnvironmentDataRow(reportEnvironment);
      parameterData = new CompoundDataRow(envDataRow, new ParameterDataRow());

    }

    /**
     * the document metadata of the report. Can be null, if the report does not have a bundle associated or if
     * this context is not part of a report-processing.
     */
    public DocumentMetaData getDocumentMetaData()
    {
      return documentMetaData;
    }

    public ReportEnvironment getReportEnvironment()
    {
      return reportEnvironment;
    }

    public ResourceKey getContentBase()
    {
      return contentBase;
    }

    public DataRow getParameterData()
    {
      return parameterData;
    }

    public DataFactory getDataFactory()
    {
      return dataFactory;
    }

    public ResourceBundleFactory getResourceBundleFactory()
    {
      return resourceBundleFactory;
    }

    public void close() throws ReportDataFactoryException
    {
      open = false;
      dataFactory.close();
    }

    public Configuration getConfiguration()
    {
      return configuration;
    }

    public ResourceManager getResourceManager()
    {
      return resourceManager;
    }

    public void update(final MasterReport report) throws ReportProcessingException
    {

      if (report == null)
      {
        if (open)
        {
          close();
        }

        this.resourceManager = new ResourceManager();
        this.resourceManager.registerDefaults();

        dataFactory = new TableDataFactory();
        resourceBundleFactory = new DefaultResourceBundleFactory();
        configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
        contentBase = null;
        documentMetaData = new MemoryDocumentMetaData();
        reportEnvironment = new DefaultReportEnvironment(configuration);

        final ReportEnvironmentDataRow envDataRow = new ReportEnvironmentDataRow(reportEnvironment);
        parameterData = new CompoundDataRow(envDataRow, new ParameterDataRow());
      }
      else
      {
        if (open)
        {
          close();
        }

        this.resourceManager = report.getResourceManager();
        this.contentBase = report.getContentBase();
        final Object dataCacheEnabledRaw =
            report.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.DATA_CACHE);
        final boolean dataCacheEnabled = Boolean.FALSE.equals(dataCacheEnabledRaw) == false;
        this.dataFactory = new CachingDataFactory(report.getDataFactory().derive(), dataCacheEnabled);
        this.resourceBundleFactory = MasterReport.computeAndInitResourceBundleFactory
            (report.getResourceBundleFactory(), report.getReportEnvironment());
        this.reportEnvironment = report.getReportEnvironment();
        this.configuration = report.getConfiguration();
        final ReportEnvironmentDataRow envDataRow = new ReportEnvironmentDataRow(reportEnvironment);
        this.parameterData = new CompoundDataRow(envDataRow, new ParameterDataRow(report.getParameterValues()));

        dataFactory.initialize(new DesignTimeDataFactoryContext
            (configuration, report.getResourceManager(), report.getContentBase(), resourceBundleFactory));

        if (report.getBundle() != null)
        {
          documentMetaData = report.getBundle().getMetaData();
        }
        else
        {
          documentMetaData = new MemoryDocumentMetaData();
        }
      }
    }

    public void update(final ReportParameterValues properties)
    {
      final ReportEnvironmentDataRow envDataRow = new ReportEnvironmentDataRow(reportEnvironment);
      this.parameterData = new CompoundDataRow(envDataRow, new ParameterDataRow(properties));
    }
  }

  private class UpdateAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private UpdateAction()
    {
      putValue(Action.NAME, "Update");
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      fireParameterChanged();
    }
  }

  private MasterReport report;
  private ReportParameterValues reportParameterValues;
  private ReportParameterValidator validator;
  private ArrayList changeListeners;
  private InternalParameterContext parameterContext;
  private HashMap errorLabels;
  private JLabel globalErrorMessage;
  private static final Color ERROR_COLOR = new Color(251, 191, 191, 255);

  public ParameterReportControllerPane()
  {
    changeListeners = new ArrayList();
    parameterContext = new InternalParameterContext();
    errorLabels = new HashMap();
    globalErrorMessage = new JLabel();

    setLayout(new GridBagLayout());
  }

  public MasterReport getReport()
  {
    return report;
  }

  public void setReport(final MasterReport report) throws ReportProcessingException
  {
    this.report = report;
    reinit();
  }

  private void reinit() throws ReportProcessingException
  {
    removeAll();
    errorLabels.clear();

    if (report == null)
    {
      this.reportParameterValues = null;
      this.parameterContext.update((MasterReport) null);
      this.validator = null;
      return;
    }

    final ReportParameterDefinition parameterDefinition = report.getParameterDefinition();
    if (parameterDefinition == null)
    {
      this.reportParameterValues = null;
      this.parameterContext.update((MasterReport) null);
      this.validator = null;
      return;
    }

    try
    {
      final ReportParameterDefinition parameters = report.getParameterDefinition();
      final DefaultParameterContext parameterContext = new DefaultParameterContext(report);

      try
      {
        final ReportParameterValidator reportParameterValidator = parameters.getValidator();
        final ValidationResult validationResult =
            reportParameterValidator.validate(new ValidationResult(), parameters, parameterContext);
        if (validationResult.isEmpty() == false)
        {
          throw new ReportParameterValidationException
              ("The parameters provided for this report are not valid.", validationResult);
        }
        this.reportParameterValues = validationResult.getParameterValues();
      }
      finally
      {
        parameterContext.close();
      }
    }
    catch (ReportDataFactoryException e)
    {
      // this may fail if the datasource is not there or the report is really messed up ..
      this.reportParameterValues =
          new ReportParameterValues(report.getParameterValues());
    }

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridy = 0;
    gbc.gridx = 0;
    gbc.anchor = GridBagConstraints.WEST;
    add(globalErrorMessage, gbc);

    try
    {
      // we are using a very simple model here (for now).
      parameterContext.update(report);
      validator = parameterDefinition.getValidator();

      final ParameterDefinitionEntry[] entries = parameterDefinition.getParameterDefinitions();
      for (int i = 0; i < entries.length; i++)
      {
        final ParameterDefinitionEntry entry = entries[i];
        if (entry instanceof ListParameter)
        {
          final ListParameter listParameter = (ListParameter) entry;
          createListParameter(listParameter, 1 + i * 2);
        }
        else
        {
          // just an ordinary parameter entry ..
          createPlainParameter(entry, 1 + i * 2);
        }
      }

      final JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new GridLayout(1, 1));
      buttonPane.add(new JButton(new UpdateAction()));

      gbc = new GridBagConstraints();
      gbc.gridy = entries.length * 2 + 1;
      gbc.gridx = 0;
      gbc.gridwidth = 3;
      gbc.anchor = GridBagConstraints.EAST;
      add(buttonPane, gbc);
    }
    catch (ReportDataFactoryException e)
    {
      this.removeAll();
      // mark report as invalid ..
      gbc = new GridBagConstraints();
      gbc.gridy = 0;
      gbc.gridx = 0;
      gbc.anchor = GridBagConstraints.WEST;
      globalErrorMessage.setText("An error occured while configuring the parameter-pane");
      add(globalErrorMessage, gbc);
    }
  }

  private void createListParameter(final ListParameter entry, final int gridY)
      throws ReportDataFactoryException
  {

    final JComponent editor;
    if (entry.isAllowMultiSelection())
    {
      final JList list = new JList();
      final KeyedComboBoxModel keyedComboBoxModel = createModel(entry);
      list.setModel(keyedComboBoxModel);
      list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      list.setVisibleRowCount(Math.min(keyedComboBoxModel.getSize(), 10));
      final ListSelectionModel selectionModel = list.getSelectionModel();

      final Object value = this.reportParameterValues.get(entry.getName());
      if (value instanceof Object[])
      {
        selectionModel.setValueIsAdjusting(true);
        final List keylist = Arrays.asList((Object[]) value);
        final int size = keyedComboBoxModel.getSize();
        for (int i = 0; i < size; i++)
        {
          final Object key = keyedComboBoxModel.getKeyAt(i);
          if (keylist.contains(key))
          {
            selectionModel.addSelectionInterval(i, i);
          }
        }
        selectionModel.setValueIsAdjusting(false);
      }

      selectionModel.addListSelectionListener(new MultiValueListParameterHandler(entry.getName(), list));
      final JScrollPane scrollPane = new MinimalScrollPane(list);
      scrollPane.getViewport().setMinimumSize(list.getPreferredScrollableViewportSize());
      scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

      editor = scrollPane;
    }
    else
    {
      final JComboBox list = new JComboBox();
      final KeyedComboBoxModel keyedComboBoxModel = createModel(entry);
      list.setModel(keyedComboBoxModel);
      list.addActionListener(new SingleValueListParameterHandler(entry.getName(), list));
      editor = list;

      final Object value = this.reportParameterValues.get(entry.getName());
      if (value != null)
      {
        keyedComboBoxModel.setSelectedKey(value);
      }
    }

    addToPanel(entry, gridY, editor);
  }

  private void addToPanel(final ParameterDefinitionEntry entry, final int gridY, final JComponent editor)
  {
    final JLabel label = new JLabel(computeLabel(entry));
    final JLabel errorLabel = new JLabel();
    errorLabels.put(entry.getName(), errorLabel);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridy = gridY;
    gbc.gridx = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.insets = new Insets(5, 0, 0, 0);
    add(label, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = gridY;
    gbc.gridx = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 0, 0, 0);
    add(editor, gbc);

    if (entry.isMandatory())
    {
      gbc = new GridBagConstraints();
      gbc.gridy = gridY;
      gbc.gridx = 2;
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.insets = new Insets(5, 0, 0, 0);
      add(new JLabel("*"), gbc);
    }

    gbc = new GridBagConstraints();
    gbc.gridy = gridY;
    gbc.gridx = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 0, 0, 0);
    add(errorLabel, gbc);
  }

  private KeyedComboBoxModel createModel(final ListParameter parameter) throws ReportDataFactoryException
  {
    final ParameterValues paramValues = parameter.getValues(parameterContext);
    final int count = paramValues.getRowCount();
    final Object[] keys = new Object[count];
    final Object[] values = new Object[count];
    for (int i = 0; i < count; i++)
    {
      keys[i] = paramValues.getKeyValue(i);
      values[i] = paramValues.getTextValue(i);
    }

    final KeyedComboBoxModel model = new KeyedComboBoxModel();
    model.setData(keys, values);
    return model;
  }

  private void createPlainParameter(final ParameterDefinitionEntry entry, final int gridY)
  {
    final String renderHint = entry.getParameterAttribute
        (ParameterAttributeNames.Swing.NAMESPACE, ParameterAttributeNames.Swing.RENDER_HINT, parameterContext);
    final JComponent editor;
    if ("multi-line".equals(renderHint))
    {
      final JTextArea textArea = new JTextArea();
      textArea.getDocument().addDocumentListener(new TextComponentEditHandler(entry.getValueType(), entry.getName(), textArea));
      textArea.setColumns(60);
      textArea.setRows(10);
      editor = new JScrollPane(textArea);

      final Object value = this.reportParameterValues.get(entry.getName());
      if (value != null)
      {
        try
        {
          textArea.setText(ConverterRegistry.toAttributeValue(value));
        }
        catch (BeanException e)
        {
          // ignore illegal values, set them as plain text.
          textArea.setText(value.toString());
          textArea.setBackground(ERROR_COLOR);
        }
      }

    }
    else
    {
      final JTextField textField = new JTextField();
      textField.setColumns(60);
      textField.getDocument().addDocumentListener(new TextComponentEditHandler(entry.getValueType(), entry.getName(), textField));
      editor = textField;

      final Object value = this.reportParameterValues.get(entry.getName());
      if (value != null)
      {
        try
        {
          textField.setText(ConverterRegistry.toAttributeValue(value));
        }
        catch (BeanException e)
        {
          // ignore illegal values, set them as plain text.
          textField.setText(value.toString());
          textField.setBackground(ERROR_COLOR);
        }
      }
    }

    addToPanel(entry, gridY, editor);
  }

  private String computeLabel(final ParameterDefinitionEntry entry)
  {
    final String swingLabel = entry.getParameterAttribute
        (ParameterAttributeNames.Swing.NAMESPACE, ParameterAttributeNames.Swing.LABEL, parameterContext);
    if (swingLabel != null)
    {
      return swingLabel;
    }

    final String coreLabel = entry.getParameterAttribute
        (ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.LABEL, parameterContext);
    if (coreLabel != null)
    {
      return coreLabel;
    }

    return entry.getName();
  }

  public ReportParameterValues getReportParameterValues()
  {
    return reportParameterValues;
  }

  public void addChangeListener(final ChangeListener changeListener)
  {
    if (changeListener == null)
    {
      throw new NullPointerException();
    }
    changeListeners.add(changeListener);
  }

  public void removeChangeListener(final ChangeListener changeListener)
  {
    if (changeListener == null)
    {
      throw new NullPointerException();
    }
    changeListeners.remove(changeListener);
  }

  protected void fireParameterChanged()
  {
    final ChangeEvent chEvent = new ChangeEvent(this);
    for (int i = 0; i < changeListeners.size(); i++)
    {
      final ChangeListener listener = (ChangeListener) changeListeners.get(i);
      listener.stateChanged(chEvent);
    }
  }

  protected void updateParameterValue(final String name, final Object value)
  {
    if (validator == null)
    {
      return;
    }


    reportParameterValues.put(name, value);
    parameterContext.update(reportParameterValues);
    try
    {
      final ValidationResult validationResult = validator.validate
          (new ValidationResult(), report.getParameterDefinition(), parameterContext);
      final String[] propertyNames = validationResult.getProperties();
      for (int i = 0; i < propertyNames.length; i++)
      {
        final String propertyName = propertyNames[i];
        final JLabel o = (JLabel) errorLabels.get(propertyName);
        final ValidationMessage[] validationMessages = validationResult.getErrors(propertyName);
        final StringBuffer message = new StringBuffer(1000);
        for (int j = 0; j < validationMessages.length; j++)
        {
          if (j != 0)
          {
            message.append('\n');
          }
          final ValidationMessage validationMessage = validationMessages[j];
          message.append(validationMessage.getMessage());
        }
        o.setText(message.toString());
      }
    }
    catch (Exception e)
    {
      // mark the report as invalid or so ..
      globalErrorMessage.setText("Failed to validate the parameters");
    }
  }
}
