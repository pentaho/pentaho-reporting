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

package org.pentaho.reporting.engine.classic.core.layout.model;

/**
 * The page grid describes the logical page. That grid consists of PageGridAreas, which correspond to the usable
 * content-PageArea. PageGridAreas are synchronized against each other - the smallest width or height defines the
 * available column space.
 * <p/>
 * Modifications to PageAreas are only valid, if they are not locked.
 *
 * @author Thomas Morgner
 */
public interface PageGrid extends Cloneable {
  public PhysicalPageBox getPage( int row, int col );

  public int getRowCount();

  public int getColumnCount();

  public long[] getHorizontalBreaks();

  public Object clone() throws CloneNotSupportedException;
}
