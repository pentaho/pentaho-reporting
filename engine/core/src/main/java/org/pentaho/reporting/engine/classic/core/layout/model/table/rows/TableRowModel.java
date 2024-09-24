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

package org.pentaho.reporting.engine.classic.core.layout.model.table.rows;

import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * The table row model is responsible for calculating cell heights and for distributing extra space for cells and rows.
 * It also deals with gathering evidence for cell sizes of row-spanned cells.
 *
 * @author Thomas Morgner
 */
public interface TableRowModel {
  public void addRow();

  public int getRowCount();

  public void validatePreferredSizes();

  public void validateActualSizes();

  public void initialize( TableRenderBox table );

  public void prune( int rows );

  public void clear();

  void updateDefinedSize( int rowNumber, int rowSpan, long preferredSize );

  void updateValidatedSize( int rowNumber, int rowSpan, long leading, long height );

  long getValidatedRowSize( int rowNumber );

  long getPreferredRowSize( int rowNumber );

  public int getMaximumRowSpan( int rowNumber );

  void setDebugInformation( ElementType elementType, InstanceID instanceID );
}
