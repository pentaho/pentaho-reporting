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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * The DemoTextInputPanel provides two input fields for the report properties used in the StackedLayoutDemos.
 *
 * @author Thomas Morgner
 */
public class DemoTextInputPanel extends JPanel
{
  private JTextArea messageOneField;
  private JTextArea messageTwoField;

  public DemoTextInputPanel()
  {
    setLayout(new GridBagLayout());

    final JLabel messageOneLabel = new JLabel("One:");
    final JLabel messageTwoLabel = new JLabel("Two:");
    messageOneField = new JTextArea();
    messageOneField.setWrapStyleWord(true);
    messageOneField.setRows(10);
    messageTwoField = new JTextArea();
    messageTwoField.setRows(10);
    messageTwoField.setWrapStyleWord(true);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    add(messageOneLabel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    add(messageTwoLabel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    add(new JScrollPane(messageOneField), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    add(new JScrollPane(messageTwoField), gbc);
  }

  public String getMessageOne()
  {
    return messageOneField.getText();
  }

  public String getMessageTwo()
  {
    return messageTwoField.getText();
  }
}
