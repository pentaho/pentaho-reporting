/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.ui.datasources.jdbc.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.ui.datasources.jdbc.JdbcDataSourceModule;

/**
 * @author David Kincade
 */
public class SchemaSelectionDialog extends CommonDialog
{
  private JComboBox schemaComboBox;
  private ResourceBundleSupport bundleSupport;

  public SchemaSelectionDialog(final JDialog owner, final String[] schemas)
  {
    super(owner);
    bundleSupport = new ResourceBundleSupport(Locale.getDefault(), JdbcDataSourceModule.MESSAGES,
        ObjectUtilities.getClassLoader(JdbcDataSourceModule.class));
    setTitle(bundleSupport.getString("SchemaSelectionDialog.ChooseSchema"));
    setModal(true);
    setResizable(true);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(getParent());
    setLayout(new BorderLayout());

    schemaComboBox = new JComboBox(schemas);

    super.init();
  }

  protected String getDialogId()
  {
    return "JdbcDataSourceEditor.SchemaSelection";
  }

  protected boolean hasCancelButton()
  {
    return false;
  }

  protected Component createContentPane()
  {
    final JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(schemaComboBox, BorderLayout.NORTH);
    return panel;
  }

  public String getSchema()
  {
    if (performEdit() == false)
    {
      return null;
    }
    return (String) schemaComboBox.getSelectedItem();
  }

  protected ResourceBundleSupport getBundleSupport()
  {
    return bundleSupport;
  }
}
