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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts.internalframe;

import java.awt.Container;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

/**
 * Creation-Date: 11.12.2005, 12:50:21
 *
 * @author Thomas Morgner
 *//* Used by InternalFrameDemo.java. */
public class DocumentInternalFrame extends JInternalFrame
{
  public DocumentInternalFrame()
  {
    super("Document", true, true, true, true);
    final Container contentPane = getContentPane();
    contentPane.setLayout(new FlowLayout());
    contentPane.add(new JLabel("Some text"));
    contentPane.add(new JButton("A button"));
    contentPane.add(new JLabel("Some more text"));
  }

}
