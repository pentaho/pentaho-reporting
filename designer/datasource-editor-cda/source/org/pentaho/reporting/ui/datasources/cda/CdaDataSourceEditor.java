package org.pentaho.reporting.ui.datasources.cda;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataFactoryEditorSupport;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.CdaDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.CdaQueryEntry;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.CdaResponseParser;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.HttpQueryBackend;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.SmartComboBox;
import org.pentaho.reporting.libraries.designtime.swing.VerticalLayout;
import org.pentaho.reporting.libraries.designtime.swing.background.CancelEvent;
import org.pentaho.reporting.libraries.designtime.swing.background.DataPreviewDialog;
import org.pentaho.reporting.libraries.designtime.swing.background.PreviewWorker;
import org.pentaho.reporting.libraries.formula.util.URLEncoder;

public class CdaDataSourceEditor extends CommonDialog
{
  private class FetchAction extends AbstractAction
  {
    private FetchAction()
    {
      putValue(Action.NAME, Messages.getString("CdaDataSourceEditor.FetchAction.Name"));
    }

    public void actionPerformed(final ActionEvent e)
    {

      try
      {
        final TypedTableModel model = fetchData("listQueries", new HashMap<String, String>());

        final QueriesTableModel clone = (QueriesTableModel) queriesTableModel.clone();
        queriesTableModel.clear();
        for (int i = 0; i < model.getRowCount(); i++)
        {
          final String query = (String) model.getValueAt(i, 0);
          final String name = (String) model.getValueAt(i, 1);

          final HashMap<String,String> extraParameter = new HashMap<String, String>();
          extraParameter.put("dataAccessId", query);
          final TypedTableModel param = fetchData("listParameters", extraParameter);

          final HashMap<String,String> oldParamMappings = new HashMap<String, String>();
          final QueriesTableModel.QueryData queryById = clone.getQueryById(query);
          if (queryById != null)
          {
            final ParameterMapping[] parameters = queryById.getQueryEntry().getParameters();
            for (final ParameterMapping parameter : parameters)
            {
              oldParamMappings.put(parameter.getAlias(), parameter.getName());
            }
          }

          final ParameterMapping[] parameterMappings = new ParameterMapping[param.getRowCount()];
          final String[] declaredParameters = new String[parameterMappings.length];
          for (int j = 0; j < parameterMappings.length; j++)
          {
            final String paramNameOnServer = (String) param.getValueAt(j, 0);
            String mappedName = oldParamMappings.get(paramNameOnServer);
            if (mappedName == null)
            {
              mappedName = paramNameOnServer;
            }
            parameterMappings[j] = new ParameterMapping(mappedName, paramNameOnServer);
            declaredParameters[j] = paramNameOnServer;
          }

          final String queryName;
          if (StringUtils.isEmpty(name))
          {
            queryName = "Anonymous Query #" + i;
          }
          else
          {
            queryName = name;
          }
          
          final CdaQueryEntry entry = new CdaQueryEntry(queryName, query);
          entry.setParameters(parameterMappings);
          queriesTableModel.add(new QueriesTableModel.QueryData(entry, declaredParameters));
          extraParameter.clear();
        }
      }
      catch (ReportDataFactoryException e1)
      {
        designTimeContext.error(e1);
      }
    }
  }


  private class PreviewAction extends AbstractAction
  {
    private PreviewAction()
    {
      putValue(Action.NAME, Messages.getString("CdaDataSourceEditor.Preview.Name"));
    }

    public void actionPerformed(final ActionEvent aEvt)
    {
      final int selectedRow = queriesTable.getSelectedRow();
      if (selectedRow == -1)
      {
        return;
      }
      try
      {
        final CdaDataFactory dataFactory = produceDataFactory();
        DataFactoryEditorSupport.configureDataFactoryForPreview(dataFactory, designTimeContext);

        final DataPreviewDialog previewDialog = new DataPreviewDialog(CdaDataSourceEditor.this);

        final CdaPreviewWorker worker = new CdaPreviewWorker(dataFactory, queriesTableModel.getName(selectedRow));
        previewDialog.showData(worker);

        final ReportDataFactoryException factoryException = worker.getException();
        if (factoryException != null)
        {
          ExceptionDialog.showExceptionDialog(CdaDataSourceEditor.this,
              Messages.getString("ErrorDialog.Title"), Messages.getString("ErrorDialog.PreviewError"),
              factoryException);
        }
      }
      catch (Exception e)
      {
        ExceptionDialog.showExceptionDialog(CdaDataSourceEditor.this,
            Messages.getString("ErrorDialog.Title"), Messages.getString("ErrorDialog.PreviewError"),
            e);
      }
    }
  }


  private static class CdaPreviewWorker implements PreviewWorker
  {
    private CdaDataFactory dataFactory;
    private TableModel resultTableModel;
    private ReportDataFactoryException exception;
    private String query;

    private CdaPreviewWorker(final CdaDataFactory dataFactory,
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
        dataFactory.close();
      }
      catch (ReportDataFactoryException e)
      {
        exception = e;
      }
    }
  }

  private JTextField baseUrl;
  private JComboBox baseUrlField;
  private JTextField solution;
  private JTextField path;
  private JTextField file;
  private JTextField username;
  private JTextField password;
  private JCheckBox useLocalCall;
  private QueriesTableModel queriesTableModel;
  private GetMethod httpCall;
  private HttpClient client;
  private DesignTimeContext designTimeContext;
  private JTable queriesTable;
  private Action editParameterAction;
  private Action previewAction;

  public CdaDataSourceEditor(final DesignTimeContext context)
  {
    init(context);
  }

  public CdaDataSourceEditor(final DesignTimeContext context, final Frame owner)
      throws HeadlessException
  {
    super(owner);
    init(context);
  }

  public CdaDataSourceEditor(final DesignTimeContext context, final Dialog owner)
      throws HeadlessException
  {
    super(owner);
    init(context);
  }

  private void init(final DesignTimeContext context)
  {
    this.designTimeContext = context;

    this.queriesTableModel = new QueriesTableModel();
    queriesTable = new JTable(queriesTableModel);
    baseUrl = new JTextField();
    baseUrlField = new SmartComboBox();
    baseUrlField.setEditable(true);
    final DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(context.getDataSchemaModel().getColumnNames());
    comboBoxModel.insertElementAt(null, 0);
    comboBoxModel.setSelectedItem(null);
    baseUrlField.setModel(comboBoxModel);
    solution = new JTextField();
    path = new JTextField();
    file = new JTextField();
    username = new JTextField();
    password = new JTextField();
    useLocalCall = new JCheckBox(Messages.getString("CdaDataSourceEditor.AllowLocalAPICalls"));
    editParameterAction = new EditParameterAction();

    previewAction = new PreviewAction();

    super.init();
  }

  protected String getDialogId()
  {
    return "CdaDataSourceEditor";
  }

  protected Component createContentPane()
  {
    final JPanel fetchQueriesPanel = new JPanel();
    fetchQueriesPanel.setLayout(new BorderLayout());
    fetchQueriesPanel.add(new JLabel(Messages.getString("CdaDataSourceEditor.FetchQueryFromServer")), BorderLayout.CENTER);
    fetchQueriesPanel.add(new JButton(new FetchAction()), BorderLayout.EAST);

    final JPanel previewAndParameterPanel = new JPanel(new BorderLayout());
    previewAndParameterPanel.add(new JButton(previewAction), BorderLayout.EAST);
    previewAndParameterPanel.add(new JButton(editParameterAction), BorderLayout.CENTER);

    final JPanel panel = new JPanel();
    panel.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
    panel.add(new JLabel(Messages.getString("CdaDataSourceEditor.ServerURL")));
    panel.add(baseUrl);
    panel.add(new JLabel(Messages.getString("CdaDataSourceEditor.ServerURLField")));
    panel.add(baseUrlField);
    panel.add(new JLabel(Messages.getString("CdaDataSourceEditor.Username")));
    panel.add(username);
    panel.add(new JLabel(Messages.getString("CdaDataSourceEditor.Password")));
    panel.add(password);
    panel.add(new JLabel(Messages.getString("CdaDataSourceEditor.Solution")));
    panel.add(solution);
    panel.add(new JLabel(Messages.getString("CdaDataSourceEditor.Path")));
    panel.add(path);
    panel.add(new JLabel(Messages.getString("CdaDataSourceEditor.File")));
    panel.add(file);
    panel.add(useLocalCall);
    panel.add(fetchQueriesPanel);

    final JPanel cpanel = new JPanel();
    cpanel.setLayout(new BorderLayout());
    cpanel.setBorder(new EmptyBorder(3,3,3,3));
    cpanel.add(panel, BorderLayout.NORTH);
    cpanel.add(new JScrollPane(queriesTable), BorderLayout.CENTER);
    cpanel.add(previewAndParameterPanel, BorderLayout.SOUTH);
    return cpanel;
  }

  public DataFactory performConfiguration(final CdaDataFactory input, final String queryName)
  {
    if (input != null)
    {
      baseUrl.setText(input.getBaseUrl());
      baseUrlField.setSelectedItem(input.getBaseUrlField());
      password.setText(input.getPassword());
      username.setText(input.getUsername());
      file.setText(input.getFile());
      solution.setText(input.getSolution());
      path.setText(input.getPath());
      useLocalCall.setSelected(input.isUseLocalCall());

      queriesTableModel.clear();
      final String[] queryNames = input.getQueryNames();
      for (int i = 0; i < queryNames.length; i++)
      {
        final String name = queryNames[i];
        final CdaQueryEntry queryEntry = input.getQueryEntry(name);
        queriesTableModel.add(new QueriesTableModel.QueryData(queryEntry, new String[0]));
      }
    }
    else
    {
      useLocalCall.setSelected(true);
    }

    if (performEdit() == false)
    {
      return null;
    }

    return produceDataFactory();
  }

  private CdaDataFactory produceDataFactory()
  {
    final CdaDataFactory dataFactory = new CdaDataFactory();
    dataFactory.setBaseUrl(baseUrl.getText());
    dataFactory.setBaseUrlField((String) baseUrlField.getSelectedItem());
    dataFactory.setPassword(password.getText());
    dataFactory.setUsername(username.getText());
    dataFactory.setFile(file.getText());
    dataFactory.setPath(path.getText());
    dataFactory.setSolution(solution.getText());
    dataFactory.setUseLocalCall(useLocalCall.isSelected());
    for (int i = 0; i < queriesTableModel.size(); i++)
    {
      final QueriesTableModel.QueryData queryData = queriesTableModel.get(i);
      dataFactory.setQueryEntry(queryData.getQueryEntry().getName(), queryData.getQueryEntry());
    }
    return dataFactory;
  }


  private TypedTableModel fetchData(final String method,
                                    final Map<String, String> extraParameter) throws ReportDataFactoryException
  {
    if (StringUtils.isEmpty(baseUrl.getText(), true))
    {
      throw new ReportDataFactoryException("Base URL is null");
    }
    try
    {
      final StringBuilder url = new StringBuilder();
      url.append(baseUrl.getText());
      url.append("/content/cda/");
      url.append(method);
      url.append("?");
      url.append("outputType=xml");
      url.append("&solution=");
      url.append(encodeParameter(solution.getText()));
      url.append("&path=");
      url.append(encodeParameter(path.getText()));
      url.append("&file=");
      url.append(encodeParameter(file.getText()));
      for (final Map.Entry<String, String> entry : extraParameter.entrySet())
      {
        final String key = encodeParameter(entry.getKey());
        if (StringUtils.isEmpty(key))
        {
          continue;
        }
        url.append("&");
        url.append(key);
        url.append("=");
        url.append(encodeParameter(entry.getValue()));
      }

      httpCall = new GetMethod(url.toString());
      final HttpClient client = getHttpClient();
      final int status = client.executeMethod(httpCall);
      if (status != 200)
      {
        throw new ReportDataFactoryException("Failed to retrieve data: " + httpCall.getStatusLine() + " Called: " + url);
      }

      final InputStream responseBody = httpCall.getResponseBodyAsStream();
      return CdaResponseParser.performParse(responseBody);
    }
    catch (UnsupportedEncodingException use)
    {
      throw new ReportDataFactoryException("Failed to encode parameter", use);
    }
    catch (Exception e)
    {
      throw new ReportDataFactoryException("Failed to send request", e);
    }
    finally
    {
      httpCall = null;
    }
  }

  private HttpClient getHttpClient()
  {
    if (client == null)
    {
      client = new HttpClient();
      client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
      client.getParams().setAuthenticationPreemptive(true);
    }
    client.getState().setCredentials(AuthScope.ANY, HttpQueryBackend.getCredentials(username.getText(), password.getText()));
    return client;
  }

  private String getURLEncoding()
  {
    return ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.URLEncoding");
  }

  private String encodeParameter(final String value) throws UnsupportedEncodingException
  {
    if (StringUtils.isEmpty(value))
    {
      return "";
    }
    return URLEncoder.encode(value, getURLEncoding());
  }


  private class EditParameterAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private EditParameterAction()
    {
      putValue(Action.NAME, Messages.getString("CdaDataSourceEditor.EditParameter.Name"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      final int selectedRow = queriesTable.getSelectedRow();
      if ((selectedRow == -1) || (queriesTable.getSelectedRowCount() > 1))
      {
        return; /* TODO all error message */
      }

      final QueriesTableModel.QueryData queryData = queriesTableModel.get(selectedRow);
      final CdaQueryEntry queryEntry = queryData.getQueryEntry();

      try
      {
        final ParameterEditorDialog dialog = new ParameterEditorDialog(CdaDataSourceEditor.this);
        final String[] reportFields = designTimeContext.getDataSchemaModel().getColumnNames();
        final ParameterEditorDialog.EditResult editResult =
            dialog.performEdit(queryEntry.getParameters(), reportFields, queryData.getDeclaredParameter());
        if (editResult == null)
        {
          return;
        }

        queryEntry.setParameters(editResult.getParameterMappings());
      }
      catch (Exception e1)
      {
        designTimeContext.error(e1);
      }
      catch (Throwable t1)
      {
        designTimeContext.error(new RuntimeException("Fatal error", t1));
      }
    }
  }


}
