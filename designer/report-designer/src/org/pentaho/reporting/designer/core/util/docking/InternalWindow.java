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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.util.docking;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.SystemColor;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class InternalWindow extends JPanel
{
  public InternalWindow(final String titleCategory)
  {
    setLayout(new BorderLayout());

    final DefaultHeaderComponent defaultHeaderComponent = new DefaultHeaderComponent(titleCategory);
    defaultHeaderComponent.setBorder(new HeaderBorder());

    add(defaultHeaderComponent, BorderLayout.NORTH);
    setBorder(new ShadowBorder());
    setBorder(BorderFactory.createLineBorder(SystemColor.controlShadow));

  }

  public final void setFocusCycleRoot(final boolean focusCycleRoot)
  {
  }

  public final boolean isFocusCycleRoot()
  {
    return true;
  }

  public final Container getFocusCycleRootAncestor()
  {
    return null;
  }
}
