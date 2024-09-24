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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.opensource;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewInternalFrame;
import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoFrame;
import org.pentaho.reporting.engine.classic.demo.util.DemoController;
import org.pentaho.reporting.engine.classic.demo.util.PreviewHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * A demo to show the usage of the InteralPreviewFrame. It shows a report on a primitive desktop.
 *
 * @author Thomas Morgner
 */
public class InternalFrameDemoHandler extends OpenSourceXMLDemoHandler
{
  private class InternalFramePreviewHandler implements PreviewHandler
  {
    protected InternalFramePreviewHandler()
    {
    }

    public void attemptPreview()
    {
      try
      {
        final MasterReport report = createReport();

        final PreviewInternalFrame frame = new PreviewInternalFrame(report);
        frame.setClosable(true);
        frame.setResizable(true);
        frame.setToolbarFloatable(false);
        getDesktop().add(frame);
        frame.pack();
        frame.setVisible(true);
        frame.requestFocus();
      }
      catch (ReportDefinitionException e)
      {
        AbstractDemoFrame.showExceptionDialog(desktop, "report.definitionfailure", e);
      }
    }
  }

  /**
   * The data for the report.
   */
  private final TableModel data;
  /**
   * The desktop pane.
   */
  private JDesktopPane desktop;
  private PreviewHandler previewHandler;

  /**
   * Constructs the demo application.
   */
  public InternalFrameDemoHandler()
  {
    this.data = new OpenSourceProjects();
    this.previewHandler = new InternalFramePreviewHandler();
  }

  public String getDemoName()
  {
    return "Internal Frame Demo (External)";
  }

  public synchronized JComponent getPresentationComponent()
  {
    return getDesktop();
  }

  protected JDesktopPane getDesktop()
  {
    if (desktop == null)
    {
      desktop = init(getController());
    }
    return desktop;
  }

  /**
   * Creates the content for the application frame.
   *
   * @return a panel containing the basic user interface.
   */
  private JDesktopPane init(final DemoController ctrl)
  {
    final JPanel content = new JPanel(new BorderLayout());
    content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    content.add(createDefaultTable(data));
    content.add(new JButton(ctrl.getExportAction()), BorderLayout.SOUTH);

    final JInternalFrame frame = new JInternalFrame();
    frame.setClosable(false);
    frame.setVisible(true);
    frame.setContentPane(content);
    frame.pack();

    final JDesktopPane desktop = new JDesktopPane();
    desktop.setDoubleBuffered(false);
    desktop.add(frame);
    return desktop;
  }

  public PreviewHandler getPreviewHandler()
  {
    return previewHandler;
  }

  /**
   * Entry point for running the demo application...
   *
   * @param args ignored.
   */
  public static void main(final String[] args)
  {
    // initialize JFreeReport
    ClassicEngineBoot.getInstance().start();

    final InternalFrameDemoHandler handler = new InternalFrameDemoHandler();
    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);
  }
}
