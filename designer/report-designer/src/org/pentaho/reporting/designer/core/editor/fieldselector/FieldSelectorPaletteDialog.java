package org.pentaho.reporting.designer.core.editor.fieldselector;

import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.UIManager;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.MacOSXIntegration;

public class FieldSelectorPaletteDialog extends JDialog
{
  private class FrameSizeMonitor extends WindowAdapter
  {
    private FrameSizeMonitor()
    {
    }

    /**
     * Invoked when a window is in the process of being closed.
     * The close operation can be overridden at this point.
     */
    public void windowClosing(final WindowEvent e)
    {
      WorkspaceSettings.getInstance().setFieldPaletteBounds(FieldSelectorPaletteDialog.this.getBounds());
      WorkspaceSettings.getInstance().setFieldSelectorVisible(false);
    }
  }


  private FieldSelectorPanel fieldSelectorPanel;

  public FieldSelectorPaletteDialog(final Frame parent, final ReportDesignerContext designerContext)
  {
    super(parent);
    init(designerContext);
  }

  protected void init(final ReportDesignerContext designerContext)
  {
    setResizable(true);
    addWindowListener(new FrameSizeMonitor());

    if (MacOSXIntegration.MAC_OS_X)
    {
      getRootPane().putClientProperty("Window.style", "small"); // NON-NLS
    }
    else if (UIManager.getLookAndFeel().getSupportsWindowDecorations())
    {
      setUndecorated(true);
      getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
    }

    setTitle(Messages.getString("FieldSelectorPaletteDialog.Title"));
    setDefaultCloseOperation(HIDE_ON_CLOSE);

    this.fieldSelectorPanel = new FieldSelectorPanel();
    this.fieldSelectorPanel.setReportDesignerContext(designerContext);

    setContentPane(fieldSelectorPanel);
  }

  public void initWindowLocation()
  {
    final Rectangle rectangle = WorkspaceSettings.getInstance().getFieldPaletteBounds();
    if (rectangle != null)
    {
      final Rectangle bounds = fieldSelectorPanel.getReportDesignerContext().getParent().getBounds();
      if (rectangle.contains(bounds) || rectangle.equals(bounds))
      {
        DebugLog.log("Found a usable screen-configuration: Restoring frame to " + bounds);// NON-NLS
        setBounds(bounds);
        setVisible(true);
        return;
      }
    }

    pack();
    LibSwingUtil.centerDialogInParent(this);
  }
}
