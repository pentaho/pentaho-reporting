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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.crosstab;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.filter.types.NumberFieldType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.wizard.AutoGeneratorUtility;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.bulk.BulkDataProvider;
import org.pentaho.reporting.libraries.designtime.swing.bulk.DefaultBulkListModel;
import org.pentaho.reporting.libraries.designtime.swing.bulk.RemoveBulkAction;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkDownAction;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkUpAction;
import org.pentaho.reporting.libraries.designtime.swing.table.PropertyTable;

public class CreateCrosstabDialog extends CommonDialog
{
  private class AddListSelectionAction extends AbstractAction implements ListSelectionListener
  {
    private ListSelectionModel selectionModel;
    private DefaultListModel data;
    private ListModel fields;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private AddListSelectionAction(final JList availableFields,
                                   final DefaultListModel data)
    {


      this.selectionModel = availableFields.getSelectionModel();
      this.fields = availableFields.getModel();
      this.data = data;

      putValue(Action.SMALL_ICON, IconLoader.getInstance().getFowardArrowIcon());
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("CreateCrosstabDialog.AddColumn"));
      selectionModel.addListSelectionListener(this);
      setEnabled(selectionModel.isSelectionEmpty() == false);
    }

    public void valueChanged(final ListSelectionEvent e)
    {
      setEnabled(selectionModel.isSelectionEmpty() == false);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      for (int i = 0; i < fields.getSize(); i++)
      {
        if (selectionModel.isSelectedIndex(i))
        {
          data.addElement(fields.getElementAt(i));
        }
      }
    }
  }

  private class AddDimensionAction extends AbstractAction implements ListSelectionListener
  {
    private ListSelectionModel selectionModel;
    private CrosstabDimensionTableModel data;
    private ListModel fields;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private AddDimensionAction(final JList availableFields,
                               final CrosstabDimensionTableModel data)
    {
      this.selectionModel = availableFields.getSelectionModel();
      this.fields = availableFields.getModel();
      this.data = data;

      putValue(Action.SMALL_ICON, IconLoader.getInstance().getFowardArrowIcon());
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("CreateCrosstabDialog.AddColumn"));
      selectionModel.addListSelectionListener(this);
      setEnabled(selectionModel.isSelectionEmpty() == false);
    }

    public void valueChanged(final ListSelectionEvent e)
    {
      setEnabled(selectionModel.isSelectionEmpty() == false);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      for (int i = 0; i < fields.getSize(); i++)
      {
        if (selectionModel.isSelectedIndex(i))
        {
          final String item = (String) fields.getElementAt(i);
          data.add(new CrosstabDimension(item));
        }
      }
    }
  }

  private class AddDetailsAction extends AbstractAction implements ListSelectionListener
  {
    private ListSelectionModel selectionModel;
    private CrosstabDetailTableModel data;
    private ListModel fields;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private AddDetailsAction(final JList availableFields,
                             final CrosstabDetailTableModel data)
    {
      this.selectionModel = availableFields.getSelectionModel();
      this.fields = availableFields.getModel();
      this.data = data;

      putValue(Action.SMALL_ICON, IconLoader.getInstance().getFowardArrowIcon());
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("CreateCrosstabDialog.AddColumn"));
      selectionModel.addListSelectionListener(this);
      setEnabled(selectionModel.isSelectionEmpty() == false);
    }

    public void valueChanged(final ListSelectionEvent e)
    {
      setEnabled(selectionModel.isSelectionEmpty() == false);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      for (int i = 0; i < fields.getSize(); i++)
      {
        if (selectionModel.isSelectedIndex(i))
        {
          final String item = (String) fields.getElementAt(i);
          data.add(new CrosstabDetail(item));
        }
      }
    }
  }

  private JList otherFields;
  private JTable rowFields;
  private JTable columnFields;
  private JTable detailFields;

  private JList availableFields;
  private DefaultBulkListModel availableFieldsModel;
  private DefaultBulkListModel otherFieldsModel;
  private CrosstabDimensionTableModel rowsFieldsModel;
  private CrosstabDimensionTableModel columnsFieldsModel;
  private CrosstabDetailTableModel detailFieldsModel;
  private ReportRenderContext reportRenderContext;

  public CreateCrosstabDialog()
  {
    init();
  }

  public CreateCrosstabDialog(final Frame owner) throws HeadlessException
  {
    super(owner);
    init();
  }

  public CreateCrosstabDialog(final Dialog owner) throws HeadlessException
  {
    super(owner);
    init();
  }

  protected void init()
  {
    setTitle(Messages.getString("CreateCrosstabDialog.Title"));
    setModal(true);

    availableFieldsModel = new DefaultBulkListModel();

    availableFields = new JList(availableFieldsModel);
    availableFields.setDragEnabled(true);
    availableFields.setTransferHandler(new ListTransferHandler(availableFields, availableFieldsModel));
    availableFields.setDropMode(DropMode.ON);

    otherFieldsModel = new DefaultBulkListModel();
    otherFields = new JList(otherFieldsModel);
    otherFields.setVisibleRowCount(3);
    otherFields.setTransferHandler(new ListTransferHandler(otherFields, otherFieldsModel));
    otherFields.setDragEnabled(true);
    otherFields.setDropMode(DropMode.ON);

    rowsFieldsModel = new CrosstabDimensionTableModel();
    rowFields = new PropertyTable(rowsFieldsModel);
    rowFields.setTransferHandler(new CrosstabDimensionTableTransferHandler(rowFields, rowsFieldsModel));
    rowFields.setDragEnabled(true);
    rowFields.setDropMode(DropMode.ON);

    columnsFieldsModel = new CrosstabDimensionTableModel();
    columnFields = new PropertyTable(columnsFieldsModel);
    columnFields.setTransferHandler(new CrosstabDimensionTableTransferHandler(columnFields, columnsFieldsModel));
    columnFields.setDragEnabled(true);
    columnFields.setDropMode(DropMode.ON);

    detailFieldsModel = new CrosstabDetailTableModel();
    detailFields = new PropertyTable(detailFieldsModel);
    detailFields.setTransferHandler(new CrosstabDetailTableTransferHandler(detailFields, detailFieldsModel));
    detailFields.setDragEnabled(true);
    detailFields.setDropMode(DropMode.ON);

    super.init();
  }

  protected void performInitialResize()
  {
    super.performInitialResize();
    if (getHeight() > 800)
    {
      setBounds(getX(), getY(), getWidth(), 800);
    }
    LibSwingUtil.centerDialogInParent(this);
  }

  protected String getDialogId()
  {
    return "ReportDesigner.Core.CreateCrosstab";
  }

  protected Component createContentPane()
  {
    final JPanel contentPane = new JPanel();
    contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout());
    contentPane.add(createTitlePanel(), BorderLayout.NORTH);
    contentPane.add(createSelectionPane(), BorderLayout.CENTER);
    return contentPane;
  }

  private JPanel createTitlePanel()
  {
    final JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
    titlePanel.add(new JLabel(Messages.getString("CreateCrosstabDialog.TitleLabel")));
    return titlePanel;
  }

  private JComponent createSelectionPane()
  {
    final JPanel sidePane = new JPanel();
    sidePane.setLayout(new BorderLayout());

    final JLabel tablesColumnsLabel = new JLabel(Messages.getString("CreateCrosstabDialog.AvailableFields"));
    sidePane.add(tablesColumnsLabel, BorderLayout.NORTH);
    final JScrollPane comp = new JScrollPane
        (availableFields, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    sidePane.add(comp, BorderLayout.CENTER);

    final JPanel tablesPane = new JPanel();
    tablesPane.setLayout(new GridBagLayout());
    addList(tablesPane, 0, otherFields, "OtherFields");
    addTable(tablesPane, 1, rowFields, "RowFields", new AddDimensionAction(availableFields, rowsFieldsModel));
    addTable(tablesPane, 2, columnFields, "ColumnsFields", new AddDimensionAction(availableFields, columnsFieldsModel));
    addTable(tablesPane, 3, detailFields, "Details", new AddDetailsAction(availableFields, detailFieldsModel));

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidePane, tablesPane);
    splitPane.setBorder(null);
    return splitPane;
  }

  private void addTable(final JComponent tablesPane, final int index,
                        final JTable list, final String labelText, final Action addAction)
  {
    final BulkDataProvider bulkListModel = (BulkDataProvider) list.getModel();
    final JButton otherAdd = new BorderlessButton(addAction);
    final JLabel otherLabel = new JLabel(Messages.getString("CreateCrosstabDialog." + labelText));

    final ListSelectionModel otherSelectionModel = list.getSelectionModel();
    final JButton otherSortUp = new BorderlessButton(new SortBulkUpAction(bulkListModel, otherSelectionModel));
    final JButton otherSortDown = new BorderlessButton(new SortBulkDownAction(bulkListModel, otherSelectionModel));
    final JButton otherRemove = new BorderlessButton(new RemoveBulkAction(bulkListModel, otherSelectionModel));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 2;
    gbc.gridy = 0 + index * 2;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);
    tablesPane.add(otherLabel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 0 + index * 2;
    gbc.insets = new Insets(5, 5, 5, 5);
    tablesPane.add(otherSortUp, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 0 + index * 2;
    gbc.insets = new Insets(5, 5, 5, 5);
    tablesPane.add(otherSortDown, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 0 + index * 2;
    gbc.insets = new Insets(5, 5, 5, 5);
    tablesPane.add(otherRemove, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1 + index * 2;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridwidth = 4;
    gbc.insets = new Insets(0, 5, 5, 0);
    tablesPane.add(new JScrollPane
        (list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1 + index * 2;
    gbc.insets = new Insets(5, 5, 5, 0);
    tablesPane.add(otherAdd, gbc);
  }

  private void addList(final JComponent tablesPane, final int index, final JList list, final String labelText)
  {
    final DefaultBulkListModel bulkListModel = (DefaultBulkListModel) list.getModel();
    final JButton otherAdd = new BorderlessButton(new AddListSelectionAction(availableFields, bulkListModel));
    final JLabel otherLabel = new JLabel(Messages.getString("CreateCrosstabDialog." + labelText));

    final ListSelectionModel otherSelectionModel = list.getSelectionModel();
    final JButton otherSortUp = new BorderlessButton(new SortBulkUpAction(bulkListModel, otherSelectionModel));
    final JButton otherSortDown = new BorderlessButton(new SortBulkDownAction(bulkListModel, otherSelectionModel));
    final JButton otherRemove = new BorderlessButton(new RemoveBulkAction(bulkListModel, otherSelectionModel));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 2;
    gbc.gridy = 0 + index * 2;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);
    tablesPane.add(otherLabel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 0 + index * 2;
    gbc.insets = new Insets(5, 5, 5, 5);
    tablesPane.add(otherSortUp, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 0 + index * 2;
    gbc.insets = new Insets(5, 5, 5, 5);
    tablesPane.add(otherSortDown, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 0 + index * 2;
    gbc.insets = new Insets(5, 5, 5, 5);
    tablesPane.add(otherRemove, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1 + index * 2;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridwidth = 4;
    gbc.insets = new Insets(0, 5, 5, 0);
    tablesPane.add(new JScrollPane
        (list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1 + index * 2;
    gbc.insets = new Insets(5, 5, 5, 0);
    tablesPane.add(otherAdd, gbc);
  }

  protected DefaultBulkListModel getAvailableFieldsModel()
  {
    return availableFieldsModel;
  }

  private Element createFieldItem(final String text)
  {
    return createFieldItem(text, null, null);
  }
  
  private Element createFieldItem(final String fieldName,
                                         final String aggregationGroup,
                                         final Class aggregationType)
  {
    final DataAttributeContext context = reportRenderContext.getReportDataSchemaModel().getDataAttributeContext();
    final DataAttributes attributes = reportRenderContext.getReportDataSchemaModel().getDataSchema().getAttributes(fieldName);
    final ElementType targetType = AutoGeneratorUtility.createFieldType(attributes, context);

    final Element element = new Element();
    element.setElementType(targetType);
    element.getElementType().configureDesignTimeDefaults(element, Locale.getDefault());

    if (targetType instanceof NumberFieldType)
    {
      element.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING, "0.00;-0.00");
    }

    element.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, fieldName);
    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, 80f);
    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 20f);
    element.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.AGGREGATION_TYPE, aggregationType);
    element.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.AGGREGATION_GROUP, aggregationGroup);
    element.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING, Boolean.TRUE);
    return element;
  }

  private static Element createLabel(final String text)
  {
    final Element label = new Element();
    label.setElementType(LabelType.INSTANCE);
    label.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text);
    label.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, 80f);
    label.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 20f);
    return label;
  }

  public CrosstabGroup createCrosstab(final ReportRenderContext reportRenderContext)
  {
    if (reportRenderContext == null)
    {
      throw new NullPointerException();
    }
    this.reportRenderContext = reportRenderContext;

    final String[] columnNames = reportRenderContext.getReportDataSchemaModel().getColumnNames();
    final DefaultListModel availableFieldsModel = getAvailableFieldsModel();
    availableFieldsModel.clear();
    for (int i = 0; i < columnNames.length; i++)
    {
      availableFieldsModel.addElement(columnNames[i]);
    }

    if (performEdit() == false)
    {
      return null;
    }

    if (columnsFieldsModel.size() < 1)
    {
      return null;
    }
    if (rowsFieldsModel.size() < 1)
    {
      return null;
    }

    final CrosstabCellBody cellBody = new CrosstabCellBody();
    cellBody.addElement(createCell(null));

    GroupBody body = cellBody;
    for (int col = columnsFieldsModel.size() - 1; col >= 0; col -= 1)
    {
      final CrosstabDimension column = columnsFieldsModel.get(col);
      final CrosstabColumnGroup columnGroup = new CrosstabColumnGroup(body);
      columnGroup.setName("Group " + column.getField());
      columnGroup.setField(column.getField());
      columnGroup.getTitleHeader().getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, -100f);
      columnGroup.getTitleHeader().addElement(createLabel(column.getTitle()));
      columnGroup.getHeader().getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, -100f);
      columnGroup.getHeader().addElement(createFieldItem(column.getField()));
      columnGroup.getSummaryHeader().getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, -100f);
      columnGroup.getSummaryHeader().addElement(createLabel(column.getSummaryTitle()));

      if (column.isPrintSummary())
      {
        final CrosstabCell cell = createCell("Group " + column.getField());
        cell.setColumnField(column.getField());
        cell.setName(column.getField());
        cellBody.addElement(cell);
      }
      body = new CrosstabColumnGroupBody(columnGroup);
    }

    for (int row = rowsFieldsModel.size() - 1; row >= 0; row -= 1)
    {
      final CrosstabDimension rowDimension = rowsFieldsModel.get(row);
      final CrosstabRowGroup rowGroup = new CrosstabRowGroup(body);
      rowGroup.setName("Group " + rowDimension.getField());
      rowGroup.setField(rowDimension.getField());
      rowGroup.getTitleHeader().getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, -100f);
      rowGroup.getTitleHeader().addElement(createLabel(rowDimension.getTitle()));
      rowGroup.getHeader().getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, -100f);
      rowGroup.getHeader().addElement(createFieldItem(rowDimension.getField()));
      rowGroup.getSummaryHeader().getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, -100f);
      rowGroup.getSummaryHeader().addElement(createLabel(rowDimension.getSummaryTitle()));

      if (rowDimension.isPrintSummary())
      {
        final CrosstabCell cell = createCell("Group " + rowGroup.getField());
        cell.setRowField(rowDimension.getField());
        cell.setName(rowDimension.getField());
        cellBody.addElement(cell);

        for (int col = columnsFieldsModel.size() - 1; col >= 0; col -= 1)
        {
          final CrosstabDimension column = columnsFieldsModel.get(col);
          if (column.isPrintSummary())
          {
            final CrosstabCell crosstabCell = createCell("Group " + rowGroup.getField());
            crosstabCell.setColumnField(column.getField());
            crosstabCell.setRowField(rowDimension.getField());
            crosstabCell.setName(column.getField() + "," + rowGroup.getField());
            cellBody.addElement(crosstabCell);
          }
        }
      }
      body = new CrosstabRowGroupBody(rowGroup);
    }

    for (int other = otherFieldsModel.size() - 1; other >= 0; other -= 1)
    {
      final String column = (String) otherFieldsModel.get(other);
      final CrosstabOtherGroup columnGroup = new CrosstabOtherGroup(body);
      columnGroup.setField(column);
      columnGroup.getHeader().addElement(createFieldItem(column));

      body = new CrosstabOtherGroupBody(columnGroup);
    }

    return new CrosstabGroup(body);
  }

  private CrosstabCell createCell(final String group)
  {
    final CrosstabCell cell = new CrosstabCell();
    cell.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, -100f);
    cell.getStyle().setStyleProperty(BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_ROW);
    for (int i = 0; i < detailFieldsModel.size(); i += 1)
    {
      final CrosstabDetail crosstabDetail = detailFieldsModel.get(i);
      cell.addElement(createFieldItem(crosstabDetail.getField(), group, crosstabDetail.getAggregation()));
    }
    return cell;
  }

  public static void main(String[] args)
  {
    final CreateCrosstabDialog d = new CreateCrosstabDialog();
    d.getAvailableFieldsModel().addElement("Test");
    d.setModal(true);
    d.setVisible(true);

  }
}
