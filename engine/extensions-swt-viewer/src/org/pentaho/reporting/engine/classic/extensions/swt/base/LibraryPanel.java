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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.reporting.libraries.base.versioning.DependencyInformation;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * A panel containing a table that lists the libraries used in a project.
 * <p/>
 * Used in the AboutFrame class.
 *
 * @author Baochuan Lu
 */
public class LibraryPanel extends Composite
{
  /**
   * Constructs a LibraryPanel.
   *
   * @param libraries a list of libraries (represented by Library objects).
   */
  public LibraryPanel(final List libraries, final Composite parent)
  {
    super(parent, SWT.NONE);
    this.setLayout(new GridLayout(1, false));
    final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
        | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
    gridData.widthHint = 500;
    gridData.heightHint = 300;
    
    final Table table = new Table(this, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
    table.setLayoutData(gridData);
    table.setHeaderVisible(true);
    
    TableColumn column = new TableColumn(table, SWT.NONE);
    column.setWidth(150);
    column.setText("Name");
    
    column = new TableColumn(table, SWT.NONE);
    column.setWidth(50);
    column.setText("Version");
    
    column = new TableColumn(table, SWT.NONE);
    column.setWidth(150);
    column.setText("License");
    
    column = new TableColumn(table, SWT.NONE);
    column.setWidth(350);
    column.setText("Info");
    
    for (int i = 0; i < libraries.size(); i++)
    {
      final DependencyInformation depInfo = (DependencyInformation) libraries.get(i);
      final TableItem item = new TableItem(table, SWT.NONE);
      item.setText(0, depInfo.getName());
      item.setText(1, depInfo.getVersion());
      item.setText(2, depInfo.getLicenseName() == null ? "No license name":depInfo.getLicenseName());
      item.setText(3, depInfo.getInfo());
    }
  }

  public LibraryPanel(final ProjectInformation projectInfo, final Composite parent)
  {
    this(LibraryPanel.getLibraries(projectInfo), parent);
  }

  private static List getLibraries(final ProjectInformation info)
  {
    if (info == null)
    {
      return new ArrayList();
    }
    final ArrayList libs = new ArrayList();
    LibraryPanel.collectLibraries(info, libs);
    return libs;
  }

  private static void collectLibraries(final ProjectInformation info,
                                       final List list)
  {
    DependencyInformation[] libs = info.getLibraries();
    for (int i = 0; i < libs.length; i++)
    {
      final DependencyInformation lib = libs[i];
      if (list.contains(lib) == false)
      {
        // prevent duplicates, they look ugly ..
        list.add(lib);
        if (lib instanceof ProjectInformation)
        {
          LibraryPanel.collectLibraries((ProjectInformation) lib, list);
        }
      }
    }

    libs = info.getOptionalLibraries();
    for (int i = 0; i < libs.length; i++)
    {
      final DependencyInformation lib = libs[i];
      if (list.contains(lib) == false)
      {
        // prevent duplicates, they look ugly ..
        list.add(lib);
        if (lib instanceof ProjectInformation)
        {
          LibraryPanel.collectLibraries((ProjectInformation) lib, list);
        }
      }
    }
  }
}
