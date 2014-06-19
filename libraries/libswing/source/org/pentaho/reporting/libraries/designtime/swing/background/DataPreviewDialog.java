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

package org.pentaho.reporting.libraries.designtime.swing.background;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableModel;

import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.Messages;

public class DataPreviewDialog extends JDialog
{
  private class CloseAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private CloseAction()
    {
      putValue(Action.NAME, Messages.getInstance().getString("PreviewDialog.Close"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      dispose();
    }
  }

  private JTable table;

  public DataPreviewDialog()
      throws HeadlessException
  {
    init();
  }

  public DataPreviewDialog(final Frame owner)
      throws HeadlessException
  {
    super(owner);
    init();
  }

  public DataPreviewDialog(final Dialog owner)
      throws HeadlessException
  {
    super(owner);
    init();
  }

  private void init()
  {

    setModal(true);
    setTitle(Messages.getInstance().getString("PreviewDialog.Title"));

    final JPanel mainPanel = new JPanel(new BorderLayout());
    setContentPane(mainPanel);

    table = new JTable();
    mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

    final JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
    final JButton closeButton = new JButton(new CloseAction());
    buttonsPanel.add(closeButton);
    mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

    final JComponent contentPane = (JComponent) getContentPane();
    final InputMap inputMap = contentPane.getInputMap();
    final ActionMap actionMap = contentPane.getActionMap();

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel"); // NON-NLS
    actionMap.put("cancel", new CloseAction()); // NON-NLS

    setResizable(true);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setSize(800, 600);
  }

  public void showData(final PreviewWorker previewWorker)
  {
    // Run the query in a separate thread so that we can display a cancel dialog
    final Thread qt = new Thread(previewWorker);
    BackgroundCancellableProcessHelper.executeProcessWithCancelDialog
        (qt, previewWorker, this, Messages.getInstance().getString("PreviewDialog.PreviewDataTask"));

    // Get the results ... or throw the exception that was generated
    final TableModel rawTableModel = previewWorker.getResultTableModel();
    if (rawTableModel == null)
    {
      // User must have hit cancel
      return;
    }

    LibSwingUtil.centerDialogInParent(this);
    table.setModel(rawTableModel);

    setVisible(true);
    previewWorker.close();
  }
}
