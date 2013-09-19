package org.pentaho.reporting.ui.datasources.kettle;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkDownAction;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkUpAction;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.TagListTableCellEditor;

public class ParameterEditorDialog extends CommonDialog
{

  private class RemoveParameterAction extends AbstractAction implements ListSelectionListener
  {
    private RemoveParameterAction()
    {
      final URL resource = KettleDataSourceDialog.class.getResource
          ("/org/pentaho/reporting/ui/datasources/kettle/resources/Remove.png");
      if (resource != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(resource));
      }
      else
      {
        putValue(Action.NAME, Messages.getString("ParameterEditorDialog.RemoveParameter.Name"));
      }
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("ParameterEditorDialog.RemoveParameter.Description"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      final TableCellEditor tableCellEditor = parameterMappingTable.getCellEditor();
      if (tableCellEditor != null)
      {
        tableCellEditor.stopCellEditing();
      }
      final int i = parameterMappingTable.getSelectedRow();
      if (i == -1)
      {
        return;
      }

      final ParameterMappingTableModel tableModel = (ParameterMappingTableModel) parameterMappingTable.getModel();
      tableModel.removeRow(i);
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(final ListSelectionEvent e)
    {
      setEnabled(parameterMappingTable.getSelectedRow() != -1);
    }
  }


  private class AddParameterAction extends AbstractAction
  {
    private AddParameterAction()
    {
      final URL resource = KettleDataSourceDialog.class.getResource
          ("/org/pentaho/reporting/ui/datasources/kettle/resources/Add.png");
      if (resource != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(resource));
      }
      else
      {
        putValue(Action.NAME, Messages.getString("ParameterEditorDialog.AddParameter.Name"));
      }
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("ParameterEditorDialog.AddParameter.Description"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      final TableCellEditor tableCellEditor = parameterMappingTable.getCellEditor();
      if (tableCellEditor != null)
      {
        tableCellEditor.stopCellEditing();
      }
      final ParameterMappingTableModel tableModel = (ParameterMappingTableModel) parameterMappingTable.getModel();
      tableModel.addRow();
    }
  }

  public static class EditResult
  {
    private String[] argumentNames;
    private ParameterMapping[] parameterMappings;

    public EditResult(final String[] argumentNames, final ParameterMapping[] parameterMappings)
    {
      if (argumentNames == null)
      {
        throw new NullPointerException();
      }
      if (parameterMappings == null)
      {
        throw new NullPointerException();
      }

      this.argumentNames = argumentNames.clone();
      this.parameterMappings = parameterMappings.clone();
    }

    public String[] getArgumentNames()
    {
      return argumentNames.clone();
    }

    public ParameterMapping[] getParameterMappings()
    {
      return parameterMappings.clone();
    }
  }

  private class AddArgumentAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private AddArgumentAction()
    {
      final URL resource = KettleDataSourceDialog.class.getResource
          ("/org/pentaho/reporting/ui/datasources/kettle/resources/Add.png");
      if (resource != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(resource));
      }
      else
      {
        putValue(Action.NAME, Messages.getString("ParameterEditorDialog.AddArgument.Name"));
      }
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("ParameterEditorDialog.AddArgument.Description"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      final TableCellEditor tableCellEditor = argumentTable.getCellEditor();
      if (tableCellEditor != null)
      {
        tableCellEditor.stopCellEditing();
      }

      final ArgumentTableModel tableModel = (ArgumentTableModel) argumentTable.getModel();
      tableModel.addRow();
    }
  }

  private class RemoveArgumentAction extends AbstractAction implements ListSelectionListener
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private RemoveArgumentAction()
    {
      final URL resource = KettleDataSourceDialog.class.getResource
          ("/org/pentaho/reporting/ui/datasources/kettle/resources/Remove.png");
      if (resource != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(resource));
      }
      else
      {
        putValue(Action.NAME, Messages.getString("ParameterEditorDialog.RemoveArgument.Name"));
      }
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("ParameterEditorDialog.RemoveArgument.Description"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      final TableCellEditor tableCellEditor = argumentTable.getCellEditor();
      if (tableCellEditor != null)
      {
        tableCellEditor.stopCellEditing();
      }
      final int i = argumentTable.getSelectedRow();
      if (i == -1)
      {
        return;
      }

      final ArgumentTableModel tableModel = (ArgumentTableModel) argumentTable.getModel();
      tableModel.removeRow(i);
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(final ListSelectionEvent e)
    {
      setEnabled(argumentTable.getSelectedRow() != -1);
    }
  }

  private JTable argumentTable;
  private JTable parameterMappingTable;
  private TagListTableCellEditor innerTableCellEditor;
  private TagListTableCellEditor outerTableCellEditor;
  private TagListTableCellEditor argsTableCellEditor;

  public ParameterEditorDialog()
  {
    init();
  }

  public ParameterEditorDialog(final Frame owner)
  {
    super(owner);
    init();
  }

  public ParameterEditorDialog(final Dialog owner)
  {
    super(owner);
    init();
  }

  protected void init()
  {
    super.init();
    setTitle(Messages.getString("ParameterEditorDialog.Title"));
  }

  protected String getDialogId()
  {
    return "KettleDataSourceDialog.ParameterEditor";
  }

  protected Component createContentPane()
  {
    innerTableCellEditor = new TagListTableCellEditor();
    outerTableCellEditor = new TagListTableCellEditor();
    argsTableCellEditor = new TagListTableCellEditor();

    parameterMappingTable = new JTable(new ParameterMappingTableModel());
    parameterMappingTable.getColumnModel().getColumn(0).setCellEditor(innerTableCellEditor);
    parameterMappingTable.getColumnModel().getColumn(1).setCellEditor(outerTableCellEditor);
    parameterMappingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    final ArgumentTableModel argumentTableModel = new ArgumentTableModel();
    argumentTable = new JTable(argumentTableModel);
    argumentTable.getColumnModel().getColumn(1).setCellEditor(argsTableCellEditor);
    argumentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    final RemoveParameterAction removeParameterAction = new RemoveParameterAction();
    parameterMappingTable.getSelectionModel().addListSelectionListener(removeParameterAction);

    final JPanel parameterMappingButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    parameterMappingButtonPanel.add(new BorderlessButton(new AddParameterAction()));
    parameterMappingButtonPanel.add(new BorderlessButton(new RemoveParameterAction()));

    final JPanel parameterMappingPanel = new JPanel(new BorderLayout());
    parameterMappingPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("ParameterEditorDialog.ParameterBox")));
    parameterMappingPanel.add(new JScrollPane(parameterMappingTable), BorderLayout.CENTER);
    parameterMappingPanel.add(parameterMappingButtonPanel, BorderLayout.NORTH);

    final RemoveArgumentAction removeArgumentAction = new RemoveArgumentAction();
    argumentTable.getSelectionModel().addListSelectionListener(removeArgumentAction);

    final JPanel argumentsButtonPanel = new JPanel();
    argumentsButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    argumentsButtonPanel.add(new BorderlessButton(new AddArgumentAction()));
    argumentsButtonPanel.add(new BorderlessButton(removeArgumentAction));
    argumentsButtonPanel.add(new BorderlessButton(new SortBulkUpAction(argumentTableModel, argumentTable.getSelectionModel())));
    argumentsButtonPanel.add(new BorderlessButton(new SortBulkDownAction(argumentTableModel, argumentTable.getSelectionModel())));

    final JPanel argumentsButtonCarrier = new JPanel();
    argumentsButtonCarrier.setLayout(new BorderLayout());
    argumentsButtonCarrier.add(argumentsButtonPanel, BorderLayout.NORTH);

    final JPanel argumentsPanel = new JPanel();
    argumentsPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("ParameterEditorDialog.ArgumentBox")));
    argumentsPanel.setLayout(new BorderLayout());
    argumentsPanel.add(new JScrollPane(argumentTable), BorderLayout.CENTER);
    argumentsPanel.add(argumentsButtonCarrier, BorderLayout.NORTH);

    final JPanel contentPane = new JPanel();
    contentPane.setLayout(new GridLayout(1, 2, 5, 5));
    contentPane.add(parameterMappingPanel);
    contentPane.add(argumentsPanel);
    return contentPane;
  }

  public EditResult performEdit(final String[] argumentNames, final ParameterMapping[] parameterMappings,
                                final String[] reportFields, final String[] transformationParameters)
  {
    argsTableCellEditor.setTags(reportFields);
    innerTableCellEditor.setTags(reportFields);
    outerTableCellEditor.setTags(transformationParameters);

    final ParameterMappingTableModel parameterMappingTableModel =
        (ParameterMappingTableModel) parameterMappingTable.getModel();
    parameterMappingTableModel.setMappings(parameterMappings);

    final ArgumentTableModel argumentTableModel =
        (ArgumentTableModel) argumentTable.getModel();
    argumentTableModel.setArguments(argumentNames);

    if (super.performEdit() == false)
    {
      return null;
    }

    final TableCellEditor cellEditor = parameterMappingTable.getCellEditor();
    if (cellEditor != null)
    {
      cellEditor.stopCellEditing();
    }

    final TableCellEditor tableCellEditor = argumentTable.getCellEditor();
    if (tableCellEditor != null)
    {
      tableCellEditor.stopCellEditing();
    }

    return new EditResult(argumentTableModel.getArguments(), parameterMappingTableModel.getMappings());
  }
}
