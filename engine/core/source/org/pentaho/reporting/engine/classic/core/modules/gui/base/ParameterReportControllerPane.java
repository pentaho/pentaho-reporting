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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.gui.base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters.ButtonParameterComponent;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters.DefaultParameterComponentFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters.ParameterComponent;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters.ParameterComponentFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters.ParameterUpdateContext;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.core.parameters.CompoundDataRow;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterValidator;
import org.pentaho.reporting.engine.classic.core.parameters.ValidationMessage;
import org.pentaho.reporting.engine.classic.core.parameters.ValidationResult;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentMetaData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class ParameterReportControllerPane extends JPanel
{
  private boolean isUpdating;

  private static class InternalParameterContext implements ParameterContext
  {
    private DataRow parameterData;
    private DataFactory dataFactory;
    private ResourceBundleFactory resourceBundleFactory;
    private Configuration configuration;
    private ResourceKey contentBase;
    private ResourceManager resourceManager;
    private boolean closed;
    private DocumentMetaData documentMetaData;
    private ReportEnvironment reportEnvironment;

    private InternalParameterContext()
    {
      this.resourceManager = new ResourceManager();

      dataFactory = new TableDataFactory();
      resourceBundleFactory = new DefaultResourceBundleFactory();
      configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
      documentMetaData = new MemoryDocumentMetaData();
      reportEnvironment = new DefaultReportEnvironment(configuration);

      final ReportEnvironmentDataRow envDataRow = new ReportEnvironmentDataRow(reportEnvironment);
      parameterData = new CompoundDataRow(envDataRow, new ParameterDataRow());

    }

    public PerformanceMonitorContext getPerformanceMonitorContext()
    {
      return ClassicEngineBoot.getInstance().getObjectFactory().get(PerformanceMonitorContext.class);
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
      closed = true;
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

      if (closed == false)
      {
        close();
      }
      if (report == null)
      {

        this.resourceManager = new ResourceManager();

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

        dataFactory.initialize(new DesignTimeDataFactoryContext(report));

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
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private UpdateAction()
    {
      putValue(Action.NAME, messages.getString("ParameterReportControllerPane.Update"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      validateParameter();
      performUpdate();
    }
  }

  private class InternalParameterUpdateHandler implements ParameterUpdateContext
  {
    public void setParameterValue(final String name, final Object value)
    {
      setParameterValue(name, value, true);
    }

    public void setParameterValue(final String name, final Object value, final boolean autoUpdate)
    {
      updateParameterValue(name, value, autoUpdate);
    }

    public Object getParameterValue(final String name)
    {
      return reportParameterValues.get(name);
    }

    public void addChangeListener(final ChangeListener changeListener)
    {
      ParameterReportControllerPane.this.addInternalChangeListener(changeListener);
    }

    public void removeChangeListener(final ChangeListener changeListener)
    {
      ParameterReportControllerPane.this.removeInternalChangeListener(changeListener);
    }
  }

  private static class ParameterCarrierPanel extends JPanel implements Scrollable
  {
    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    private ParameterCarrierPanel()
    {
      setLayout(new GridBagLayout());
    }

    public Dimension getPreferredScrollableViewportSize()
    {
      return getPreferredSize();
    }

    public int getScrollableUnitIncrement(final Rectangle visibleRect, final int orientation, final int direction)
    {
      return 10;
    }

    public int getScrollableBlockIncrement(final Rectangle visibleRect, final int orientation, final int direction)
    {
      return 20;
    }

    public boolean getScrollableTracksViewportWidth()
    {
      return true;
    }

    public boolean getScrollableTracksViewportHeight()
    {
      return false;
    }
  }

  public static final Color ERROR_COLOR = new Color(251, 191, 191, 255);

  private MasterReport report;
  private ReportParameterValues reportParameterValues;
  private ReportParameterValidator validator;
  private ArrayList<ChangeListener> changeListeners;
  private ArrayList<ChangeListener> internalChangeListeners;
  private JCheckBox autoUpdateCheckbox;
  private JButton updateButton;
  private ParameterCarrierPanel carrierPanel;

  private InternalParameterContext parameterContext;
  private HashMap<String, JLabel> errorLabels;
  private JLabel globalErrorMessage;
  private ParameterComponentFactory parameterEditorFactory;
  private boolean inUpdate;
  private ParameterUpdateContext updateContext;
  private Messages messages;
  private ArrayList<ParameterComponent> parameterComponents;

  public ParameterReportControllerPane()
  {
    messages = new Messages(Locale.getDefault(), SwingPreviewModule.BUNDLE_NAME,
        ObjectUtilities.getClassLoader(ParameterReportControllerPane.class));
    changeListeners = new ArrayList<ChangeListener>();
    internalChangeListeners = new ArrayList<ChangeListener>();

    parameterComponents = new ArrayList<ParameterComponent>();

    carrierPanel = new ParameterCarrierPanel();
    parameterContext = new InternalParameterContext();
    errorLabels = new HashMap<String, JLabel>();
    globalErrorMessage = new JLabel();
    autoUpdateCheckbox = new JCheckBox(messages.getString("ParameterReportControllerPane.AutoUpdate"));
    updateButton = new JButton(new UpdateAction());

    setLayout(new GridBagLayout());
    parameterEditorFactory = new DefaultParameterComponentFactory();
    updateContext = new InternalParameterUpdateHandler();

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridy = 0;
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    add(globalErrorMessage, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 1;
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.anchor = GridBagConstraints.WEST;
    final JScrollPane scrollPane = new JScrollPane(carrierPanel);
    scrollPane.getViewport().setBackground(carrierPanel.getBackground());
    add(scrollPane, gbc);

    final JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new GridLayout(1, 1));
    buttonPane.add(updateButton);
    gbc = new GridBagConstraints();
    gbc.gridy = 2;
    gbc.gridx = 1;
    gbc.anchor = GridBagConstraints.EAST;
    add(buttonPane, gbc);

    final JPanel cbPane = new JPanel();
    cbPane.setLayout(new GridLayout(1, 1));
    cbPane.add(autoUpdateCheckbox);
    gbc = new GridBagConstraints();
    gbc.gridy = 2;
    gbc.gridx = 0;
    gbc.anchor = GridBagConstraints.WEST;
    add(cbPane, gbc);

  }

  public MasterReport getReport()
  {
    return report;
  }

  public void setReport(final MasterReport report) throws ReportProcessingException
  {
    //   final MasterReport oldReport = this.report;
    this.report = report;
    if (!isUpdating)
    {
      reinit();
    }
  }

  public void hideControls()
  {
    autoUpdateCheckbox.setVisible(false);
    updateButton.setVisible(false);
  }

  private void reinit() throws ReportProcessingException
  {
    internalChangeListeners.clear();
    carrierPanel.removeAll();
    errorLabels.clear();
    globalErrorMessage.setText(null);
    parameterComponents.clear();

    if (report == null)
    {
      this.reportParameterValues = null;
      this.parameterContext.update((MasterReport) null);
      this.validator = null;
      this.autoUpdateCheckbox.setSelected(false);
      return;
    }

    final Object autoUpdate = report.getAttribute
        (AttributeNames.Core.NAMESPACE, AttributeNames.Core.AUTO_SUBMIT_PARAMETER);
    final boolean showAutoSubmitCheckbox;
    final boolean autoSubmitDefault;

    if (autoUpdate == null)
    {
      showAutoSubmitCheckbox = true;
      autoSubmitDefault = Boolean.FALSE.equals(report.getAttribute
          (AttributeNames.Core.NAMESPACE, AttributeNames.Core.AUTO_SUBMIT_DEFAULT)) == false;
    }
    else
    {
      showAutoSubmitCheckbox = false;
      autoSubmitDefault = Boolean.TRUE.equals(autoUpdate);
    }

    this.autoUpdateCheckbox.setVisible(showAutoSubmitCheckbox);
    this.autoUpdateCheckbox.setSelected(autoSubmitDefault);

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
        // first compute the default values ...
        this.reportParameterValues = validationResult.getParameterValues();
      }
      finally
      {
        parameterContext.close();
      }
    }
    catch (ReportDataFactoryException e)
    {
      e.printStackTrace();
      // this may fail if the datasource is not there or the report is really messed up ..
      this.reportParameterValues = new ReportParameterValues(report.getParameterValues());
    }

    parameterComponents = new ArrayList<ParameterComponent>();
    // we are using a very simple model here (for now).
    parameterContext.update(report);
    parameterContext.update(this.reportParameterValues);

    validator = parameterDefinition.getValidator();

    final ParameterDefinitionEntry[] entries = parameterDefinition.getParameterDefinitions();
    for (int i = 0; i < entries.length; i++)
    {
      final ParameterDefinitionEntry entry = entries[i];
      if ("true".equals(entry.getParameterAttribute
          (ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.HIDDEN, parameterContext)))
      {
        continue;
      }
      final ParameterComponent parameterComponent = parameterEditorFactory.create(entry, parameterContext, updateContext);
      addToPanel(entry, 1 + i * 2, parameterComponent.getUIComponent());
      parameterComponents.add(parameterComponent);
    }

    validateParameter();
  }

  private void addToPanel(final ParameterDefinitionEntry entry,
                          final int gridY,
                          final JComponent editor)
  {
    final JLabel label = new JLabel(computeLabel(entry));
    final JLabel errorLabel = new JLabel();
    errorLabels.put(entry.getName(), errorLabel);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridy = gridY;
    gbc.gridx = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 0, 5);
    carrierPanel.add(label, gbc);


    if (entry.isMandatory())
    {
      gbc = new GridBagConstraints();
      gbc.gridy = gridY;
      gbc.gridx = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.insets = new Insets(5, 0, 0, 0);
      final JLabel mandatoryLabel = new JLabel("*");
      mandatoryLabel.setToolTipText(messages.getString("ParameterReportControllerPane.MandatoryParameter"));
      carrierPanel.add(mandatoryLabel, gbc);
    }

    gbc = new GridBagConstraints();
    gbc.gridy = gridY;
    gbc.gridx = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    if (editor instanceof ButtonParameterComponent)
    {
      gbc.fill = GridBagConstraints.HORIZONTAL;
    }
    gbc.ipadx = 100;
    gbc.insets = new Insets(5, 0, 0, 0);
    carrierPanel.add(editor, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = gridY + 1;
    gbc.gridx = 1;
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.weightx = 1;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 0, 0, 0);
    carrierPanel.add(errorLabel, gbc);
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

  protected void addInternalChangeListener(final ChangeListener changeListener)
  {
    if (changeListener == null)
    {
      throw new NullPointerException();
    }
    internalChangeListeners.add(changeListener);
  }

  protected void removeInternalChangeListener(final ChangeListener changeListener)
  {
    if (changeListener == null)
    {
      throw new NullPointerException();
    }
    internalChangeListeners.remove(changeListener);
  }

  protected void performUpdate()
  {
    isUpdating = true;
    try
    {
      report.getParameterValues().clear();
      report.getParameterValues().putAll(reportParameterValues);
      setReport(report);

      final ChangeEvent event = new ChangeEvent(this);
      for (int i = 0; i < changeListeners.size(); i++)
      {
        final ChangeListener listener = changeListeners.get(i);
        listener.stateChanged(event);
      }
    }
    catch (ReportProcessingException e)
    {
      ExceptionDialog.showExceptionDialog(this,
          messages.getString("ParameterReportControllerPane.Error"),
          messages.getString("ParameterReportControllerPane.ErrorWhileConfiguringParameterUI", e.getMessage()), e);
    }
    isUpdating = false;
  }

  protected void updateParameterValue(final String name, final Object value, final boolean autoUpdate)
  {
    reportParameterValues.put(name, value);
    parameterContext.update(reportParameterValues);

    if (inUpdate || autoUpdate == false)
    {
      return;
    }

    if (validateParameter() == false)
    {
      return;
    }

    if (autoUpdateCheckbox.isSelected())
    {
      performUpdate();
    }
  }

  private boolean validateParameter()
  {
    globalErrorMessage.setText(null);
    for (final Map.Entry<String, JLabel> stringJLabelEntry : errorLabels.entrySet())
    {
      final JLabel o = stringJLabelEntry.getValue();
      o.setText(null);
    }

    boolean retval = true;
    if (validator != null)
    {
      try
      {
        final ValidationResult validationResult = validator.validate
            (new ValidationResult(), report.getParameterDefinition(), parameterContext);

        final ValidationMessage[] messages = validationResult.getErrors();
        globalErrorMessage.setText(formatMessages(messages));

        final String[] propertyNames = validationResult.getProperties();
        for (int i = 0; i < propertyNames.length; i++)
        {
          final String propertyName = propertyNames[i];
          final JLabel o = errorLabels.get(propertyName);
          final ValidationMessage[] validationMessages = validationResult.getErrors(propertyName);
          final String message = formatMessages(validationMessages);
          if (o == null)
          {
            final String s = globalErrorMessage.getText();
            if (StringUtils.isEmpty(s))
            {
              globalErrorMessage.setText(propertyName + ": " + message);
            }
            else
            {
              globalErrorMessage.setText(s + "\n" + propertyName + ": " + message);
            }
          }
          else
          {
            o.setText(message);
          }
        }

        // Set the updated and validated parameter values as new values.
        final ReportParameterValues parameterValues = validationResult.getParameterValues();
        for (final String columnName : parameterValues.getColumnNames())
        {
          final Object value = parameterValues.get(columnName);
          if (value != null)
          {
            reportParameterValues.put(columnName, value);
          }
        }

        parameterContext.update(reportParameterValues);

        for (final ParameterComponent component : parameterComponents)
        {
          // reinit the components ..
          component.initialize();
        }
      }
      catch (Exception e)
      {
        // mark the report as invalid or so ..
        ExceptionDialog.showExceptionDialog(this,
            messages.getString("ParameterReportControllerPane.Error"),
            messages.getString("ParameterReportControllerPane.ErrorWhileConfiguringParameterUI", e.getMessage()), e);
        retval = false;
      }
    }

    try
    {
      inUpdate = true;
      final ChangeEvent event = new ChangeEvent(this);
      for (int i = 0; i < internalChangeListeners.size(); i++)
      {
        final ChangeListener changeListener = internalChangeListeners.get(i);
        changeListener.stateChanged(event);
      }
    }
    finally
    {
      inUpdate = false;
    }

    return retval;
  }

  private String formatMessages(final ValidationMessage[] validationMessages)
  {
    final StringBuilder message = new StringBuilder(1000);
    for (int j = 0; j < validationMessages.length; j++)
    {
      if (j != 0)
      {
        message.append('\n');
      }
      final ValidationMessage validationMessage = validationMessages[j];
      message.append(validationMessage.getMessage());
    }
    return message.toString();
  }

  public void setErrorMessage(final String error)
  {
    globalErrorMessage.setText(error);
  }
}
