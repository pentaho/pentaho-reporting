/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
