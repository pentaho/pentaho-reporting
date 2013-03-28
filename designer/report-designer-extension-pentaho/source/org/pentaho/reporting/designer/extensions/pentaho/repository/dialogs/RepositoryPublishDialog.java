package org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfPageableModule;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.PlainTextPageableModule;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVTableModule;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlTableModule;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.RTFTableModule;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelTableModule;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;
import org.pentaho.reporting.libraries.pensol.WebSolutionFileObject;

public class RepositoryPublishDialog extends RepositoryOpenDialog
{

  private class NewFolderAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private NewFolderAction()
    {
      final URL location = RepositoryTreeDialog.class.getResource
          ("/org/pentaho/reporting/designer/extensions/pentaho/repository/resources/newfolder.png");
      if (location != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(location));
      }
      else
      {
        putValue(Action.NAME,
            Messages.getInstance().getString("SolutionRepositoryTreeDialog.NewFolderAction.Name"));
      }
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      final CreateNewRepositoryFolderDialog newFolderDialog =
              new CreateNewRepositoryFolderDialog(RepositoryPublishDialog.this);

      if (!newFolderDialog.performEdit())
      {
        return;
      }

      final FileObject treeNode = getSelectedView();
      if (treeNode == null)
      {
        return;
      }

      if (!StringUtils.isEmpty(newFolderDialog.getName()))
      {
        final Component glassPane = SwingUtilities.getRootPane(RepositoryPublishDialog.this).getGlassPane();
        try
        {
          glassPane.setVisible(true);
          glassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
          final FileObject child = treeNode.resolveFile(newFolderDialog.getFolderName());
          child.createFolder();
          if (child instanceof WebSolutionFileObject)
          {
            final WebSolutionFileObject webSolutionFileObject = (WebSolutionFileObject) child;
            webSolutionFileObject.setDescription(newFolderDialog.getDescription());
          }
          getTable().refresh();
        }
        catch (Exception e1)
        {
          UncaughtExceptionsModel.getInstance().addException(e1);
        }
        finally
        {
          glassPane.setVisible(false);
          glassPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
      }
    }
  }

  private class FileSelectionHandler implements ListSelectionListener
  {

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(final ListSelectionEvent e)
    {
      final int selectedRow = getTable().getSelectedRow();
      if (selectedRow == -1)
      {
        return;
      }

      final FileObject selectedFileObject = getTable().getSelectedFileObject(selectedRow);
      if (selectedFileObject == null)
      {
        return;
      }

      try
      {
        if (selectedFileObject.getType() == FileType.FILE)
        {
          getFileNameTextField().setText(selectedFileObject.getName().getBaseName());
        }
      }
      catch (FileSystemException e1)
      {
        // ignore ..
      }
    }
  }

  private static final String REPORT_BUNDLE_EXTENSION = ".prpt";

  private JTextField desciptionTextField;
  private JTextField titleTextField;
  private KeyedComboBoxModel<String,String> exportFormatModel;
  private JCheckBox lockOutputTypeCheckBox;

  public RepositoryPublishDialog()
  {
    super();
  }

  public RepositoryPublishDialog(final Frame owner)
  {
    super(owner);
  }

  public RepositoryPublishDialog(final Dialog owner)
  {
    super(owner);
  }

  public void init()
  {
    exportFormatModel = createExportTypeModel();

    desciptionTextField = new JTextField();
    titleTextField = new JTextField();

    lockOutputTypeCheckBox = new JCheckBox(Messages.getInstance().getString("RepositoryPublishDialog.Lock"));
    super.init();

    getTable().getSelectionModel().addListSelectionListener(new FileSelectionHandler());
  }

  protected JPanel createHeaderPanel()
  {
    final JPanel publishHeaderPanel = new JPanel(new GridBagLayout());

    final GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(5, 5, 5, 5);
    publishHeaderPanel.add(new JLabel(Messages.getInstance().getString("RepositoryPublishDialog.ReportName")), c);

    c.gridy = 1;
    c.insets = new Insets(2, 5, 0, 5);
    publishHeaderPanel.add(getFileNameTextField(), c);

    c.gridy = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(5, 5, 5, 5);
    publishHeaderPanel.add(new JLabel(Messages.getInstance().getString("RepositoryPublishDialog.ReportTitle")), c);

    c.gridy = 3;
    c.insets = new Insets(2, 5, 0, 5);
    publishHeaderPanel.add(titleTextField, c);

    c.gridy = 4;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(5, 5, 5, 5);
    publishHeaderPanel.add(new JLabel(Messages.getInstance().getString("RepositoryPublishDialog.ReportDescription")), c);

    c.gridy = 5;
    c.insets = new Insets(2, 5, 0, 5);
    publishHeaderPanel.add(desciptionTextField, c);

    c.gridy = 6;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(2, 5, 0, 5);
    publishHeaderPanel.add(new JLabel(Messages.getInstance().getString("RepositoryPublishDialog.Location")), c);

    c.insets = new Insets(0, 0, 0, 0);
    c.gridx = 0;
    c.gridy = 7;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    c.anchor = GridBagConstraints.WEST;
    publishHeaderPanel.add(createLocationFieldPanel(), c);
    return publishHeaderPanel;
  }


  protected JPanel createLocationFieldPanel()
  {
    final GridBagConstraints c = new GridBagConstraints();
    final JPanel locationFieldPanel = new JPanel();
    locationFieldPanel.setLayout(new GridBagLayout());
    c.insets = new Insets(0, 5, 5, 0);
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    c.anchor = GridBagConstraints.WEST;
    locationFieldPanel.add(getLocationCombo(), c);

    c.insets = new Insets(5, 8, 5, 0);
    c.gridx = 1;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0.0;
    c.anchor = GridBagConstraints.WEST;
    locationFieldPanel.add(new BorderlessButton(new LevelUpAction()), c);

    c.gridx = 2;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0.0;
    c.anchor = GridBagConstraints.EAST;
    locationFieldPanel.add(new BorderlessButton(new BrowseRepositoryAction()), c);

    c.gridx = 3;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0.0;
    c.anchor = GridBagConstraints.EAST;
    locationFieldPanel.add(new BorderlessButton(new NewFolderAction()), c);
    return locationFieldPanel;
  }

  private KeyedComboBoxModel<String,String> createExportTypeModel()
  {
    final KeyedComboBoxModel<String,String> keyedComboBoxModel = new KeyedComboBoxModel<String,String>();
    keyedComboBoxModel.add(null, null);
    keyedComboBoxModel.add(PdfPageableModule.PDF_EXPORT_TYPE, Messages.getInstance().getString("RepositoryPublishDialog.ExportType.PDF"));
    keyedComboBoxModel.add(HtmlTableModule.TABLE_HTML_STREAM_EXPORT_TYPE, Messages.getInstance().getString("RepositoryPublishDialog.ExportType.HTMLStream"));
    keyedComboBoxModel.add(ExcelTableModule.EXCEL_FLOW_EXPORT_TYPE, Messages.getInstance().getString("RepositoryPublishDialog.ExportType.XLS"));
    keyedComboBoxModel.add(ExcelTableModule.XLSX_FLOW_EXPORT_TYPE, Messages.getInstance().getString("RepositoryPublishDialog.ExportType.XLSX"));
    keyedComboBoxModel.add(CSVTableModule.TABLE_CSV_STREAM_EXPORT_TYPE, Messages.getInstance().getString("RepositoryPublishDialog.ExportType.CSV"));
    keyedComboBoxModel.add(RTFTableModule.TABLE_RTF_FLOW_EXPORT_TYPE, Messages.getInstance().getString("RepositoryPublishDialog.ExportType.RTF"));
    keyedComboBoxModel.add(PlainTextPageableModule.PLAINTEXT_EXPORT_TYPE, Messages.getInstance().getString("RepositoryPublishDialog.ExportType.TEXT"));
    keyedComboBoxModel.add(HtmlTableModule.TABLE_HTML_PAGE_EXPORT_TYPE, Messages.getInstance().getString("RepositoryPublishDialog.ExportType.HTMLPage"));
    return keyedComboBoxModel;
  }

  private JPanel createPublishSettingsPanel()
  {
    final JComboBox fileFormat = new JComboBox(exportFormatModel);

    final GridBagConstraints c = new GridBagConstraints();
    final JPanel publishSettingsPanel = new JPanel(new GridBagLayout());

    c.insets = new Insets(5, 5, 0, 5);
    c.gridwidth = 1;
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    publishSettingsPanel.add(new JLabel(Messages.getInstance().getString("RepositoryPublishDialog.OutputType")), c);

    c.insets = new Insets(5, 5, 5, 0);
    c.gridwidth = 1;
    c.gridx = 0;
    c.gridy = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    c.weightx = 0.0;
    publishSettingsPanel.add(fileFormat, c);

    c.insets = new Insets(0, 5, 5, 5);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.WEST;
    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 1.0;
    publishSettingsPanel.add(lockOutputTypeCheckBox, c);
    return publishSettingsPanel;
  }

  public void setExportType(final String exportType)
  {
    exportFormatModel.setSelectedKey(exportType);
  }

  public String getExportType()
  {
    return exportFormatModel.getSelectedKey();
  }

  public void setReportTitle(final String title)
  {
    titleTextField.setText(title);
  }

  public String getReportTitle()
  {
    if (StringUtils.isEmpty(titleTextField.getText()))
    {
      return null;
    }
    return titleTextField.getText();
  }

  public void setDescription(final String description)
  {
    desciptionTextField.setText(description);
  }

  public String getDescription()
  {
    if (StringUtils.isEmpty(desciptionTextField.getText()))
    {
      return null;
    }
    return desciptionTextField.getText();
  }

  public void setLockOutputType(final boolean lock)
  {
    lockOutputTypeCheckBox.setSelected(lock);
  }

  public boolean isLockOutputType()
  {
    return lockOutputTypeCheckBox.isSelected();
  }

  protected Component createContentPane()
  {
    final Container contentPane = (Container) super.createContentPane();
    contentPane.add(createPublishSettingsPanel(), BorderLayout.SOUTH);
    return contentPane;
  }

  protected boolean validateInputs(final boolean onConfirm)
  {
    if (super.validateInputs(onConfirm) == false)
    {
      return false;
    }

    if (onConfirm == false)
    {
      return true;
    }

    final String reportName = getFileNameTextField().getText();
    if (StringUtils.isEmpty(reportName) == false &&
        reportName.endsWith(REPORT_BUNDLE_EXTENSION) == false)
    {
      final String safeReportName = reportName + REPORT_BUNDLE_EXTENSION;
      getFileNameTextField().setText(safeReportName);
    }

    try
    {
      final FileObject selectedView = getSelectedView();
      final String validateName = getSelectedFile();
      if (validateName == null || selectedView == null)
      {
        return false;
      }

      final FileObject targetFile = selectedView.resolveFile(getFileNameTextField().getText());
      final FileObject fileObject = selectedView.getFileSystem().resolveFile(targetFile.getName());
      if (fileObject.getType() == FileType.IMAGINARY)
      {
        return true;
      }

      final int result = JOptionPane.showConfirmDialog(this,
          Messages.getInstance().formatMessage("PublishToServerAction.FileExistsOverride", validateName),
          Messages.getInstance().getString("PublishToServerAction.Information.Title"), JOptionPane.YES_NO_OPTION);
      return result == JOptionPane.YES_OPTION;
    }
    catch (FileSystemException fse)
    {
      UncaughtExceptionsModel.getInstance().addException(fse);
      return false;
    }
  }

  protected boolean isCreateFolderAllowed()
  {
    return true;
  }

  protected boolean isDoubleClickConfirmsDialog()
  {
    return false;
  }
}
