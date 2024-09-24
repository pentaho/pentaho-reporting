package org.pentaho.reporting.ui.datasources.jdbc.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.ui.datasources.jdbc.JdbcDataSourceModule;

public class JdbcSecurityDialog extends CommonDialog
{
  private JComboBox jdbcUserFieldBox;
  private JComboBox jdbcPasswordFieldBox;
  private ResourceBundleSupport bundleSupport;

  public JdbcSecurityDialog(final DesignTimeContext context)
      throws HeadlessException
  {
    init(context);
  }

  public JdbcSecurityDialog(final Frame owner, final DesignTimeContext context)
      throws HeadlessException
  {
    super(owner);
    init(context);
  }

  public JdbcSecurityDialog(final Dialog owner, final DesignTimeContext context)
      throws HeadlessException
  {
    super(owner);
    init(context);
  }

  protected void init(final DesignTimeContext context)
  {
    this.bundleSupport = new ResourceBundleSupport(Locale.getDefault(), JdbcDataSourceModule.MESSAGES,
        ObjectUtilities.getClassLoader(JdbcDataSourceModule.class));

    setTitle(bundleSupport.getString("JdbcSecurityDialog.Title"));

    final String[] reportFields = context.getDataSchemaModel().getColumnNames();
    jdbcPasswordFieldBox = new JComboBox(reportFields);
    jdbcPasswordFieldBox.setEditable(true);

    jdbcUserFieldBox = new JComboBox(reportFields);
    jdbcUserFieldBox.setEditable(true);

    super.init();
  }

  protected String getDialogId()
  {
    return "JdbcDataSourceEditor.Security";
  }

  protected Component createContentPane()
  {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets(5, 5, 5, 5);
    contentPane.add(new JLabel(bundleSupport.getString("JdbcSecurityDialog.JDBCUser.FieldValue")), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    contentPane.add(jdbcUserFieldBox, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets(5, 5, 5, 5);
    contentPane.add(new JLabel(bundleSupport.getString("JdbcSecurityDialog.JDBCPassword.FieldValue")), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPane.add(jdbcPasswordFieldBox, gbc);
    return contentPane;
  }

  public String getJdbcUserField()
  {
    final Object o = jdbcUserFieldBox.getSelectedItem();
    if (o instanceof String == false)
    {
      return null;
    }
    final String field = (String) o;
    if (StringUtils.isEmpty(field))
    {
      return null;
    }
    return field;
  }

  public void setJdbcUserField(final String jdbcUserField)
  {
    jdbcUserFieldBox.setSelectedItem(jdbcUserField);
  }

  public String getJdbcPasswordField()
  {
    final Object o = jdbcPasswordFieldBox.getSelectedItem();
    if (o instanceof String == false)
    {
      return null;
    }
    final String field = (String) o;
    if (StringUtils.isEmpty(field))
    {
      return null;
    }
    return field;
  }

  public void setJdbcPasswordField(final String jdbcPasswordField)
  {
    jdbcPasswordFieldBox.setSelectedItem(jdbcPasswordField);
  }

  public boolean performEdit()
  {
    return super.performEdit();
  }
}
