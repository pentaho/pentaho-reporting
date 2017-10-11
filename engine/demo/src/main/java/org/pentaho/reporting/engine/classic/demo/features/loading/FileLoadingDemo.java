/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.features.loading;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class FileLoadingDemo extends AbstractDemoHandler
{
  private class BrowseAction extends AbstractAction
  {
    private JFileChooser fileChooser;

    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private BrowseAction()
    {
      putValue(Action.NAME, "Browse");
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      if (fileChooser == null)
      {
        final FileFilter filter = new FilesystemFilter
            (new String[]{".xml", ".report", ".prpt", ".prpti"}, "Report Definitions (*.xml, *.report, *.prpt)", true);
        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setMultiSelectionEnabled(false);
      }
      if (fileChooser.showOpenDialog(panel) != JFileChooser.APPROVE_OPTION)
      {
        return;
      }
      final File selectedFile = fileChooser.getSelectedFile();
      if (selectedFile != null)
      {
        textField.setText(selectedFile.getAbsolutePath());
      }

    }
  }

  private JPanel panel;
  private JTextField textField;

  public FileLoadingDemo()
  {
    textField = new JTextField();
    textField.setColumns(40);

    panel = new JPanel();
    panel.setLayout(new BorderLayout());

    final JPanel selectCarrier = new JPanel();
    selectCarrier.add(textField, BorderLayout.CENTER);
    selectCarrier.add(new JButton(new BrowseAction()));

    panel.add(selectCarrier, BorderLayout.NORTH);
  }

  /**
   * Returns the display name of the demo.
   *
   * @return the name.
   */
  public String getDemoName()
  {
    return "File Loading Demo";
  }


  /**
   * Creates the report. For XML reports, this will most likely call the ReportGenerator, while API reports may use this
   * function to build and return a new, fully initialized report object.
   *
   * @return the fully initialized JFreeReport object.
   * @throws org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException
   *          if an error occured preventing the report definition.
   */
  public MasterReport createReport() throws ReportDefinitionException
  {
    final File file = new File (textField.getText());

    try
    {
      ResourceManager manager = new ResourceManager();
      Resource res = manager.createDirectly(file, MasterReport.class);
      return (MasterReport) res.getResource();
    }
    catch (Exception e)
    {
      throw new ReportDefinitionException("Parsing failed", e);
    }
  }

  /**
   * Returns the URL of the HTML document describing this demo.
   *
   * @return the demo description.
   */
  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("file-loading.html", FileLoadingDemo.class);
  }

  /**
   * Returns the presentation component for this demo. This component is shown before the real report generation is
   * started. Ususally it contains a JTable with the demo data and/or input components, which allow to configure the
   * report.
   *
   * @return the presentation component, never null.
   */
  public JComponent getPresentationComponent()
  {
    return panel;
  }
}
