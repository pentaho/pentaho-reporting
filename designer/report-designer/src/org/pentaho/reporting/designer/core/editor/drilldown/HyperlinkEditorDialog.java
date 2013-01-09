package org.pentaho.reporting.designer.core.editor.drilldown;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.util.HashMap;
import java.util.Map;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.format.EditableStyleSheet;
import org.pentaho.reporting.designer.core.util.undo.ElementFormatUndoEntry;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

public class HyperlinkEditorDialog extends CommonDialog
{
  private HyperlinkEditorPane hyperlinksPane;

  public HyperlinkEditorDialog()
      throws HeadlessException
  {
    init();
  }

  public HyperlinkEditorDialog(final Frame owner)
      throws HeadlessException
  {
    super(owner);
    init();
  }

  public HyperlinkEditorDialog(final Dialog owner)
      throws HeadlessException
  {
    super(owner);
    init();
  }

  protected void init()
  {
    setTitle(Messages.getString("HyperlinkEditorDialog.Title"));

    hyperlinksPane = new HyperlinkEditorPane();
    super.init();
  }

  protected String getDialogId()
  {
    return "ReportDesigner.Core.HyperlinkEditor";
  }

  protected Component createContentPane()
  {
    return hyperlinksPane;
  }

  public ElementFormatUndoEntry.EditResult performEdit(final ReportDesignerContext designerContext,
                                                       final ElementStyleSheet element,
                                                       final Map<StyleKey,Expression> styleExpressions)
  {
    if (styleExpressions == null)
    {
      throw new NullPointerException();
    }

    final EditableStyleSheet styleSheet = new EditableStyleSheet();
    styleSheet.copyParentValues(element);
    
    final HashMap<StyleKey, Expression> editableStyleExpressions = new HashMap<StyleKey, Expression>(styleExpressions);

    hyperlinksPane.initializeFromStyle(styleSheet, editableStyleExpressions, designerContext);

    if (performEdit() == false)
    {
      return null;
    }

    hyperlinksPane.commitValues(styleSheet, editableStyleExpressions);

    // do something ..
    return new ElementFormatUndoEntry.EditResult(styleSheet, editableStyleExpressions);
  }
}

