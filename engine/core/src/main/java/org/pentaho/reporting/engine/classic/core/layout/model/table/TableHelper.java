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


package org.pentaho.reporting.engine.classic.core.layout.model.table;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

public class TableHelper {
  public static TableRenderBox lookupTable( TableCellRenderBox box ) {
    final RenderBox layoutParent = box.getLayoutParent();
    if ( layoutParent == null ) {
      throw new IllegalStateException( "Missing table" );
    }

    if ( layoutParent.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_ROW ) {
      return lookupTable( (TableRowRenderBox) layoutParent );
    }
    throw new IllegalStateException( "Missing table" );
  }

  public static TableRenderBox lookupTable( TableRowRenderBox box ) {
    final RenderBox layoutParent = box.getLayoutParent();
    if ( layoutParent == null ) {
      throw new IllegalStateException( "Missing table" );
    }

    if ( layoutParent.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) {
      return lookupTable( (TableSectionRenderBox) layoutParent );
    }
    throw new IllegalStateException( "Missing table" );
  }

  public static TableRenderBox lookupTable( TableSectionRenderBox box ) {
    final RenderBox layoutParent = box.getLayoutParent();
    if ( layoutParent == null ) {
      throw new IllegalStateException( "Missing table" );
    }

    if ( layoutParent.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE ) {
      return (TableRenderBox) layoutParent;
    }
    throw new IllegalStateException( "Missing table" );
  }

}
