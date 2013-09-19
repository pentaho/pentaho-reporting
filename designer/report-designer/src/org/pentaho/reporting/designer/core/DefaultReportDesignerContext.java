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

package org.pentaho.reporting.designer.core;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.pentaho.reporting.designer.core.xul.XulDesignerFrame;
import org.pentaho.ui.xul.XulException;

public class DefaultReportDesignerContext extends AbstractReportDesignerContext
{
  private XulDesignerFrame xulDesignerFrame;
  private Component parent;

  public DefaultReportDesignerContext(final Component parent, final ReportDesignerView view) throws XulException
  {
    super(view);
    this.parent = parent;
    this.xulDesignerFrame = new XulDesignerFrame();
    this.xulDesignerFrame.setReportDesignerContext(this);
  }

  public XulDesignerFrame getXulDesignerFrame()
  {
    return xulDesignerFrame;
  }

  public JPopupMenu getPopupMenu(final String id)
  {
    return xulDesignerFrame.getPopupMenu(id);
  }

  public JComponent getToolBar(final String id)
  {
    return xulDesignerFrame.getToolBar(id);
  }

  public Component getParent()
  {
    return parent;
  }
}
