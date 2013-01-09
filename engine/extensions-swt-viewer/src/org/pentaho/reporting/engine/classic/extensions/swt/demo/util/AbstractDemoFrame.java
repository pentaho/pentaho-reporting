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

import java.util.Locale;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.extensions.swt.base.about.AboutDialog;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.engine.classic.demo.ClassicEngineDemoInfo;

/**
 * The AbstractDemoFrame provides some basic functionality shared among all
 * demos. It provides default handlers for preview and the window-closing event
 * as well as helper function to display error messages.
 *
 * Creation-Date: 8/17/2008
 *
 * @author Baochuan Lu
 */
public abstract class AbstractDemoFrame extends ApplicationWindow implements DemoController
{
  public static final String EMBEDDED_KEY = "org.pentaho.reporting.engine.classic.demo.Embedded";
  
//  public static Shell rootShell = null;

  protected class CloseAction extends Action
  {
    private CloseAction()
    {
      setText(resources.getString("action.close.name"));
      setToolTipText(resources.getString("action.close.description"));
    }
    
    public void run()
    {
      AbstractDemoFrame.this.close();
    }
  }
 
  protected class PreviewAction extends Action
  {
    private PreviewAction()
    {
      setText(resources.getString("action.print-preview.name"));
      setToolTipText(resources.getString("action.print-preview.description"));
    }

    public void run()
    {
      attemptPreview(getShell());
    }
  }

  private class AboutAction extends Action
  {
    private AboutAction()
    {
      setText(resources.getString("action.about.name"));
      setToolTipText(resources.getString("action.about.description"));
    }
    
    public void run()
    {
      displayAbout();
    }
  }

  /** The base resource class. */
  public static final String RESOURCE_BASE =
          "org.pentaho.reporting.engine.classic.demo.resources.demo-resources";

  /** Localized resources. */
  private ResourceBundleSupport resources;

  /** The close action is called when closing the frame. */
  private Action closeAction;

  /** The preview action is called when the user chooses to preview the report. */
  private Action previewAction;

  /** About action. */
  private Action aboutAction;

  private boolean ignoreEmbeddedConfig;

  /**
   * Constructs a new frame that is initially invisible.
   * <p/>
   * This constructor sets the component's locale property to the value returned
   * by <code>JComponent.getDefaultLocale</code>.
   */
  protected AbstractDemoFrame()
  {
    super(null);
    resources = new ResourceBundleSupport(Locale.getDefault(), RESOURCE_BASE,
        ObjectUtilities.getClassLoader(AbstractDemoFrame.class));
    previewAction = new PreviewAction();
    closeAction = new CloseAction();
    aboutAction = new AboutAction();
    
    addMenuBar();
    addToolBar(SWT.FLAT | SWT.WRAP);
    addStatusLine();
  }

  public void setIgnoreEmbeddedConfig(final boolean ignoreEmbeddedConfig)
  {
    this.ignoreEmbeddedConfig = ignoreEmbeddedConfig;
  }

  public boolean isIgnoreEmbeddedConfig()
  {
    return ignoreEmbeddedConfig;
  }

  /**
   * Returns the resource bundle for this demo frame.
   *
   * @return the resource bundle for the localization.
   */
  public ResourceBundleSupport getResources()
  {
    return resources;
  }

  /**
   * Returns the close action implementation to handle the closing of the
   * frame.
   *
   * @return the close action.
   */
  public Action getCloseAction()
  {
    return closeAction;
  }

  /**
   * Returns the preview action implementation to handle the preview action
   * event.
   *
   * @return the preview action.
   */
  public Action getPreviewAction()
  {
    return previewAction;
  }

  public Action getAboutAction()
  {
    return aboutAction;
  }

  /**
   * Exits the application, but only if the user agrees.
   *
   * @return false if the user decides not to exit the application.
   */
  protected boolean attemptExit()
  {
    final boolean exit = 
      MessageDialog.openConfirm(
          this.getParentShell(), 
          getResources().getString("exitdialog.title"),
          getResources().getString("exitdialog.message"));
 
    if (exit)
    {
      if (ignoreEmbeddedConfig ||
          "false".equals(ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
              (EMBEDDED_KEY, "false")))
      {
        this.close();
      }
    }
    return exit;
  }

  /**
   * Handler method called by the preview action. This method should perform all
   * operations to preview the report.
   */
  protected abstract void attemptPreview(Shell shell);

  /**
   * Shows the exception dialog by using localized messages. The message base is
   * used to construct the localisation key by appending ".title" and ".message"
   * to the base name.
   *
   * @param localisationBase the resource prefix.
   * @param e                the exception.
   */
  public static void showExceptionDialog
          (final String localisationBase, final Exception e)
  {
    System.out.println("should show an Exception Dialog for: "+e);
  }


  /** Displays information about the application. */
  public synchronized void displayAbout()
  {
    new AboutDialog(this.getShell(), getResources().getString("action.about.name"),
                      ClassicEngineDemoInfo.getInstance()).open();
  }

  /**
   * Creates a menu manager.
   *
   * @return the menu manager.
   */
  protected MenuManager createMenuManager(){

    final MenuManager fileMenu = new MenuManager(getResources().getString("menu.file.name"));
    final MenuManager helpMenu = new MenuManager(getResources().getString("menu.help.name"));
    
    final MenuManager menubar = new MenuManager("");
    menubar.add(fileMenu);
    menubar.add(helpMenu);
    
    fileMenu.add(previewAction );
    fileMenu.add(closeAction);
    helpMenu.add(aboutAction);
    
    return menubar;
  }

  public Action getExportAction()
  {
    return previewAction;
  }

  public void setStatusText(final String text)
  {
    setStatus(text);
  }
}
