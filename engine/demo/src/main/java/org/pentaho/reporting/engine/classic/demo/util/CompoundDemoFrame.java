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
import java.awt.Container;
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
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * The CompoundDemoFrame provides a unified GUI which is able to present more than one demo. The Demos are selectable
 * using a JTree component.
 *
 * @author Thomas Morgner
 */
public class CompoundDemoFrame extends AbstractDemoFrame
{
  private static final Log logger = LogFactory.getLog(CompoundDemoFrame.class);

  private class TreeSelectionHandler implements TreeSelectionListener
  {
    public TreeSelectionHandler()
    {
    }

    public void valueChanged(TreeSelectionEvent e)
    {
      final TreePath treePath = e.getNewLeadSelectionPath();
      if (treePath == null)
      {
        setSelectedHandler(null);
      }
      else
      {
        final Object o = treePath.getLastPathComponent();
        if (o instanceof DemoHandlerTreeNode)
        {
          DemoHandlerTreeNode handlerNode = (DemoHandlerTreeNode) o;
          DemoHandler handler = handlerNode.getHandler();
          setSelectedHandler(handler);
        }
        else
        {
          setSelectedHandler(null);
        }
      }
    }

  }

  private DemoHandler selectedHandler;
  private DemoSelector demoSelector;
  private JPanel demoContent;
  private JComponent externalHandlerArea;

  public CompoundDemoFrame(final DemoSelector demoSelector)
  {
    this.demoSelector = demoSelector;
  }

  public DemoSelector getDemoSelector()
  {
    return demoSelector;
  }

  protected void init()
  {
    setTitle(demoSelector.getName());
    setJMenuBar(createMenuBar());
    setContentPane(createDefaultContentPane());
  }

  protected void setSelectedHandler(final DemoHandler handler)
  {
    selectedHandler = handler;
    demoContent.removeAll();
    if (handler instanceof InternalDemoHandler)
    {
      demoContent.add(createDefaultDemoPane((InternalDemoHandler) handler));
      getPreviewAction().setEnabled(true);
    }
    else if (handler != null)
    {
      demoContent.add(getExternalHandlerInfoPane());
      getPreviewAction().setEnabled(true);
    }
    else
    {
      demoContent.add(getNoHandlerInfoPane());
      getPreviewAction().setEnabled(false);
    }
    demoContent.revalidate();
  }

  protected JComponent getExternalHandlerInfoPane()
  {
    if (externalHandlerArea == null)
    {
      final JPanel content = new JPanel(new BorderLayout());
      content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

      final URL url = ObjectUtilities.getResource
          ("org/pentaho/reporting/engine/classic/demo/resources/external-handler-info.html", CompoundDemoFrame.class);
      final JComponent scroll = createDescriptionTextPane(url);
      final JButton previewButton = new JButton(getPreviewAction());
      content.add(scroll, BorderLayout.CENTER);
      content.add(previewButton, BorderLayout.SOUTH);
      externalHandlerArea = content;
    }
    return externalHandlerArea;
  }

  protected JComponent createDescriptionTextPane(final URL url)
  {
    final JEditorPane editorPane = new JEditorPane();
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
        logger.error("Failed to load demo description", e);
        editorPane.setText("Unable to load the demo description. Error: " + e
            .getMessage());
      }
    }
    else
    {
      editorPane.setText(
          "Unable to load the demo description. No such resource.");
    }

    return new JScrollPane(editorPane,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
  }

  protected JComponent getNoHandlerInfoPane()
  {
    return new JPanel();
  }

  public DemoHandler getSelectedHandler()
  {
    return selectedHandler;
  }

  protected Container createDefaultContentPane()
  {

    demoContent = new JPanel();
    demoContent.setLayout(new BorderLayout());
    demoContent.setMinimumSize(new Dimension(100, 100));
    demoContent.add(getNoHandlerInfoPane(), BorderLayout.CENTER);

    JPanel placeHolder = new JPanel();
    placeHolder.setMinimumSize(new Dimension(300, 0));
    placeHolder.setPreferredSize(new Dimension(300, 0));
    placeHolder.setMaximumSize(new Dimension(300, 0));

    JPanel rootContent = new JPanel();
    rootContent.setLayout(new BorderLayout());
    rootContent.add(demoContent, BorderLayout.CENTER);
    rootContent.add(placeHolder, BorderLayout.NORTH);

    final DemoSelectorTreeNode root = new DemoSelectorTreeNode(null,
        demoSelector);
    final DefaultTreeModel model = new DefaultTreeModel(root);
    final JTree demoTree = new JTree(model);
    demoTree.addTreeSelectionListener(new TreeSelectionHandler());

    JSplitPane rootSplitPane =
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(demoTree), rootContent);
    rootSplitPane.setContinuousLayout(true);
    rootSplitPane.setDividerLocation(200);
    rootSplitPane.setOneTouchExpandable(true);
    return rootSplitPane;
  }

  protected JComponent createDefaultDemoPane(final InternalDemoHandler demoHandler)
  {
    final JPanel content = new JPanel(new BorderLayout());
    content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

    final URL url = demoHandler.getDemoDescriptionSource();
    final JComponent scroll = createDescriptionTextPane(url);

    final JButton previewButton = new JButton(getPreviewAction());

    final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    splitPane.setTopComponent(scroll);
    splitPane.setBottomComponent(demoHandler.getPresentationComponent());
    splitPane.setDividerLocation(200);
    splitPane.setOneTouchExpandable(true);
    content.add(splitPane, BorderLayout.CENTER);
    content.add(previewButton, BorderLayout.SOUTH);
    return content;
  }

  protected void attemptPreview()
  {
    final DemoHandler selectedHandler = getSelectedHandler();
    if (selectedHandler == null)
    {
      return;
    }

    final PreviewHandler previewHandler = selectedHandler.getPreviewHandler();
    previewHandler.attemptPreview();
  }
}
