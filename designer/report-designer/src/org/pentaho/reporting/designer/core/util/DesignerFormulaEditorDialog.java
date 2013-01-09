package org.pentaho.reporting.designer.core.util;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToolBar;

import org.pentaho.openformula.ui.FormulaEditorDialog;
import org.pentaho.reporting.libraries.designtime.swing.ToolbarButton;

public class DesignerFormulaEditorDialog extends FormulaEditorDialog
{
  private class InsertTextAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private InsertTextAction()
    {
      putValue(Action.SMALL_ICON, IconLoader.getInstance().getHyperlinkIcon());
      putValue(Action.SHORT_DESCRIPTION, UtilMessages.getInstance().getString("DesignerFormulaEditorDialog.InsertDrillDown"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      insertText("=DRILLDOWN(\"Text\"; \"Text\"; Any)");  // NON-NLS
    }
  }

  public DesignerFormulaEditorDialog()
  {
  }

  public DesignerFormulaEditorDialog(final Frame owner)
  {
    super(owner);
  }

  public DesignerFormulaEditorDialog(final Dialog owner)
  {
    super(owner);
  }

  protected void init()
  {
    super.init();

    final JToolBar toolBar = getOperatorPanel();
    toolBar.add(new ToolbarButton(new InsertTextAction()));
  }
}
