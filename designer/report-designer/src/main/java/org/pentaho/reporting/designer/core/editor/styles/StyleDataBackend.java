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

package org.pentaho.reporting.designer.core.editor.styles;

import org.pentaho.reporting.designer.core.util.table.GroupingHeader;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;

public interface StyleDataBackend {
  public static final Object NULL_INDICATOR = new Object();

  int getRowCount();

  StyleMetaData getMetaData( int row );

  GroupingHeader getGroupings( int row );

  void resetCache();

  void clearCache( int rowIndex );

  ResolverStyleSheet getResolvedStyle();

  Object[] getFullValues();

  Object[] getPropertyEditors();
}
