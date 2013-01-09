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

package org.pentaho.openformula.ui.util;

import java.awt.Component;
import java.util.Locale;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.pentaho.openformula.ui.Messages;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;

public class FunctionCategoryCellRenderer extends DefaultListCellRenderer
{
  public FunctionCategoryCellRenderer()
  {
  }

  public Component getListCellRendererComponent(final JList list,
                                                final Object value,
                                                final int index,
                                                final boolean isSelected,
                                                final boolean cellHasFocus)
  {
    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    if (value instanceof FunctionCategory)
    {
      final FunctionCategory cat = (FunctionCategory) value;
      setText(cat.getDisplayName(Locale.getDefault()));
      setToolTipText(cat.getDescription(Locale.getDefault()));
    }
    else
    {
      setText(Messages.getInstance().getString("FunctionCategoryCellRenderer.All")); 
      setToolTipText(null);
    }
    return this;
  }
}
