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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.internationalisation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DefaultResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.DefaultReportController;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;

public class LocaleSelectionReportController extends DefaultReportController
{
  private static final Log logger = LogFactory.getLog(LocaleSelectionReportController.class);

  private class UpdateAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    protected UpdateAction()
    {
      putValue(Action.NAME, "Update");
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      final PreviewPane base = getPreviewPane();
      if (base == null)
      {
        return;
      }
      final MasterReport report = base.getReportJob();
      final DefaultResourceBundleFactory rfact =
          new DefaultResourceBundleFactory(getSelectedLocale());
      report.setResourceBundleFactory(rfact);
      try
      {
        base.setReportJob(report);
      }
      catch (Exception ex)
      {
        logger.error("Unable to refresh the report.", ex);
      }
    }
  }

  private KeyedComboBoxModel localesModel;
  private PreviewPane previewPane;

  public LocaleSelectionReportController()
  {
    final UpdateAction updateAction = new UpdateAction();
    localesModel = createLocalesModel();

    final JComboBox cbx = new JComboBox(localesModel);
    setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    add(new JLabel("Select locale:"), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(cbx, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.EAST;
    add(new JButton(updateAction));

  }

  private KeyedComboBoxModel createLocalesModel()
  {
    final KeyedComboBoxModel cn = new KeyedComboBoxModel();
    final Locale[] locales = Locale.getAvailableLocales();
    for (int i = 0; i < locales.length; i++)
    {
      final Locale locale = locales[i];
      cn.add(locale, locale.getDisplayName());
    }
    cn.setSelectedKey(Locale.getDefault());
    return cn;
  }

  public Locale getSelectedLocale()
  {
    final Locale l = (Locale) localesModel.getSelectedKey();
    if (l == null)
    {
      return Locale.getDefault();
    }
    return l;
  }

  public void setSelectedLocale(final Locale locale)
  {
    if (locale == null)
    {
      throw new NullPointerException();
    }
    localesModel.setSelectedKey(locale);
  }

  public void initialize(final PreviewPane pane)
  {
    super.initialize(pane);
    this.previewPane = pane;
  }

  /**
   * Called when the report controller gets removed.
   *
   * @param pane
   */
  public void deinitialize(final PreviewPane pane)
  {
    super.deinitialize(pane);
    this.previewPane = null;
  }

  public PreviewPane getPreviewPane()
  {
    return previewPane;
  }
}
