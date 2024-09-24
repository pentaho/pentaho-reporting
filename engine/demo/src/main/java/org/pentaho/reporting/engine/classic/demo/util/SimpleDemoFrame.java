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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class SimpleDemoFrame extends AbstractDemoFrame
{
  private InternalDemoHandler demoHandler;

  public SimpleDemoFrame(final InternalDemoHandler demoHandler)
  {
    this.demoHandler = demoHandler;
  }

  protected InternalDemoHandler getDemoHandler()
  {
    return demoHandler;
  }

  public void init()
  {
    final InternalDemoHandler demoHandler = getDemoHandler();
    setTitle(demoHandler.getDemoName());
    setJMenuBar(createMenuBar());
    setContentPane(createDefaultContentPane());
  }

  protected JComponent createDefaultContentPane()
  {
    final JPanel content = new JPanel(new BorderLayout());
    content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

    final InternalDemoHandler demoHandler = getDemoHandler();
    final JEditorPane editorPane = new JEditorPane();
    final URL url = demoHandler.getDemoDescriptionSource();
    editorPane.setEditable(false);
    editorPane.setPreferredSize(new Dimension(400, 200));
    if (url != null)
    {
      try
      {
        editorPane.setPage(url);
      }
      catch (IOException e)
      {
        editorPane.setText("Unable to load the demo description. Error: " + e.getMessage());
      }
    }
    else
    {
      editorPane.setText("Unable to load the demo description. No such resource.");
    }

    final JScrollPane scroll = new JScrollPane(editorPane,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    final JButton previewButton = new JButton(getPreviewAction());

    final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    splitPane.setTopComponent(scroll);
    splitPane.setBottomComponent(demoHandler.getPresentationComponent());
    content.add(splitPane, BorderLayout.CENTER);
    content.add(previewButton, BorderLayout.SOUTH);
    return content;
  }

  /**
   * Handler method called by the preview action. This method should perform all operations to preview the report.
   */
  protected void attemptPreview()
  {
    final InternalDemoHandler demoHandler = getDemoHandler();
    final PreviewHandler previewHandler = demoHandler.getPreviewHandler();
    previewHandler.attemptPreview();
  }
}
