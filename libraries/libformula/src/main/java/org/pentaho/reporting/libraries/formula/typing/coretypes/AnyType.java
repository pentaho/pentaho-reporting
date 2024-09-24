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
public final class AnyType extends DefaultType {
  public static final AnyType TYPE;
  public static final AnyType ANY_ARRAY;
  public static final AnyType ANY_SEQUENCE;
  private static final long serialVersionUID = 5871721530497016577L;

  static {
    TYPE = new AnyType();
    TYPE.addFlag( Type.SCALAR_TYPE );
    TYPE.lock();

    ANY_ARRAY = new AnyType();
    ANY_ARRAY.addFlag( Type.ARRAY_TYPE );
    ANY_ARRAY.lock();

    ANY_SEQUENCE = new AnyType();
    ANY_SEQUENCE.addFlag( Type.SEQUENCE_TYPE );
    ANY_SEQUENCE.lock();
  }

  private AnyType() {
    addFlag( Type.ANY_TYPE );
  }
}
