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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.swt.demo.util;

import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * The CompoundDemoFrame provides a unified GUI which is able to present more
 * than one demo. The Demos are selectable using a Tree component.
 * <p/>
 * Creation-Date: 8/17/2008
 *
 * @author Baochuan Lu
 */
public class CompoundDemoFrame extends AbstractDemoFrame
{
  private static class VerticalSashSelectionHandler extends SelectionAdapter
  {
    private final Sash vSash;

    public VerticalSashSelectionHandler(final Sash vSash)
    {
      this.vSash = vSash;
    }

    public void widgetSelected(SelectionEvent event)
    {
      // We re-attach to the left edge, and we use the x value of the event to
      // determine the offset from the left
      ((FormData) vSash.getLayoutData()).left = new FormAttachment(0, event.x);

      // Until the parent window does a layout, the sash will not be redrawn
      // in
      // its new location.
      vSash.getParent().layout();
    }
  }

  private class TreeViewSelectionHandler implements ISelectionChangedListener
  {
    public void selectionChanged(SelectionChangedEvent event)
    {
      IStructuredSelection selection =
          (IStructuredSelection) event.getSelection();
      Object selectedDemo = selection.getFirstElement();
      if (selectedDemo instanceof DemoHandler)
      {
        setSelectedHandler((DemoHandler) selectedDemo);
      }
    }
  }

  private static class HorizontalSashSelectionHandler extends SelectionAdapter
  {
    private final Sash HSash;

    public HorizontalSashSelectionHandler(final Sash HSash)
    {
      this.HSash = HSash;
    }

    public void widgetSelected(SelectionEvent event)
    {
      // We re-attach to the left edge, and we use the x value of the event to
      // determine the offset from the left
      ((FormData) HSash.getLayoutData()).top = new FormAttachment(0, event.y);

      // Until the parent window does a layout, the sash will not be redrawn in its new location.
      HSash.getParent().layout();
    }
  }

  private static final Log logger = LogFactory.getLog(CompoundDemoFrame.class);

  private DemoHandler selectedHandler;
  private DemoSelector demoSelector;
  private Composite demoInfoPane;
  private Composite demoInfoPaneContent;
  private Composite externalHandlerArea;

  public CompoundDemoFrame(final DemoSelector demoSelector2)
  {
    this.demoSelector = demoSelector2;
  }

  public DemoSelector getDemoSelector()
  {
    return demoSelector;
  }

  /* overrides createContents in ApplicationWindow */

  protected Control createContents(final Composite parent)
  {
    /* center the shell */
    final Monitor primary = parent.getDisplay().getPrimaryMonitor();
    final Rectangle bounds = primary.getBounds();
    final Rectangle rect = parent.getBounds();
    final int x = bounds.x + (bounds.width - rect.width) / 2;
    final int y = bounds.y + (bounds.height - rect.height) / 2;
    parent.setLocation(x, y);
    parent.setSize(800, 480);

    parent.getShell().setText(demoSelector.getName());

    final Composite demoContent = new Composite(parent, SWT.COLOR_GREEN);
    final FormLayout layout = new FormLayout();
    //parent.setLayout(layout);
    demoContent.setLayout(layout);

    FormData formData = new FormData();
    formData.top = new FormAttachment(0, 0); // Attach to top
    formData.bottom = new FormAttachment(100, 0); // Attach to bottom
    formData.left = new FormAttachment(25, 0); // Attach halfway across
    final Sash vSash = new Sash(demoContent, SWT.VERTICAL | SWT.BORDER);
    vSash.setLayoutData(formData);
    vSash.addSelectionListener(new VerticalSashSelectionHandler(vSash));

    final Composite treePane = new Composite(demoContent, SWT.NONE);
    treePane.setLayout(new FillLayout());
    final TreeViewer tv = new TreeViewer(treePane);
    tv.setContentProvider(new DemoTreeContentProvider());
    tv.setLabelProvider(new DemoTreeLabelProvider());
    tv.setInput(demoSelector);
    formData = new FormData();
    formData.top = new FormAttachment(0, 0);
    formData.bottom = new FormAttachment(100, 0);
    formData.left = new FormAttachment(0, 0);
    formData.right = new FormAttachment(vSash, 0);
    treePane.setLayoutData(formData);

    tv.addSelectionChangedListener(new TreeViewSelectionHandler());

    demoInfoPane = new Composite(demoContent, SWT.NONE);
    demoInfoPane.setLayout(new FillLayout());
    formData = new FormData();
    formData.top = new FormAttachment(0, 0);
    formData.bottom = new FormAttachment(100, 0);
    formData.left = new FormAttachment(vSash, 0);
    formData.right = new FormAttachment(100, 0);
    demoInfoPane.setLayoutData(formData);
    demoInfoPaneContent = getNoHandlerInfoPane(demoInfoPane);

    return demoContent;
  }

  public DemoHandler getSelectedHandler()
  {
    return selectedHandler;
  }

  protected void setSelectedHandler(final DemoHandler handler)
  {
    selectedHandler = handler;
    if (demoInfoPaneContent != null && !demoInfoPaneContent.isDisposed())
    {
      demoInfoPaneContent.dispose();
    }

    if (handler instanceof InternalDemoHandler)
    {
      demoInfoPaneContent = createDefaultDemoPane(demoInfoPane, (InternalDemoHandler) handler);
    }
    else if (handler != null)
    {
      demoInfoPaneContent = getExternalHandlerInfoPane(demoInfoPane);
    }
    else
    {
      demoInfoPaneContent = getNoHandlerInfoPane(demoInfoPane);
    }
    demoInfoPane.layout();
  }

  protected Composite createDefaultDemoPane(final Composite parent, final InternalDemoHandler demoHandler)
  {
    final Composite demoPane = new Composite(parent, SWT.NONE);

    final FormLayout formLayout = new FormLayout();
    demoPane.setLayout(formLayout);

    final Sash HSash = new Sash(demoPane, SWT.HORIZONTAL);
    FormData formData = new FormData();
    formData.top = new FormAttachment(50, 0);
    formData.left = new FormAttachment(0, 0);
    formData.right = new FormAttachment(100, 0);
    HSash.setLayoutData(formData);
    HSash.addSelectionListener(new HorizontalSashSelectionHandler(HSash));

    final URL url = demoHandler.getDemoDescriptionSource();
    final Composite description = createDescriptionTextPane(demoPane, url);
    formData = new FormData();
    formData.top = new FormAttachment(0, 0);
    formData.bottom = new FormAttachment(HSash, 0);
    formData.left = new FormAttachment(0, 0);
    formData.right = new FormAttachment(100, 0);
    description.setLayoutData(formData);


    final Button previewButton = new Button(demoPane, SWT.None);
    final Action previewAction = getPreviewAction();
    previewButton.setText(previewAction.getText());
    previewButton.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(final SelectionEvent e)
      {
        previewAction.run();
      }
    });
    formData = new FormData();
    formData.right = new FormAttachment(100, 0);
    formData.bottom = new FormAttachment(100, 0);
    previewButton.setLayoutData(formData);

//    Composite presentation = demoHandler.getPresentationComponent(demoPane);
//    formData = new FormData();
//    formData.left    = new FormAttachment(0, 0);
//    formData.right   = new FormAttachment(100, 0);
//    formData.top = new FormAttachment(HSash, 0);
//    formData.bottom = new FormAttachment(previewButton, 0);
//    presentation.setLayoutData(formData);
    return demoPane;
  }

  protected Composite createDescriptionTextPane(final Composite parent, final URL url)
  {
    final Browser browser = new Browser(parent, SWT.NONE);

    if (url != null)
    {
      browser.setUrl(url.toString());
    }
    else
    {
      browser.setText(
          "Unable to load the demo description. No such resource.");
    }

    return browser;
  }

  protected Composite getNoHandlerInfoPane(final Composite parent)
  {
    return new Composite(parent, SWT.NONE);
  }

  protected Composite getExternalHandlerInfoPane(final Composite parent)
  {
    System.err.println("in getExternalHandlerInfoPane()");
    if (externalHandlerArea != null)
    {
      externalHandlerArea.dispose();
    }

    final Composite area = new Composite(parent, SWT.NONE);
    final FormLayout formLayout = new FormLayout();
    area.setLayout(formLayout);

    final URL url = ObjectUtilities
        .getResource(
            "org/pentaho/reporting/engine/classic/demo/resources/external-handler-info.html",
            CompoundDemoFrame.class);
    final Composite description = createDescriptionTextPane(area, url);
    FormData formData = new FormData();
    formData.top = new FormAttachment(0, 0);
    formData.left = new FormAttachment(0, 0);
    formData.right = new FormAttachment(100, 0);
    description.setLayoutData(formData);

    final Button previewButton = new Button(area, SWT.None);
    final Action previewAction = getPreviewAction();
    previewButton.setText(previewAction.getText());
    previewButton.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(final SelectionEvent e)
      {
        previewAction.run();
      }
    });
    formData = new FormData();
    formData.left = new FormAttachment(0, 0);
    formData.right = new FormAttachment(100, 0);
    formData.bottom = new FormAttachment(100, -5);
    previewButton.setLayoutData(formData);
    externalHandlerArea = area;

    return externalHandlerArea;
  }

  protected void attemptPreview(final Shell shell)
  {
    final DemoHandler selectedHandler = getSelectedHandler();
    if (selectedHandler == null)
    {
      return;
    }

    final PreviewHandler previewHandler = selectedHandler.getPreviewHandler();
    previewHandler.attemptPreview(shell);
  }

  private static class DemoTreeContentProvider implements ITreeContentProvider
  {
    private DemoTreeContentProvider()
    {
    }

    public Object[] getChildren(final Object element)
    {
      if (element instanceof DemoSelector)
      {
        final ArrayList<Object> nodes = new ArrayList<Object>();
        final DemoSelector[] selectors = ((DemoSelector) element).getChilds();
        for (int i = 0; i < selectors.length; i++)
        {
          final DemoSelector demoSelector = selectors[i];
          logger.debug(demoSelector.getName() + " is added.");
          nodes.add(demoSelector);
        }

        final DemoHandler[] handlers = ((DemoSelector) element).getDemos();
        for (int i = 0; i < handlers.length; i++)
        {
          final DemoHandler handler = handlers[i];
          logger.debug(handler.getDemoName() + " is added.");
          nodes.add(handler);
        }
        return (Object[]) nodes.toArray(new Object[nodes.size()]);
      }
      else
      {
        return new Object[0];
      }
    }

    public Object getParent(final Object element)
    {
      if (element instanceof DemoSelector)
      {
        return ((DemoSelector) element).getParent();
      }
      else
      {
        return ((DemoHandler) element).getParent();
      }
    }

    public boolean hasChildren(final Object element)
    {
      return getChildren(element).length > 0;
    }

    public Object[] getElements(final Object element)
    {
      return getChildren(element);
    }

    public void dispose()
    {
      // TODO Auto-generated method stub   
    }

    public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2)
    {
      // TODO Auto-generated method stub
    }
  }

  private static class DemoTreeLabelProvider extends LabelProvider
  {
    private DemoTreeLabelProvider()
    {
    }

    public String getText(final Object element)
    {
      if (element instanceof DemoSelector)
      {
        return ((DemoSelector) element).getName();
      }
      else
      {
        return ((DemoHandler) element).getDemoName();
      }
    }
  }
}


