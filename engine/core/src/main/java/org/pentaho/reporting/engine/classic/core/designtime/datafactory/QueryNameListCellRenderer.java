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

package org.pentaho.reporting.engine.classic.core.designtime.datafactory;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.pentaho.reporting.libraries.base.util.StringUtils;

public class QueryNameListCellRenderer extends DefaultListCellRenderer {
  public Component getListCellRendererComponent( final JList list, final Object value, final int index,
      final boolean isSelected, final boolean cellHasFocus ) {
    final JLabel listCellRendererComponent =
        (JLabel) super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    if ( value != null ) {
      final DataSetQuery dataSetQuery = (DataSetQuery) value;
      final String queryName = dataSetQuery.getQueryName();
      if ( !StringUtils.isEmpty( queryName, false ) ) {
        listCellRendererComponent.setText( queryName );
      } else {
        listCellRendererComponent.setText( " " );
      }
    }
    return listCellRendererComponent;
  }
}
