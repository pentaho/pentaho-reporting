package org.pentaho.reporting.designer.extensions.connectioneditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import javax.swing.JPanel;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

public class ConnectionEditorDialog extends CommonDialog
{
  private ConnectionEditorPanel editorPanel;

  public ConnectionEditorDialog()
  {
    init();
  }

  public ConnectionEditorDialog(final Frame owner) throws HeadlessException
  {
    super(owner);
    init();
  }

  public ConnectionEditorDialog(final Dialog owner) throws HeadlessException
  {
    super(owner);
    init();
  }

  protected void init()
  {
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle(Messages.getInstance().getString("ConnectionEditorDialog.Title"));

    editorPanel = new ConnectionEditorPanel();

    super.init();
  }

  protected String getDialogId()
  {
    return getClass().getSimpleName();
  }

  protected Component createContentPane()
  {
    final JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(editorPanel, BorderLayout.CENTER);
    return panel;
  }

  public void performEditConnections(final ReportDesignerContext context)
  {
    if (performEdit() == false)
    {
      return;
    }

    editorPanel.commit();
  }

  public static void main(String[] args)
  {
    ClassicEngineBoot.getInstance().start();

    ConnectionEditorDialog d = new ConnectionEditorDialog();
    d.setModal(true);
    d.setVisible(true);
  }
}
