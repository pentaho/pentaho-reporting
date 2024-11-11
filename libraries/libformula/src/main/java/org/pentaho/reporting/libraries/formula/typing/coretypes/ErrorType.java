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


package org.pentaho.reporting.libraries.formula.typing.coretypes;

import org.pentaho.reporting.libraries.formula.typing.DefaultType;
import org.pentaho.reporting.libraries.formula.typing.Type;

/**
 * Creation-Date: 02.11.2006, 09:37:54
 *
 * @author Thomas Morgner
 */
//add scalar flag?
public final class ErrorType extends DefaultType {
  public static final Type TYPE = new ErrorType();
  private static final long serialVersionUID = -5893173280337804811L;

  private ErrorType() {
    addFlag( Type.ERROR_TYPE );
    addFlag( Type.SCALAR_TYPE );
    lock();
  }
}
