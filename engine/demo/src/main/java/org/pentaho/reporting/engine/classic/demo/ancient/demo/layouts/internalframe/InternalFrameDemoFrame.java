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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts.internalframe;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.functions.PaintComponentTableModel;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts.ComponentDrawingDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Creation-Date: 11.12.2005, 12:49:55
 *
 * @author Thomas Morgner
 */
public class InternalFrameDemoFrame extends AbstractDemoFrame
{
  private static final Log logger = LogFactory.getLog(InternalFrameDemoFrame.class);

  private class NewFrameAction extends AbstractAction
  {
    protected NewFrameAction()
    {
      putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
      putValue(Action.NAME, "New");
      putValue(Action.ACCELERATOR_KEY,
          KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      final JInternalFrame frame = new DocumentInternalFrame();
      frame.setSize(400, 300);
      frame.setVisible(true); //necessary as of 1.3
      desktop.add(frame);
      try
      {
        frame.setSelected(true);
      }
      catch (PropertyVetoException ex)
      {
        // ignore exception ..
      }
    }
  }

  private JDesktopPane desktop;

  public InternalFrameDemoFrame()
  {
    setTitle("InternalFrameDemo");

    setJMenuBar(createMenuBar());

    desktop = new JDesktopPane();
    setContentPane(desktop);
  }


  /**
   * Computes the maximum bounds of the current screen device. If this method is called on JDK 1.4, Xinerama-aware
   * results are returned. (See Sun-Bug-ID 4463949 for details).
   *
   * @return the maximum bounds of the current screen.
   */
  private static Rectangle getMaximumWindowBounds()
  {
    try
    {
      final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
      return localGraphicsEnvironment.getMaximumWindowBounds();
    }
    catch (Exception e)
    {
      // ignore ... will fail if this is not a JDK 1.4 ..
    }

    final Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
    return new Rectangle(0, 0, s.width, s.height);
  }

  public void updateFrameSize(final int inset)
  {
    final Rectangle screenSize = getMaximumWindowBounds();
    setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
  }

  protected JMenuBar createMenuBar()
  {
    final JMenuBar menuBar = new JMenuBar();

//Set up the lone menu.
    final JMenu menu = new JMenu("Document");
    menu.setMnemonic(KeyEvent.VK_D);
    menuBar.add(menu);

    menu.add(new JMenuItem(new NewFrameAction()));
    menu.add(new JMenuItem(getPreviewAction()));
    menu.addSeparator();
    menu.add(new JMenuItem(getCloseAction()));

    final JMenu helpmenu = new JMenu("Help");
    helpmenu.setMnemonic(KeyEvent.VK_H);
    helpmenu.add(new JMenuItem(getAboutAction()));
    return menuBar;
  }


  /**
   * Handler method called by the preview action. This method should perform all operations to preview the report.
   */
  protected void attemptPreview()
  {
    final JInternalFrame frame = findSelectedFrame();
    if (frame == null)
    {
      return;
    }
    final Rectangle bounds = frame.getBounds();
    final Container parent = frame.getParent();
    final boolean visible = frame.isVisible();
    final int layer = frame.getLayer();

    // now print ..
    previewReport(frame);

    if (parent != null)
    {
      if (frame.getParent() != parent)
      {
        frame.getParent().remove(frame);
        parent.add(frame);
      }
    }
    frame.setBounds(bounds);
    frame.setVisible(visible);
    frame.setLayer(new Integer(layer));
  }


  protected void previewReport(final JInternalFrame frame)
  {
    try
    {
      final URL in = ObjectUtilities.getResourceRelative
          ("component-drawing.xml", ComponentDrawingDemoHandler.class);
      if (in == null)
      {
        return;
      }
      final ResourceManager mgr = new ResourceManager();
      final Resource resource = mgr.createDirectly(in, MasterReport.class);
      final MasterReport report = (MasterReport) resource.getResource();
      report.getReportConfiguration().setConfigProperty
          ("org.pentaho.reporting.engine.classic.core.AllowOwnPeerForComponentDrawable", "true");
      final PaintComponentTableModel tableModel = new PaintComponentTableModel();
      tableModel.addComponent(frame);
      report.setDataFactory(new TableDataFactory("default", tableModel));

      // Important: The dialog must be modal, so that we know, when the report
      // processing is finished.
      final PreviewDialog previewDialog = new PreviewDialog(report, this, true);
      previewDialog.setToolbarFloatable(true);
      previewDialog.pack();
      LibSwingUtil.positionFrameRandomly(previewDialog);
      previewDialog.setVisible(true);
    }
    catch (Exception e)
    {
      logger.error("Failed to parse the report definition", e);
    }
  }


  private JInternalFrame findSelectedFrame()
  {
    final JInternalFrame[] frames = desktop.getAllFrames();
    for (int i = 0; i < frames.length; i++)
    {
      final JInternalFrame frame = frames[i];
      if (frame.isSelected())
      {
        return frame;
      }
    }
    return null;
  }
}
