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

import java.util.Arrays;

public abstract class AbstractStyleDataBackend implements StyleDataBackend {
  private static final Object[] EMPTY_VALUES = new Object[ 0 ];
  private static final StyleMetaData[] EMPTY_METADATA = new StyleMetaData[ 0 ];
  private static final GroupingHeader[] EMPTY_GROUPINGS = new GroupingHeader[ 0 ];

  private Object[] propertyEditors;
  private Object[] fullValues;
  private ResolverStyleSheet resolverStyleSheet;
  private StyleMetaData[] metaData;
  private GroupingHeader[] groupings;

  public AbstractStyleDataBackend() {
    this.metaData = EMPTY_METADATA;
    this.groupings = EMPTY_GROUPINGS;
    propertyEditors = EMPTY_VALUES;
    fullValues = EMPTY_VALUES;
    resolverStyleSheet = new ResolverStyleSheet();
  }

  protected AbstractStyleDataBackend( final StyleMetaData[] metaData,
                                      final GroupingHeader[] groupings ) {
    this.metaData = metaData;
    this.groupings = groupings;
    resolverStyleSheet = new ResolverStyleSheet();

    propertyEditors = new Object[ this.metaData.length ];
    fullValues = new Object[ this.metaData.length ];
  }

  public int getRowCount() {
    return metaData.length;
  }

  public StyleMetaData getMetaData( final int row ) {
    //noinspection ReturnOfCollectionOrArrayField, as this is for internal use only
    return metaData[ row ];
  }

  public GroupingHeader getGroupings( final int row ) {
    //noinspection ReturnOfCollectionOrArrayField, as this is for internal use only
    return groupings[ row ];
  }

  protected GroupingHeader[] getGroupings() {
    return groupings;
  }

  public void clearCache( final int rowIndex ) {
    fullValues[ rowIndex ] = null;
  }

  public void resetCache() {
    Arrays.fill( fullValues, null );
    resolverStyleSheet.clear();

  }

  public Object[] getFullValues() {
    return fullValues;
  }

  public Object[] getPropertyEditors() {
    return propertyEditors;
  }

  public ResolverStyleSheet getResolvedStyle() {
    return resolverStyleSheet;
  }
}
