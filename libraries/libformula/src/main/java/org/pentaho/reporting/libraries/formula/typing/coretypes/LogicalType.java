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


package org.pentaho.reporting.libraries.formula.typing.coretypes;

import org.pentaho.reporting.libraries.formula.typing.DefaultType;
import org.pentaho.reporting.libraries.formula.typing.Type;

/**
 * Creation-Date: 02.11.2006, 09:37:54
 *
 * @author Thomas Morgner
 */
//TODO: add scalar type?
public final class LogicalType extends DefaultType {
  public static final LogicalType TYPE = new LogicalType();
  private static final long serialVersionUID = -5308030940856879227L;

  private LogicalType() {
    addFlag( Type.LOGICAL_TYPE );
    addFlag( Type.NUMERIC_TYPE );
    addFlag( Type.SCALAR_TYPE );
    lock();
  }
}
