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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.ancient.demo.csv;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.FormValidator;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.JStatusBar;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.LengthLimitingDocument;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.CSVTableModelProducer;
import org.pentaho.reporting.engine.classic.demo.util.DemoController;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * Demo that show how to use <code>CSVTableModelProducer</code> to generate <code>TableModel</code> for JFreeReport
 * input data.
 * <p/>
 * This version allows the user to enter data directly.
 *
 * @see CSVTableModelProducer
 */
public class CSVUserInputPanel extends JPanel
{
  /**
   * Internal action class to confirm the dialog and to validate the input.
   */
  private class ActionSelectSeparator extends AbstractAction
  {
    /**
     * Default constructor.
     */
    protected ActionSelectSeparator()
    {
    }

    /**
     * Receives notification that the action has occurred.
     *
     * @param e the action event.
     */
    public void actionPerformed(final ActionEvent e)
    {
      performSeparatorSelection();
    }
  }


  private class CSVDialogValidator extends FormValidator
  {
    protected CSVDialogValidator()
    {
      super();
    }

    public boolean performValidate()
    {
      return CSVUserInputPanel.this.performValidate();
    }

    public Action getConfirmAction()
    {
      return CSVUserInputPanel.this.getController().getExportAction();
    }
  }

  private class LoadCSVDataAction extends AbstractAction
  {
    protected LoadCSVDataAction()
    {
      putValue(Action.NAME, "Select");
    }

    public void actionPerformed(final ActionEvent e)
    {
      performLoadFile();
    }
  }

  public static final String RESOURCE_BASE =
      "org.pentaho.reporting.engine.classic.demo.resources.demo-resources";

  private static final String COMMA_SEPARATOR = ",";
  private static final String SEMICOLON_SEPARATOR = ";";
  private static final String TAB_SEPARATOR = "\t";
  private static final String CSV_FILE_EXTENSION = ".csv";

  private JTextArea txDataArea;
  private JCheckBox cbxColumnNamesAsFirstRow;

  /**
   * A radio button for tab separators.
   */
  private JRadioButton rbSeparatorTab;

  /**
   * A radio button for colon separators.
   */
  private JRadioButton rbSeparatorColon;

  /**
   * A radio button for semi-colon separators.
   */
  private JRadioButton rbSeparatorSemicolon;

  /**
   * A radio button for other separators.
   */
  private JRadioButton rbSeparatorOther;

  /**
   * A text field for the 'other' separator.
   */
  private JTextField txSeparatorOther;

  private FormValidator formValidator;
  private DemoController controller;
  private ResourceBundleSupport resources;
  private JFileChooser fileChooser;

  /**
   * Creates the demo workspace.
   */
  public CSVUserInputPanel(final DemoController controler)
  {
    this.controller = controler;
    this.resources = new ResourceBundleSupport(Locale.getDefault(), RESOURCE_BASE,
        ObjectUtilities.getClassLoader(CSVUserInputPanel.class));
    this.formValidator = new CSVDialogValidator();

    setLayout(new BorderLayout());
    add(createDataArea(), BorderLayout.CENTER);
    add(createSeparatorPanel(), BorderLayout.SOUTH);
  }

  private JComponent createDataArea()
  {
    txDataArea = new JTextArea();
    cbxColumnNamesAsFirstRow = new JCheckBox("First Row contains Column-Names");

    final JLabel lbDataArea = new JLabel("CSV-Data");
    final JLabel lbLoadData = new JLabel("Load Data from File");
    final JButton btLoadData = new JButton(new LoadCSVDataAction());

    final JPanel dataPanel = new JPanel();
    dataPanel.setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(1, 1, 1, 1);
    dataPanel.add(lbDataArea, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.insets = new Insets(1, 1, 1, 1);
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridwidth = 2;
    dataPanel.add(txDataArea, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.insets = new Insets(1, 1, 1, 1);
    dataPanel.add(lbLoadData, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.insets = new Insets(1, 1, 1, 1);
    dataPanel.add(btLoadData, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.insets = new Insets(1, 1, 1, 1);
    dataPanel.add(cbxColumnNamesAsFirstRow, gbc);

    formValidator.registerButton(cbxColumnNamesAsFirstRow);

    return dataPanel;
  }

  public DemoController getController()
  {
    return controller;
  }

  /**
   * Validates the contents of the dialog's input fields. If the selected file exists, it is also checked for validity.
   *
   * @return <code>true</code> if the input is valid, <code>false</code> otherwise
   */
  protected boolean performValidate()
  {
    final JStatusBar statusBar = controller.getStatusBar();
    statusBar.clear();

    return true;
  }

  protected FormValidator getFormValidator()
  {
    return formValidator;
  }

  /**
   * Creates a separator panel.
   *
   * @return The panel.
   */
  private JPanel createSeparatorPanel()
  {
    // separator panel
    final JPanel separatorPanel = new JPanel();
    separatorPanel.setLayout(new GridBagLayout());

    final TitledBorder tb =
        new TitledBorder(resources.getString("csvdemodialog.separatorchar"));
    separatorPanel.setBorder(tb);

    rbSeparatorTab = new JRadioButton(resources.getString("csvdemodialog.separator.tab"));
    rbSeparatorColon = new JRadioButton(resources.getString("csvdemodialog.separator.colon"));
    rbSeparatorSemicolon = new JRadioButton(resources.getString("csvdemodialog.separator.semicolon"));
    rbSeparatorOther = new JRadioButton(resources.getString("csvdemodialog.separator.other"));

    getFormValidator().registerButton(rbSeparatorColon);
    getFormValidator().registerButton(rbSeparatorOther);
    getFormValidator().registerButton(rbSeparatorSemicolon);
    getFormValidator().registerButton(rbSeparatorTab);

    final ButtonGroup btg = new ButtonGroup();
    btg.add(rbSeparatorTab);
    btg.add(rbSeparatorColon);
    btg.add(rbSeparatorSemicolon);
    btg.add(rbSeparatorOther);

    final Action selectAction = new ActionSelectSeparator();
    rbSeparatorTab.addActionListener(selectAction);
    rbSeparatorColon.addActionListener(selectAction);
    rbSeparatorSemicolon.addActionListener(selectAction);
    rbSeparatorOther.addActionListener(selectAction);

    final LengthLimitingDocument ldoc = new LengthLimitingDocument(1);
    txSeparatorOther = new JTextField();
    txSeparatorOther.setDocument(ldoc);
    getFormValidator().registerTextField(txSeparatorOther);


    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets(1, 1, 1, 1);
    separatorPanel.add(rbSeparatorTab, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.insets = new Insets(1, 1, 1, 1);
    separatorPanel.add(rbSeparatorColon, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.insets = new Insets(1, 1, 1, 1);
    separatorPanel.add(rbSeparatorSemicolon, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.insets = new Insets(1, 1, 1, 1);
    separatorPanel.add(rbSeparatorOther, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.ipadx = 120;
    gbc.insets = new Insets(1, 1, 1, 1);
    separatorPanel.add(txSeparatorOther, gbc);

    return separatorPanel;
  }

  /**
   * Enables or disables the 'other' separator text field.
   */
  protected void performSeparatorSelection()
  {
    if (rbSeparatorOther.isSelected())
    {
      txSeparatorOther.setEnabled(true);
    }
    else
    {
      txSeparatorOther.setEnabled(false);
    }
  }

  public boolean isColumnNamesAsFirstRow()
  {
    return cbxColumnNamesAsFirstRow.isSelected();
  }

  public void setColumnNamesAsFirstRow(final boolean colsAsFirstRow)
  {
    cbxColumnNamesAsFirstRow.setSelected(colsAsFirstRow);
  }


  /**
   * Selects a file to use as target for the report processing.
   */
  protected void performLoadFile()
  {
    if (fileChooser == null)
    {
      fileChooser = new JFileChooser();
      fileChooser.addChoosableFileFilter(new FilesystemFilter
          (CSV_FILE_EXTENSION, resources.getString("csvdemodialog.csv-file-description")));
      fileChooser.setMultiSelectionEnabled(false);
    }

    final int option = fileChooser.showOpenDialog(this);
    if (option == JFileChooser.APPROVE_OPTION)
    {
      final File selFile = fileChooser.getSelectedFile();
      // now load the file
      try
      {
        final BufferedReader in = new BufferedReader(new FileReader(selFile));
        final StringWriter out = new StringWriter((int) selFile.length());
        IOUtils.getInstance().copyWriter(in, out);
        in.close();
        txDataArea.setText(out.toString());
      }
      catch (IOException ioe)
      {
        controller.getStatusBar().setStatusText("Failed to load CSV file: " + ioe.getLocalizedMessage());
      }
    }
  }

  public String getData()
  {
    return txDataArea.getText();
  }

  /**
   * Returns the separator string, which is controlled by the selection of radio buttons.
   *
   * @return The separator string.
   */
  public String getSeparatorString()
  {
    if (rbSeparatorColon.isSelected())
    {
      return COMMA_SEPARATOR;
    }
    if (rbSeparatorSemicolon.isSelected())
    {
      return SEMICOLON_SEPARATOR;
    }
    if (rbSeparatorTab.isSelected())
    {
      return TAB_SEPARATOR;
    }
    if (rbSeparatorOther.isSelected())
    {
      return txSeparatorOther.getText();
    }
    return "";
  }

  /**
   * Sets the separator string.
   *
   * @param s the separator.
   */
  public void setSeparatorString(final String s)
  {
    if (s == null)
    {
      rbSeparatorOther.setSelected(true);
      txSeparatorOther.setText("");
    }
    else if (s.equals(COMMA_SEPARATOR))
    {
      rbSeparatorColon.setSelected(true);
    }
    else if (s.equals(SEMICOLON_SEPARATOR))
    {
      rbSeparatorSemicolon.setSelected(true);
    }
    else if (s.equals(TAB_SEPARATOR))
    {
      rbSeparatorTab.setSelected(true);
    }
    else
    {
      rbSeparatorOther.setSelected(true);
      txSeparatorOther.setText(s);
    }
    performSeparatorSelection();
  }
}
