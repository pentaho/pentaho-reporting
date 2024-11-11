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


package org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel;

import java.io.Serializable;

import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;

public interface TableMetaData extends Serializable {
  DataAttributes getCellDataAttribute( final int row,
                                       final int column );

  boolean isCellDataAttributesSupported();

  DataAttributes getColumnAttribute( final int column );

  DataAttributes getTableAttribute();

}
