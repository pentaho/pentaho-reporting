/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.editor.parameters;

import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.*;

/**
 * Todo: Document me!
 * <p/>
 * Date: 09.04.2009 Time: 18:36:34
 *
 * @author Thomas Morgner.
 */
public class StaticTextComboBoxModel extends AbstractListModel implements ComboBoxModel {
  private String[] values;
  private String selectedItem;

  public StaticTextComboBoxModel() {
    values = new String[ 0 ];
  }

  public void setValues( final String[] values ) {
    this.values = values.clone();
    fireContentsChanged( this, 0, values.length );
  }

  public int getSize() {
    return values.length;
  }

  public Object getElementAt( final int index ) {
    return values[ index ];
  }

  public void setSelectedItem( final Object anItem ) {
    selectedItem = (String) anItem;
    fireContentsChanged( this, -1, -1 );
  }

  public Object getSelectedItem() {
    return StringUtils.isEmpty( selectedItem ) ? null : selectedItem;
  }
}
