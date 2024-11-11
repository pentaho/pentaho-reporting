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


package org.pentaho.reporting.designer.core.util.table;

import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.BasicTextPropertyEditorDialog;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.PropertyCellEditorWithEllipsis;

import java.awt.*;

public class DesignerPropertyCellEditorWithEllipsis extends PropertyCellEditorWithEllipsis {
  public DesignerPropertyCellEditorWithEllipsis() {
  }


  protected BasicTextPropertyEditorDialog createTextEditorDialog() {
    final Window window = LibSwingUtil.getWindowAncestor( DesignerPropertyCellEditorWithEllipsis.this );

    final TextAreaPropertyEditorDialog editorDialog;
    if ( window instanceof Frame ) {
      editorDialog = new TextAreaPropertyEditorDialog( (Frame) window );
    } else if ( window instanceof Dialog ) {
      editorDialog = new TextAreaPropertyEditorDialog( (Dialog) window );
    } else {
      editorDialog = new TextAreaPropertyEditorDialog();
    }
    return editorDialog;
  }

}
