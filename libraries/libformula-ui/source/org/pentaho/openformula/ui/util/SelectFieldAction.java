package org.pentaho.openformula.ui.util;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.pentaho.openformula.ui.DefaultFunctionParameterEditor;
import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class SelectFieldAction extends AbstractAction
{
  private PropertyChangeListener selectorUpdateHandler;
  private FieldSelectorDialog fieldSelectorDialog;
  private Component parent;
  private FieldDefinition[] fields;

  public SelectFieldAction(final Component parent, final PropertyChangeListener selectorUpdateHandler)
  {
    this.parent = parent;
    this.selectorUpdateHandler = selectorUpdateHandler;
    final URL resource = DefaultFunctionParameterEditor.class.getResource
        ("/org/pentaho/openformula/ui/images/field.gif");  //NON-NLS
    if (resource != null)
    {
      final Icon icon = new ImageIcon(resource);
      putValue(Action.SMALL_ICON, icon);
    }
    else
    {
      putValue(Action.NAME, "..");
    }
  }

  public FieldDefinition[] getFields()
  {
    return fields.clone();
  }

  public void setFields(final FieldDefinition[] fields)
  {
    this.fields = fields.clone();
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    if (fieldSelectorDialog == null)
    {
      final Window w = LibSwingUtil.getWindowAncestor(parent);
      if (w instanceof Frame)
      {
        this.fieldSelectorDialog = new FieldSelectorDialog((Frame) w);
        LibSwingUtil.positionDialogRelativeToParent(this.fieldSelectorDialog, 0.5, 0.5);
      }
      else if (w instanceof Dialog)
      {
        this.fieldSelectorDialog = new FieldSelectorDialog((Dialog) w);
        LibSwingUtil.positionDialogRelativeToParent(this.fieldSelectorDialog, 0.5, 0.5);
      }
      else
      {
        this.fieldSelectorDialog = new FieldSelectorDialog();
        LibSwingUtil.positionDialogRelativeToParent(this.fieldSelectorDialog, 0.5, 0.5);
      }
    }

    if (fields != null)
    {
      this.fieldSelectorDialog.setFields(fields);
    }

    fieldSelectorDialog.removePropertyChangeListener
        (FieldSelectorDialog.SELECTED_DEFINITION_PROPERTY, selectorUpdateHandler);
    fieldSelectorDialog.setSelectedDefinition(null);
    fieldSelectorDialog.addPropertyChangeListener(FieldSelectorDialog.SELECTED_DEFINITION_PROPERTY,
        selectorUpdateHandler);
    fieldSelectorDialog.setVisible(true);
  }

  public void dispose()
  {
    if (fieldSelectorDialog != null)
    {
      fieldSelectorDialog.dispose();
    }
  }
}
