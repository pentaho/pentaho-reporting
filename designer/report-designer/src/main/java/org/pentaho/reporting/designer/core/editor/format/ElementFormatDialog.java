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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
