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

import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.reporting.engine.classic.extensions.swt.Util;

/**
 * A panel containing a table of system properties.
 *
 * @author Baochuan Lu
 */
public class SystemPropertiesPanel extends Composite
{
  private Table table;
  private ResourceBundle resources;
  
  public SystemPropertiesPanel(final Composite parent)
  {
    super(parent, SWT.NONE);
 //   this.setSize(400, 400);
    
    final String baseName = "org.jfree.ui.about.resources.AboutResources";
    resources = ResourceBundle.getBundle(baseName);
   
    this.setLayout(new GridLayout(1, false));
    final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
        | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
    gridData.widthHint = 500;
    gridData.heightHint = 400;
    
    table = createSystemPropertiesTable();
    table.setLayoutData(gridData);
//    MenuManager menu_manager = new MenuManager();
//    table.setMenu(menu_manager.createContextMenu(table));
//    menu_manager.add(new CopySystemPropertiesToClipboardAction());
  }
  
  /**
   * Creates and returns a Table containing all the system properties.  
   * This method returns a table that is configured so that the user can sort 
   * the properties by clicking on the table header.
   *
   * @return a system properties table.
   */
  public Table createSystemPropertiesTable()
  {
    final Table table = new Table(this, SWT.MULTI | SWT.BORDER);
    table.setHeaderVisible(true);
    
    final TableColumn name = new TableColumn(table, SWT.NONE);
    name.setWidth(200);
    name.setText("Property-Name");
    final TableColumn value = new TableColumn(table, SWT.NONE);
    value.setWidth(350);
    value.setText("Value");

    final Properties sysProps = System.getProperties();

    final TreeMap data = new TreeMap(sysProps);
    final Map.Entry[] entries = (Map.Entry[]) data.entrySet().toArray(new Map.Entry[data.size()]);
    
    final int size = entries.length;
    for (int i = 0; i < size; i++){
      final TableItem item = new TableItem(table, SWT.NONE);
      item.setText(0, (String) entries[i].getKey());
      item.setText(1, (String) entries[i].getValue());
      System.out.println("name: "+item.getText(0));

    }
    
    return table;
  }
 
  /**
   * Copies the selected cells in the table to the clipboard, in tab-delimited format.
   */
  public class copySystemPropertiesToClipboardAction extends Action{
    
    public copySystemPropertiesToClipboardAction(){
      setText(resources.getString("system-properties-panel.popup-menu.copy"));
      //setText("copy");
    }
    
    public void run(){
      final Clipboard clipboard = Util.getClipboard();
      final TextTransfer text_transfer = TextTransfer.getInstance();
      final TableItem[] items = table.getSelection();
      final int size = items.length;
      
      if (size == 0){
        return;
      }
      final StringBuffer clipboardContent = new StringBuffer();
      for (int i = 0; i < size; i++){
        final String name = items[i].getText(0);
        final String value = items[i].getText(1);
        clipboardContent.append(name+"\t"+value+"\n");
      }
      
      clipboard.setContents(
          new Object[] {clipboardContent.toString()},
          new Transfer[] {text_transfer});
    }
  }
}
