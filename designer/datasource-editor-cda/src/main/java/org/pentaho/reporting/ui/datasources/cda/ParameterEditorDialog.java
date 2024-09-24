/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.ui.datasources.cda;

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
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.TagListTableCellEditor;

public class ParameterEditorDialog extends CommonDialog
{

  private class RemoveParameterAction extends AbstractAction implements ListSelectionListener
  {
    private RemoveParameterAction()
    {
      final URL resource = CdaDataSourceEditor.class.getResource
          ("/org/pentaho/reporting/ui/datasources/cda/resources/Remove.png");
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
      final URL resource = CdaDataSourceEditor.class.getResource
          ("/org/pentaho/reporting/ui/datasources/cda/resources/Add.png");
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
    private ParameterMapping[] parameterMappings;

    public EditResult(final ParameterMapping[] parameterMappings)
    {
      if (parameterMappings == null)
      {
        throw new NullPointerException();
      }

      this.parameterMappings = parameterMappings.clone();
    }

    public ParameterMapping[] getParameterMappings()
    {
      return parameterMappings.clone();
    }
  }

  private JTable parameterMappingTable;
  private TagListTableCellEditor innerTableCellEditor;
  private TagListTableCellEditor outerTableCellEditor;

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

  @Override
  protected void init()
  {
    super.init();
    setTitle(Messages.getString("ParameterEditorDialog.Title"));
  }

  protected String getDialogId()
  {
    return "CdaDataSourceEditor.ParameterEditor";
  }

  @Override
  protected Component createContentPane()
  {
    innerTableCellEditor = new TagListTableCellEditor();
    outerTableCellEditor = new TagListTableCellEditor();

    parameterMappingTable = new JTable(new ParameterMappingTableModel());
    parameterMappingTable.getColumnModel().getColumn(0).setCellEditor(innerTableCellEditor);
    parameterMappingTable.getColumnModel().getColumn(1).setCellEditor(outerTableCellEditor);
    parameterMappingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    final RemoveParameterAction removeParameterAction = new RemoveParameterAction();
    parameterMappingTable.getSelectionModel().addListSelectionListener(removeParameterAction);

    final JPanel parameterMappingButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    parameterMappingButtonPanel.add(new BorderlessButton(new AddParameterAction()));
    parameterMappingButtonPanel.add(new BorderlessButton(new RemoveParameterAction()));

    final JPanel parameterMappingPanel = new JPanel(new BorderLayout());
    parameterMappingPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("ParameterEditorDialog.ParameterBox")));
    parameterMappingPanel.add(new JScrollPane(parameterMappingTable), BorderLayout.CENTER);
    parameterMappingPanel.add(parameterMappingButtonPanel, BorderLayout.NORTH);

    final JPanel contentPane = new JPanel();
    contentPane.setLayout(new GridLayout(1, 2, 5, 5));
    contentPane.add(parameterMappingPanel);
    return contentPane;
  }

  public EditResult performEdit(final ParameterMapping[] parameterMappings,
                                final String[] reportFields,
                                final String[] declaredParameter)
  {
    innerTableCellEditor.setTags(reportFields);
    outerTableCellEditor.setTags(declaredParameter);

    final ParameterMappingTableModel parameterMappingTableModel =
        (ParameterMappingTableModel) parameterMappingTable.getModel();
    parameterMappingTableModel.setMappings(parameterMappings);

    if (super.performEdit() == false)
    {
      return null;
    }

    final TableCellEditor cellEditor = parameterMappingTable.getCellEditor();
    if (cellEditor != null)
    {
      cellEditor.stopCellEditing();
    }

    return new EditResult(parameterMappingTableModel.getMappings());
  }
}
