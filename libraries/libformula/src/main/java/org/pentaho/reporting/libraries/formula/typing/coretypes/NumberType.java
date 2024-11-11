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
public class NumberType extends DefaultType {
  public static final NumberType GENERIC_NUMBER;
  public static final NumberType GENERIC_NUMBER_ARRAY;
  public static final NumberType NUMBER_SEQUENCE;
  private static final long serialVersionUID = 2070930250111567639L;

  static {
    GENERIC_NUMBER = new NumberType();
    GENERIC_NUMBER.addFlag( Type.SCALAR_TYPE );
    GENERIC_NUMBER.addFlag( Type.NUMERIC_TYPE );
    GENERIC_NUMBER.lock();

    GENERIC_NUMBER_ARRAY = new NumberType();
    GENERIC_NUMBER_ARRAY.addFlag( Type.ARRAY_TYPE );
    GENERIC_NUMBER_ARRAY.lock();

    NUMBER_SEQUENCE = new NumberType();
    NUMBER_SEQUENCE.addFlag( Type.SEQUENCE_TYPE );
    NUMBER_SEQUENCE.addFlag( Type.NUMERIC_SEQUENCE_TYPE );
    NUMBER_SEQUENCE.lock();
  }

  public NumberType() {
    addFlag( Type.NUMERIC_TYPE );
  }
}
