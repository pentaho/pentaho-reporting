/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.util;

import org.pentaho.openformula.ui.FormulaEditorDialog;
import org.pentaho.openformula.ui.FunctionParameterEditor;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import java.awt.*;
import java.util.Iterator;

public class GUIUtils {
  public static final String FUNCTION_EDITOR_CONFIX_PREFIX = "org.pentaho.reporting.designer.core.function-editor.";

  private GUIUtils() {
  }

  private static void ensureMinimumDialogWidth( final Dialog dialog, final int minWidth ) {
    if ( dialog.getWidth() < minWidth ) {
      dialog.setSize( minWidth, dialog.getHeight() );
    }
  }


  private static void ensureMinimumDialogHeight( final Dialog dialog, final int minHeight ) {
    if ( dialog.getHeight() < minHeight ) {
      dialog.setSize( dialog.getWidth(), minHeight );
    }
  }


  private static void ensureMaximumDialogWidth( final Dialog dialog, final int maxWidth ) {
    if ( dialog.getWidth() > maxWidth ) {
      dialog.setSize( maxWidth, dialog.getHeight() );
    }
  }


  private static void ensureMaximumDialogHeight( final Dialog dialog, final int maxHeight ) {
    if ( dialog.getHeight() > maxHeight ) {
      dialog.setSize( dialog.getWidth(), maxHeight );
    }
  }


  public static void ensureMinimumDialogSize( final Dialog dialog, final int minWidth, final int minHeight ) {
    ensureMinimumDialogWidth( dialog, minWidth );
    ensureMinimumDialogHeight( dialog, minHeight );
  }


  public static void ensureMaximumDialogSize( final Dialog dialog, final int maxWidth, final int maxHeight ) {
    ensureMaximumDialogWidth( dialog, maxWidth );
    ensureMaximumDialogHeight( dialog, maxHeight );
  }

  public static FormulaEditorDialog createFormulaEditorDialog( final ReportDesignerContext context,
                                                               final Component parent ) {
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final DesignerFormulaEditorDialog editorDialog;
    if ( window instanceof Frame ) {
      editorDialog = new DesignerFormulaEditorDialog( (Frame) window );
    } else if ( window instanceof Dialog ) {
      editorDialog = new DesignerFormulaEditorDialog( (Dialog) window );
    } else {
      editorDialog = new DesignerFormulaEditorDialog();
    }

    final Configuration configuration = ReportDesignerBoot.getInstance().getGlobalConfig();
    final Iterator propertyKeys = configuration.findPropertyKeys( FUNCTION_EDITOR_CONFIX_PREFIX );
    while ( propertyKeys.hasNext() ) {
      final String key = (String) propertyKeys.next();
      final String function = key.substring( FUNCTION_EDITOR_CONFIX_PREFIX.length() );
      final String editor = configuration.getConfigProperty( key );
      final FunctionParameterEditor fnEditor =
        ObjectUtilities.loadAndInstantiate( editor, GUIUtils.class, FunctionParameterEditor.class );
      if ( fnEditor instanceof ReportDesignerFunctionParameterEditor ) {
        final ReportDesignerFunctionParameterEditor rfn = (ReportDesignerFunctionParameterEditor) fnEditor;
        rfn.setReportDesignerContext( context );
      }
      editorDialog.setEditor( function, fnEditor );
    }
    return editorDialog;
  }

}
