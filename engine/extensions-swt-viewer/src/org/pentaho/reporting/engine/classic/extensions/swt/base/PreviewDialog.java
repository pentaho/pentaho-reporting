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

package org.pentaho.reporting.engine.classic.extensions.swt.base;

/**
 * =========================================================
 * Pentaho-Reporting-Classic : a free Java reporting library
 * =========================================================
 *
 * Project Info:  http://reporting.pentaho.org/
 *
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * PreviewDialog.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.extensions.swt.commonSWT.ReportProgressDialog;
import org.pentaho.reporting.engine.classic.extensions.swt.commonSWT.SwtUtil;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.Messages;

/**
 * Creation-Date: 8/17/2008
 * 
 * @author Baochuan Lu
 */
public class PreviewDialog extends ApplicationWindow
{
  private MasterReport report;
  private PreviewPane previewPane;
  private Messages messages;
  private ReportProgressDialog progressDialog;

  public PreviewDialog(final Shell parent, final MasterReport report)
  {
    super(parent);
    this.report = report;

    /* disabling minimize button */
    setShellStyle(SWT.BORDER | SWT.RESIZE | SWT.CLOSE | SWT.MAX);

    addMenuBar();
    addToolBar(SWT.FLAT | SWT.WRAP);
    addStatusLine();
  }

  protected void configureShell(final Shell shell)
  {
    super.configureShell(shell);
    shell.setText("Preview");

    SwtUtil.centerShellOnScreen(shell);
  }

  protected Control createContents(final Composite parent)
  {
    final ScrolledComposite scrolledComposite = new ScrolledComposite(parent,
        SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    previewPane = new PreviewPane(scrolledComposite, SWT.NONE, this);
    scrolledComposite.setContent(previewPane);
    scrolledComposite.setExpandHorizontal(true);
    scrolledComposite.setExpandVertical(true);
    scrolledComposite.setMinWidth(600);
    scrolledComposite.setMinHeight(800);
    previewPane.setReportJob(report);

    previewPane.setDeferredRepagination(true);
    final Configuration configuration = ClassicEngineBoot.getInstance()
    .getGlobalConfig();

    previewPane.addListener(SWT.Show, new Listener()
    {
      public void handleEvent(final Event event)
      {
        if (previewPane.isDeferredRepagination())
        {
          previewPane.startPagination();
        }
      }
    });

    previewPane
    .addPropertyChangeListener(new PreviewPanePropertyChangeHandler());

    final boolean progressDialogEnabled = "true".equals(configuration //$NON-NLS-1$
        .getConfigProperty("org.pentaho.reporting.engine.classic.core.modules.gui.base.ProgressDialogEnabled")); //$NON-NLS-1$
    if (progressDialogEnabled)
    {
      progressDialog = new ReportProgressDialog(getShell());
      final MasterReport reportJob = previewPane.getReportJob();
      previewPane.addReportProgressListener(progressDialog);
      if (reportJob == null || reportJob.getTitle() == null)
      {
        progressDialog.getShell().setText(
            messages.getString("ProgressDialog.EMPTY_TITLE"));
        progressDialog.setMessage(messages
            .getString("ProgressDialog.EMPTY_TITLE"));
      } else
      {
        final Shell shell = progressDialog.getShell();
        if (shell != null)
        {
          shell.setText(messages.getString("ProgressDialog.TITLE", reportJob
              .getTitle()));
        }
      }
      progressDialog.open();
    } else
    {
      progressDialog = null;
    }

    return parent;
  }

  protected ToolBarManager createToolBarManager(final int style)
  {
    return new ToolBarManager(style);
  }

  public MasterReport getReportJob()
  {
    return previewPane.getReportJob();
  }

  public PreviewPane getPreviewPane()
  {
    return previewPane;
  }

  private class PreviewPanePropertyChangeHandler implements
  PropertyChangeListener
  {
    private class CloseRunnable implements Runnable
    {
      public void run()
      {
        progressDialog.close();
      }
    }

    protected PreviewPanePropertyChangeHandler()
    {
    }

    /**
     * This method gets called when a bound property is changed.
     * 
     * @param evt
     *          A PropertyChangeEvent object describing the event source and the
     *          property that has changed.
     */

    public void propertyChange(final PropertyChangeEvent evt)
    {
      final String propertyName = evt.getPropertyName();
      final PreviewPane previewPane = getPreviewPane();

      if (PreviewPane.MENU_PROPERTY.equals(propertyName))
      {
        return;
      }

      if (PreviewPane.TITLE_PROPERTY.equals(propertyName))
      {
        Display.getCurrent().asyncExec(new Runnable()
        {
          public void run()
          {
            final String title = previewPane.getShell().getText();
            PreviewDialog.this.getShell().setText(title);
          }
        });
        return;
      }

      if (PreviewPane.STATUS_TEXT_PROPERTY.equals(propertyName)
          || PreviewPane.STATUS_TYPE_PROPERTY.equals(propertyName))
      {
        // statusBar.setStatus(previewPane.getStatusType(),
        // previewPane.getStatusText());
        return;
      }

      if (PreviewPane.ICON_THEME_PROPERTY.equals(propertyName))
      {
        // statusBar.setIconTheme(previewPane.getIconTheme());
        return;
      }

      if (PreviewPane.PAGINATING_PROPERTY.equals(propertyName))
      {
        if (Boolean.TRUE.equals(evt.getNewValue()))
        {
          if (progressDialog != null)
          {
            previewPane.addReportProgressListener(progressDialog);
            progressDialog.setOnlyPagination(true);
            final Display display = PreviewDialog.this.previewPane.getDisplay();
            if (!display.isDisposed())
            {
              display.syncExec(new Runnable()
              {
                public void run()
                {
                  progressDialog.open();
                  SwtUtil.centerDialogInParent(previewPane.getShell(),
                      progressDialog.getShell());
                }
              });
            }
          } else
          {
            System.err.println("progressDialog is null");
          }
        } else
        {
          if (progressDialog != null)
          {
            previewPane.removeReportProgressListener(progressDialog);
            progressDialog.setOnlyPagination(false);
            final Display display = PreviewDialog.this.previewPane.getDisplay();
            if (!display.isDisposed())
            {
              display.syncExec(new CloseRunnable());
            }
          }
        }
        return;
      }

      if (PreviewPane.PAGE_NUMBER_PROPERTY.equals(propertyName)
          || PreviewPane.NUMBER_OF_PAGES_PROPERTY.equals(propertyName))
      {
        final String message = previewPane.getPageNumber() + "/"
        + previewPane.getNumberOfPages();
        PreviewDialog.this.previewPane.getDisplay().syncExec(new Runnable()
        {
          public void run()
          {
            PreviewDialog.this.setStatus(message);
          }
        });
        return;
      }

      if (PreviewPane.CLOSED_PROPERTY.equals(propertyName))
      {
        if (previewPane.isClosed())
        {
          PreviewDialog.this.close();
        } else
        {
          //setVisible(true);
        }
      }
    }
  }
}
