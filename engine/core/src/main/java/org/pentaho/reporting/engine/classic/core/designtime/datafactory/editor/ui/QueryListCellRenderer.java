/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.ui;

import java.awt.Component;

import javax.swing.JList;

import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.Query;
import org.pentaho.reporting.libraries.designtime.swing.FixDefaultListCellRenderer;

public class QueryListCellRenderer extends FixDefaultListCellRenderer {
  public QueryListCellRenderer() {
  }

  public Component getListCellRendererComponent( final JList list, final Object value, final int index,
      final boolean isSelected, final boolean cellHasFocus ) {
    if ( value instanceof Query ) {
      Query q = (Query) value;
      return super.getListCellRendererComponent( list, q.getName(), index, isSelected, cellHasFocus );
    }

    return super.getListCellRendererComponent( list, null, index, isSelected, cellHasFocus );
  }
}
