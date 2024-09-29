/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.designer.core.editor.format;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.util.undo.ElementFormatUndoEntry;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ElementFormatDialog extends CommonDialog {

  public static final int FONT_PANE = 0;
  public static final int ADVANCED_FONT_PANE = 1;
  public static final int PARAGRAPH_PANE = 2;
  public static final int BORDERS_PANE = 3;
  public static final int COLOR_PANE = 4;

  private FontPropertiesPane fontPropertiesPane;
  private AdvancedFontPropertiesPane advancedFontPropertiesPane;
  private ParagraphPropertiesPane paragraphPropertiesPane;
  private ColorPropertiesPane colorPropertiesPane;
  private BorderPropertiesPane borderPropertiesPane;
  private JTabbedPane tabbedPane;

  public ElementFormatDialog()
    throws HeadlessException {
    init();
  }

  public ElementFormatDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public ElementFormatDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    setTitle( Messages.getString( "ElementFormatDialog.FormatElement" ) );

    fontPropertiesPane = new FontPropertiesPane();
    advancedFontPropertiesPane = new AdvancedFontPropertiesPane();
    paragraphPropertiesPane = new ParagraphPropertiesPane();
    colorPropertiesPane = new ColorPropertiesPane();
    borderPropertiesPane = new BorderPropertiesPane();

    tabbedPane = new JTabbedPane();
    tabbedPane.add( Messages.getString( "ElementFormatDialog.Font" ), fontPropertiesPane );
    tabbedPane.add( Messages.getString( "ElementFormatDialog.AdvancedFontSettings" ), advancedFontPropertiesPane );
    tabbedPane.add( Messages.getString( "ElementFormatDialog.Paragraph" ), paragraphPropertiesPane );
    tabbedPane.add( Messages.getString( "ElementFormatDialog.SizeAndBorders" ), borderPropertiesPane );
    tabbedPane.add( Messages.getString( "ElementFormatDialog.ColorAndBackground" ), colorPropertiesPane );

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.ElementFormat";
  }

  protected Component createContentPane() {
    return tabbedPane;
  }

  public void setActivePane( final int pane ) {
    tabbedPane.setSelectedIndex( pane );
  }

  public ElementFormatUndoEntry.EditResult performEdit( final ReportDesignerContext designerContext,
                                                        final ElementStyleSheet element,
                                                        final Map<StyleKey, Expression> styleExpressions ) {
    final EditableStyleSheet styleSheet = new EditableStyleSheet();
    styleSheet.copyParentValues( element );

    final Map<StyleKey, Expression> editableStyleExpressions;
    if ( styleExpressions == null ) {
      editableStyleExpressions = null;
    } else {
      editableStyleExpressions = new HashMap<StyleKey, Expression>( styleExpressions );
    }
    fontPropertiesPane.initializeFromStyle( styleSheet );
    advancedFontPropertiesPane.initializeFromStyle( styleSheet );
    paragraphPropertiesPane.initializeFromStyle( styleSheet );
    borderPropertiesPane.initializeFromStyle( styleSheet );
    colorPropertiesPane.initializeFromStyle( styleSheet );

    fontPropertiesPane.commitValues( styleSheet );
    advancedFontPropertiesPane.commitValues( styleSheet );
    paragraphPropertiesPane.commitValues( styleSheet );
    borderPropertiesPane.commitValues( styleSheet );
    colorPropertiesPane.commitValues( styleSheet );

    styleSheet.clearEdits();

    if ( performEdit() == false ) {
      return null;
    }

    fontPropertiesPane.commitValues( styleSheet );
    advancedFontPropertiesPane.commitValues( styleSheet );
    paragraphPropertiesPane.commitValues( styleSheet );
    borderPropertiesPane.commitValues( styleSheet );
    colorPropertiesPane.commitValues( styleSheet );

    // do something ..
    return new ElementFormatUndoEntry.EditResult( styleSheet, editableStyleExpressions );
  }
}
