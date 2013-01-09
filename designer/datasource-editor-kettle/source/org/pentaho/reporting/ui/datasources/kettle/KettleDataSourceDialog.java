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

package org.pentaho.reporting.ui.datasources.kettle;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.HashSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataFactoryEditorSupport;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromFileProducer;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StackableRuntimeException;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.background.CancelEvent;
import org.pentaho.reporting.libraries.designtime.swing.background.DataPreviewDialog;
import org.pentaho.reporting.libraries.designtime.swing.background.PreviewWorker;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.CommonFileChooser;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

/**
 * @author Ezequiel Cuellar
 */
public class KettleDataSourceDialog extends CommonDialog
{

  private class BrowseAction extends AbstractAction implements ListSelectionListener
  {
    private BrowseAction()
    {
      putValue(Action.NAME, Messages.getString("KettleDataSourceDialog.Browse.Name"));
      setEnabled(false);
    }

    public void actionPerformed(final ActionEvent e)
    {
      final FileFilter[] fileFilters = new FileFilter[]{new FilesystemFilter(new String[]{".ktr"},
          Messages.getString("KettleDataSourceDialog.KtrFileDescription") + " (*.ktr)", true)};

      final File reportContextFile = DesignTimeUtil.getContextAsFile(designTimeContext.getReport());

      final CommonFileChooser fileChooser = FileChooserService.getInstance().getFileChooser("kettle");
      final String fileText = fileTextField.getText();
      if (StringUtils.isEmpty(fileText) == false)
      {
        if (reportContextFile != null)
        {
          fileChooser.setSelectedFile(new File(reportContextFile.getParentFile(), fileTextField.getText()));
        }
        else
        {
          fileChooser.setSelectedFile(new File(fileTextField.getText()));
        }
      }
      fileChooser.setFilters(fileFilters);
      if (fileChooser.showDialog(KettleDataSourceDialog.this, JFileChooser.OPEN_DIALOG) == false)
      {
        return;
      }

      final File file = fileChooser.getSelectedFile();
      if (file == null)
      {
        return;
      }

      final String path;
      if (reportContextFile != null)
      {
        path = IOUtils.getInstance().createRelativePath(file.getPath(), reportContextFile.getAbsolutePath());
      }
      else
      {
        path = file.getPath();
      }
      final KettleQueryEntry queryEntry = (KettleQueryEntry) queryNameList.getSelectedValue();
      queryEntry.setFile(path);
      fileTextField.setText(path);
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(final ListSelectionEvent e)
    {
      setEnabled(queryNameList.getSelectedValue() != null);
    }
  }

  private class NameSyncHandler implements DocumentListener
  {
    private NameSyncHandler()
    {
    }

    /**
     * Gives notification that there was an insert into the document.  The
     * range given by the DocumentEvent bounds the freshly inserted region.
     *
     * @param e the document event
     */
    public void insertUpdate(final DocumentEvent e)
    {
      update();
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
      update();
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    public void changedUpdate(final DocumentEvent e)
    {
      update();
    }

    private void update()
    {
      if (inUpdateFromList)
      {
        return;
      }

      final String queryName = nameTextField.getText();
      final KettleQueryEntry selectedQuery = (KettleQueryEntry) queryNameList.getSelectedValue();
      selectedQuery.setName(queryName);
      queryNameList.repaint();
    }
  }


  private class FileSyncHandler implements DocumentListener, Runnable
  {
    private boolean armed;

    private FileSyncHandler()
    {
    }

    /**
     * Gives notification that there was an insert into the document.  The
     * range given by the DocumentEvent bounds the freshly inserted region.
     *
     * @param e the document event
     */
    public void insertUpdate(final DocumentEvent e)
    {
      update();
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
      update();
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    public void changedUpdate(final DocumentEvent e)
    {
      update();
    }

    private void update()
    {
      if (armed)
      {
        return;
      }
      armed = true;
      SwingUtilities.invokeLater(this);
    }

    public void run()
    {
      final String fileName = fileTextField.getText();
      final KettleQueryEntry selectedQuery = (KettleQueryEntry) queryNameList.getSelectedValue();
      selectedQuery.setFile(fileName);

      final AbstractReportDefinition report = designTimeContext.getReport();
      final MasterReport masterReport = DesignTimeUtil.getMasterReport(report);
      final ResourceKey contentBase;
      if (masterReport == null)
      {
        contentBase = null;
      }
      else
      {
        contentBase = masterReport.getContentBase();
      }

      try
      {
        inUpdateFromList = true;
        stepsList.setListData(selectedQuery.getSteps(report.getResourceManager(), contentBase));
        final StepMeta[] data = selectedQuery.getSteps(report.getResourceManager(), contentBase);
        stepsList.setListData(data);
        final String selectedStepName = selectedQuery.getSelectedStep();
        if (selectedStepName != null)
        {
          for (int i = 0; i < data.length; i++)
          {
            final StepMeta stepMeta = data[i];
            if (selectedStepName.equals(stepMeta.getName()))
            {
              stepsList.setSelectedValue(stepMeta, true);
              break;
            }
          }
        }

        stepsList.setEnabled(true);
        editParameterAction.setEnabled(true);
      }
      catch (ReportDataFactoryException rdfe)
      {
        logger.warn("Non-critical failure while executing the query", rdfe);
        stepsList.setEnabled(false);
        editParameterAction.setEnabled(false);
      }
      catch (Exception e1)
      {
        designTimeContext.error(e1);
        stepsList.setEnabled(false);
        editParameterAction.setEnabled(false);
      }
      catch (Throwable t1)
      {
        designTimeContext.error(new StackableRuntimeException("Fatal error", t1));
        stepsList.setEnabled(false);
        editParameterAction.setEnabled(false);
      }
      finally
      {
        inUpdateFromList = false;
        armed = false;
      }
    }
  }

  private class StepsListListener implements ListSelectionListener
  {
    private StepsListListener()
    {
    }

    public void valueChanged(final ListSelectionEvent aEvt)
    {
      final KettleQueryEntry queryEntry = (KettleQueryEntry) queryNameList.getSelectedValue();
      final Object selectedValue = stepsList.getSelectedValue();
      if (selectedValue instanceof StepMeta)
      {
        final StepMeta stepMeta = (StepMeta) selectedValue;
        queryEntry.setSelectedStep(stepMeta.getName());
      }
    }
  }

  private class QueryNameListSelectionListener implements ListSelectionListener
  {
    private QueryNameListSelectionListener()
    {
    }

    public void valueChanged(final ListSelectionEvent e)
    {
      final Object value = queryNameList.getSelectedValue();
      if (value == null)
      {
        nameTextField.setEnabled(false);
        fileTextField.setEnabled(false);
        stepsList.setEnabled(false);
        editParameterAction.setEnabled(false);
        return;
      }

      inUpdateFromList = true;
      nameTextField.setEnabled(true);
      fileTextField.setEnabled(true);

      final AbstractReportDefinition report = designTimeContext.getReport();
      final MasterReport masterReport = DesignTimeUtil.getMasterReport(report);
      final ResourceKey contentBase;
      if (masterReport == null)
      {
        contentBase = null;
      }
      else
      {
        contentBase = masterReport.getContentBase();
      }

      try
      {
        final KettleQueryEntry selectedQuery = (KettleQueryEntry) value;
        fileTextField.setText(selectedQuery.getFile());
        nameTextField.setText(selectedQuery.getName());
        final StepMeta[] data = selectedQuery.getSteps(report.getResourceManager(), contentBase);
        stepsList.setListData(data);
        final String selectedStepName = selectedQuery.getSelectedStep();
        if (selectedStepName != null)
        {
          for (int i = 0; i < data.length; i++)
          {
            final StepMeta stepMeta = data[i];
            if (selectedStepName.equals(stepMeta.getName()))
            {
              stepsList.setSelectedValue(stepMeta, true);
              break;
            }
          }
        }
        stepsList.setEnabled(true);
        editParameterAction.setEnabled(true);
      }
      catch (ReportDataFactoryException rdfe)
      {
        logger.warn("Non-critical failure while executing the query", rdfe);
        stepsList.setEnabled(false);
        editParameterAction.setEnabled(false);
      }
      catch (Exception e1)
      {
        designTimeContext.error(e1);
        stepsList.setEnabled(false);
        editParameterAction.setEnabled(false);
      }
      catch (Throwable t1)
      {
        designTimeContext.error(new StackableRuntimeException("Fatal error", t1));
        stepsList.setEnabled(false);
        editParameterAction.setEnabled(false);
      }
      finally
      {
        inUpdateFromList = false;
      }
    }
  }

  private class AddQueryAction extends AbstractAction
  {
    public AddQueryAction()
    {
      final URL resource = KettleDataSourceDialog.class.getResource
          ("/org/pentaho/reporting/ui/datasources/kettle/resources/Add.png");
      if (resource != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(resource));
      }
      else
      {
        putValue(Action.NAME, Messages.getString("KettleDataSourceDialog.AddQuery.Name"));
      }
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("KettleDataSourceDialog.AddQuery.Description"));
    }

    public void actionPerformed(final ActionEvent e)
    {
      final HashSet<String> names = new HashSet<String>();
      for (int i = 0; i < queryListModel.getSize(); i++)
      {
        final KettleQueryEntry o = (KettleQueryEntry) queryListModel.getElementAt(i);
        names.add(o.getName());
      }

      String queryName = Messages.getString("KettleDataSourceDialog.Query");
      for (int i = 1; i < 1000; ++i)
      {
        final String newQuery = Messages.getString("KettleDataSourceDialog.Query") + " " + i;
        if (names.contains(newQuery) == false)
        {
          queryName = newQuery;
          break;
        }
      }

      final KettleQueryEntry newQuery = new KettleQueryEntry(queryName);
      queryListModel.addElement(newQuery);
      queryNameList.setSelectedValue(newQuery, true);
    }
  }

  private class RemoveQueryAction extends AbstractAction implements ListSelectionListener
  {
    public RemoveQueryAction()
    {
      final URL resource = KettleDataSourceDialog.class.getResource
          ("/org/pentaho/reporting/ui/datasources/kettle/resources/Remove.png");
      if (resource != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(resource));
      }
      else
      {
        putValue(Action.NAME, Messages.getString("KettleDataSourceDialog.RemoveQuery.Name"));
      }
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("KettleDataSourceDialog.RemoveQuery.Description"));
      setEnabled(false);
    }

    public void actionPerformed(final ActionEvent e)
    {
      final Object selectedValue = queryNameList.getSelectedValue();
      if (selectedValue == null)
      {
        return;
      }
      inUpdateFromList = true;
      try
      {
        queryListModel.removeElement(selectedValue);
        nameTextField.setText("");
        fileTextField.setText("");
        stepsList.setListData(new Object[]{});
      }
      finally
      {
        inUpdateFromList = false;
      }
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(final ListSelectionEvent e)
    {
      setEnabled(queryNameList.getSelectedValue() != null);
    }
  }

  private class PreviewAction extends AbstractAction implements ListSelectionListener
  {
    private PreviewAction()
    {
      putValue(Action.NAME, Messages.getString("KettleDataSourceDialog.Preview.Name"));
      setEnabled(false);
    }

    public void actionPerformed(final ActionEvent e)
    {
      final KettleQueryEntry kettleQueryEntry = (KettleQueryEntry) queryNameList.getSelectedValue();
      final KettleTransFromFileProducer fileProducer = kettleQueryEntry.createProducer();
      final KettleDataFactory dataFactory = new KettleDataFactory();
      dataFactory.setQuery(kettleQueryEntry.getName(), fileProducer);

      try
      {
        DataFactoryEditorSupport.configureDataFactoryForPreview(dataFactory, designTimeContext);

        final DataPreviewDialog previewDialog = new DataPreviewDialog(KettleDataSourceDialog.this);

        final KettlePreviewWorker worker = new KettlePreviewWorker(dataFactory, kettleQueryEntry.getName());
        previewDialog.showData(worker);

        final ReportDataFactoryException factoryException = worker.getException();
        if (factoryException != null)
        {
          ExceptionDialog.showExceptionDialog(KettleDataSourceDialog.this,
              Messages.getString("KettleDataSourceDialog.PreviewError.Title"),
              Messages.getString("KettleDataSourceDialog.PreviewError.Message"), factoryException);
        }
      }
      catch (Exception ex)
      {
        ExceptionDialog.showExceptionDialog(KettleDataSourceDialog.this,
            Messages.getString("KettleDataSourceDialog.PreviewError.Title"),
            Messages.getString("KettleDataSourceDialog.PreviewError.Message"), ex);
      }
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(final ListSelectionEvent e)
    {
      setEnabled(stepsList.getSelectedValue() != null);
    }
  }

  private static class KettlePreviewWorker implements PreviewWorker
  {
    private KettleDataFactory dataFactory;
    private TableModel resultTableModel;
    private ReportDataFactoryException exception;
    private String query;

    private KettlePreviewWorker(final KettleDataFactory dataFactory,
                                final String query)
    {
      if (dataFactory == null)
      {
        throw new NullPointerException();
      }
      this.query = query;
      this.dataFactory = dataFactory;
    }

    public ReportDataFactoryException getException()
    {
      return exception;
    }

    public TableModel getResultTableModel()
    {
      return resultTableModel;
    }

    public void close()
    {
    }

    /**
     * Requests that the thread stop processing as soon as possible.
     */
    public void cancelProcessing(final CancelEvent event)
    {
      dataFactory.cancelRunningQuery();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run()
    {
      try
      {
        resultTableModel = dataFactory.queryData(query, new ReportParameterValues());
      }
      catch (ReportDataFactoryException e)
      {
        exception = e;
      }
      finally
      {
        dataFactory.close();
      }
    }
  }

  private class EditParameterAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private EditParameterAction()
    {
      putValue(Action.NAME, Messages.getString("KettleDataSourceDialog.EditParameter.Name"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      final KettleQueryEntry queryEntry = (KettleQueryEntry) queryNameList.getSelectedValue();
      if (queryEntry == null)
      {
        return;
      }

      final AbstractReportDefinition report = designTimeContext.getReport();
      final MasterReport masterReport = DesignTimeUtil.getMasterReport(report);
      final ResourceKey contentBase;
      if (masterReport == null)
      {
        contentBase = null;
      }
      else
      {
        contentBase = masterReport.getContentBase();
      }

      try
      {
        final ParameterEditorDialog dialog = new ParameterEditorDialog(KettleDataSourceDialog.this);
        final String[] reportFields = designTimeContext.getDataSchemaModel().getColumnNames();
        final ParameterEditorDialog.EditResult editResult = dialog.performEdit
            (queryEntry.getArguments(), queryEntry.getParameters(), reportFields,
                queryEntry.getDeclaredParameters(report.getResourceManager(), contentBase));
        if (editResult == null)
        {
          return;
        }

        queryEntry.setArguments(editResult.getArgumentNames());
        queryEntry.setParameters(editResult.getParameterMappings());
      }
      catch (Exception e1)
      {
        designTimeContext.error(e1);
      }
      catch (Throwable t1)
      {
        designTimeContext.error(new StackableRuntimeException("Fatal error", t1));
      }
    }
  }

  private static final Log logger = LogFactory.getLog(KettleDataSourceDialog.class);

  private DesignTimeContext designTimeContext;
  private JTextField fileTextField;
  private JTextField nameTextField;
  private JList stepsList;
  private JList queryNameList;
  private DefaultListModel queryListModel;
  private boolean inUpdateFromList;
  private Action editParameterAction;

  public KettleDataSourceDialog(final DesignTimeContext designTimeContext, final JDialog parent)
  {
    super(parent);
    initDialog(designTimeContext);
  }

  public KettleDataSourceDialog(final DesignTimeContext designTimeContext, final JFrame parent)
  {
    super(parent);
    initDialog(designTimeContext);

  }

  public KettleDataSourceDialog(final DesignTimeContext designTimeContext)
  {
    initDialog(designTimeContext);
  }

  private void initDialog(final DesignTimeContext designTimeContext)
  {
    if (designTimeContext == null)
    {
      throw new NullPointerException();
    }

    this.designTimeContext = designTimeContext;

    editParameterAction = new EditParameterAction();
    editParameterAction.setEnabled(false);

    queryListModel = new DefaultListModel();

    queryNameList = new JList(queryListModel);
    queryNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    queryNameList.setVisibleRowCount(5);
    queryNameList.addListSelectionListener(new QueryNameListSelectionListener());

    fileTextField = new JTextField(30);
    fileTextField.setEnabled(false);
    fileTextField.getDocument().addDocumentListener(new FileSyncHandler());

    stepsList = new JList();
    stepsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    stepsList.addListSelectionListener(new StepsListListener());

    nameTextField = new JTextField(30);
    nameTextField.setEnabled(false);
    nameTextField.getDocument().addDocumentListener(new NameSyncHandler());


    setTitle(Messages.getString("KettleDataSourceDialog.Title"));
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setModal(true);

    super.init();
  }

  protected String getDialogId()
  {
    return "KettleDataSourceDialog";
  }

  protected Component createContentPane()
  {
    final PreviewAction previewAction = new PreviewAction();
    stepsList.addListSelectionListener(previewAction);

    final BrowseAction browseAction = new BrowseAction();
    queryNameList.addListSelectionListener(browseAction);

    final RemoveQueryAction removeQueryAction = new RemoveQueryAction();
    queryNameList.addListSelectionListener(removeQueryAction);

    final JPanel previewAndParameterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    previewAndParameterPanel.add(new JButton(editParameterAction));
    previewAndParameterPanel.add(new JButton(previewAction));

    final JPanel queryListButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    queryListButtonsPanel.add(new BorderlessButton(new AddQueryAction()));
    queryListButtonsPanel.add(new BorderlessButton(removeQueryAction));

    final JPanel queryListPanel = new JPanel(new BorderLayout());
    queryListPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
    queryListPanel.add(new JScrollPane(queryNameList), BorderLayout.CENTER);
    queryListPanel.add(queryListButtonsPanel, BorderLayout.NORTH);

    final JPanel mainPanel = new JPanel(new GridBagLayout());
    mainPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    mainPanel.add(new JLabel(Messages.getString("KettleDataSourceDialog.QueryName")), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    mainPanel.add(nameTextField, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 0, 0, 13);
    mainPanel.add(new JLabel(Messages.getString("KettleDataSourceDialog.FileName")), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    mainPanel.add(fileTextField, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    mainPanel.add(new JButton(browseAction), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 0, 5, 0);
    mainPanel.add(new JLabel(Messages.getString("KettleDataSourceDialog.Steps")), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.anchor = GridBagConstraints.WEST;
    mainPanel.add(new JScrollPane(stepsList), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.WEST;
    mainPanel.add(previewAndParameterPanel, gbc);


    final JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(mainPanel, BorderLayout.EAST);
    panel.add(queryListPanel, BorderLayout.CENTER);
    return panel;
  }

  public KettleDataFactory performConfiguration(final KettleDataFactory dataFactory,
                                                final String queryName)
  {
    queryListModel.clear();

    loadData(dataFactory, queryName);
    if (performEdit() == false)
    {
      return null;
    }

    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    for (int i = 0; i < queryListModel.getSize(); i++)
    {
      final KettleQueryEntry queryEntry = (KettleQueryEntry) queryListModel.getElementAt(i);
      final KettleTransFromFileProducer producer = queryEntry.createProducer();
      kettleDataFactory.setQuery(queryEntry.getName(), producer);
    }

    return kettleDataFactory;
  }

  private void loadData(final KettleDataFactory dataFactory, final String selectedQueryName)
  {
    if (dataFactory == null)
    {
      return;
    }

    KettleQueryEntry selectedDataSet = null;

    final String[] queryNames = dataFactory.getQueryNames();
    for (int i = 0; i < queryNames.length; i++)
    {
      final String queryName = queryNames[i];
      final KettleTransFromFileProducer producer = (KettleTransFromFileProducer) dataFactory.getQuery(queryName);

      final KettleQueryEntry dataSet = new KettleQueryEntry(queryName);
      dataSet.setFile(producer.getTransformationFile());
      dataSet.setSelectedStep(producer.getStepName());
      dataSet.setArguments(producer.getDefinedArgumentNames());
      dataSet.setParameters(producer.getDefinedVariableNames());
      queryListModel.addElement(dataSet);
      if (ObjectUtilities.equal(selectedQueryName, queryName))
      {
        selectedDataSet = dataSet;
      }
    }

    queryNameList.setSelectedValue(selectedDataSet, true);
  }

  protected boolean validateInputs(final boolean onConfirm)
  {
    getConfirmAction().setEnabled(queryNameList.getModel().getSize() > 0);
    return super.validateInputs(onConfirm);
  }
}
