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

package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.HeadlessException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

public class ShowTextDialog extends CommonDialog
{
  private JTextArea textArea;

  public ShowTextDialog()
  {
    init();
  }

  public ShowTextDialog(final Frame owner)
      throws HeadlessException
  {
    super(owner);
    init();
  }

  public ShowTextDialog(final Dialog owner)
      throws HeadlessException
  {
    super(owner);
    init();
  }

  protected void init()
  {
    super.init();
    pack();
    setSize(800, 600);
  }

  protected String getDialogId()
  {
    return getClass().getName();
  }

  protected Component createContentPane()
  {
    textArea = new JTextArea();
    textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    textArea.setLineWrap(false);
    textArea.setEditable(true);

    final JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
    return panel;
  }

  protected boolean hasCancelButton()
  {
    return false;
  }

  public void showText(String text)
  {
    textArea.setText(text);
    setModal(false);
    setVisible(true);
  }
}
