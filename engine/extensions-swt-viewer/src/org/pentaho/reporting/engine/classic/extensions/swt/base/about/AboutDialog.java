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

package org.pentaho.reporting.engine.classic.extensions.swt.base.about;

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
 * ReportActionEvent.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.reporting.engine.classic.extensions.swt.base.SWTPreviewModule;
import org.pentaho.reporting.libraries.base.versioning.Licenses;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * Creation-Date: 8/17/2008
 * 
 * @author Baochuan Lu
 */
public class AboutDialog extends Dialog
{
  /**
   * The application name.
   */
  private String application;

  /**
   * The application version.
   */
  private String version;

  /**
   * The copyright string.
   */
  private String copyright;

  /**
   * Other info about the application.
   */
  private String info;

  /**
   * The license.
   */
  private String license;

  /**
   * Localized resources.
   */
  private ResourceBundle resources;

  private ProjectInformation libraries;

  /**
   * Constructs an about dialog.
   * 
   * @param shell
   *          parent shell.
   * @param title
   *          the title.
   * @param libraries
   *          information about the project.
   */
  public AboutDialog(final Shell shell, final String title,
      final ProjectInformation libraries)
  {
    super(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    this.setText(title);

    this.libraries = libraries;
    this.application = libraries.getName();
    this.version = libraries.getVersion();
    this.copyright = libraries.getCopyright();
    this.info = libraries.getInfo();
    if ("GPL".equalsIgnoreCase(libraries.getLicenseName()))
    {
      this.license = Licenses.getInstance().getGPL();
    } else if ("LGPL".equalsIgnoreCase(libraries.getLicenseName()))
    {
      this.license = Licenses.getInstance().getLGPL();
    } else
    {
      this.license = libraries.getLicenseName();
    }
    
    this.resources = ResourceBundle.getBundle(SWTPreviewModule.BUNDLE_NAME);
  }

  public void open()
  {
    // Create the dialog window
    final Shell dialog = new Shell(getParent(), getStyle());
    dialog.setText(getText());
    createContents(dialog);
    // dialog.setSize(800, 400);
    dialog.pack();
    dialog.open();
    final Display display = getParent().getDisplay();
    while (!dialog.isDisposed())
    {
      if (!display.readAndDispatch())
      {
        display.sleep();
      }
    }
  }

  /**
   * Creates the dialog's contents
   * 
   * @param parent
   *          the parent composite
   * @return Control
   */
  protected Control createContents(final Composite parent)
  {
    parent.setLayout(new FillLayout());

    final TabFolder tabFolder = new TabFolder(parent, SWT.CENTER);
    final TabItem aboutTab = new TabItem(tabFolder, SWT.NONE);
    aboutTab.setText(this.resources.getString("about-frame.tab.about"));
    aboutTab.setControl(createAboutTabControl(tabFolder));

    final TabItem systemTab = new TabItem(tabFolder, SWT.NONE);
    systemTab.setText(resources.getString("about-frame.tab.system"));
    systemTab.setControl(new SystemPropertiesPanel(tabFolder));

    return parent;
  }

  private Control createAboutTabControl(final TabFolder tabFolder)
  {
    final Composite composite = new Composite(tabFolder, SWT.NONE);
    final GridLayout layout = new GridLayout(1, false);
    composite.setLayout(layout);
    GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
    Label textLabel = new Label(composite, SWT.CENTER);
    textLabel.setText(application);
    textLabel.setLayoutData(gridData);

    textLabel = new Label(composite, SWT.CENTER);
    textLabel.setText(version == null ? "" : version);
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
    textLabel.setLayoutData(gridData);

    textLabel = new Label(composite, SWT.CENTER);
    textLabel.setText(copyright);
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
    textLabel.setLayoutData(gridData);

    textLabel = new Label(composite, SWT.CENTER);
    textLabel.setText(info);
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
    textLabel.setLayoutData(gridData);

    final TabFolder libWithLicenseTabFolder = new TabFolder(composite, SWT.NONE);
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
        | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
    // gridData.widthHint = 400;
    // gridData.heightHint = 200;
    libWithLicenseTabFolder.setLayoutData(gridData);

    if (this.license != null)
    {
      final TabItem licenseTab = new TabItem(libWithLicenseTabFolder, SWT.NONE);
      licenseTab.setText(this.resources.getString("about-frame.tab.licence"));
      licenseTab.setControl(createLicenseTabControl(libWithLicenseTabFolder));
    }

    if (this.info != null)
    {
      final TabItem libraryTab = new TabItem(libWithLicenseTabFolder, SWT.NONE);
      libraryTab.setText(this.resources.getString("about-frame.tab.libraries"));
      final Composite libPanel = new LibraryPanel(libraries, libWithLicenseTabFolder);
      libraryTab.setControl(libPanel);
    }

    return composite;
  }

  /**
   * Creates a tab showing the license.
   * 
   * @param tabFolder
   *          the parent TabFolder
   * @return a tab.
   */

  private Control createLicenseTabControl(final TabFolder tabFolder)
  {
    // Create a composite and add license to it
    final Composite composite = new Composite(tabFolder, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));
    // create a text area
    final Text licenseText = new Text(composite, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
    licenseText.setText(this.license);

    return composite;
  }
}
