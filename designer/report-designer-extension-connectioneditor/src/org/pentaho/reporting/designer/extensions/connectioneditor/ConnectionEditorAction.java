package org.pentaho.reporting.designer.extensions.connectioneditor;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class ConnectionEditorAction extends AbstractDesignerContextAction
{
  public ConnectionEditorAction()
  {
    putValue(Action.NAME, Messages.getInstance().getString("ConnectionEditorAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, Messages.getInstance().getString("ConnectionEditorAction.Description"));
    putValue(Action.MNEMONIC_KEY, Messages.getInstance().getOptionalMnemonic("ConnectionEditorAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, Messages.getInstance().getOptionalKeyStroke("ConnectionEditorAction.Accelerator"));
    putValue(Action.SMALL_ICON, IconLoader.getInstance().getEmptyIcon());
  }

  public void actionPerformed(final ActionEvent e)
  {
    final ReportDesignerContext context = getReportDesignerContext();
    final Component parent = context.getParent();
    final Window window = LibSwingUtil.getWindowAncestor(parent);
    final ConnectionEditorDialog dialog;
    if (window instanceof JDialog)
    {
      dialog = new ConnectionEditorDialog((JDialog) window);
    }
    else if (window instanceof JFrame)
    {
      dialog = new ConnectionEditorDialog((JFrame) window);
    }
    else
    {
      dialog = new ConnectionEditorDialog();
    }

    dialog.performEditConnections(context);

  }
}
