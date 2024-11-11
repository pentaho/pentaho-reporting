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
public final class TextType extends DefaultType {
  public static final TextType TYPE = new TextType();
  private static final long serialVersionUID = 6512540465045391761L;

  private TextType() {
    addFlag( Type.TEXT_TYPE );
    addFlag( Type.SCALAR_TYPE );
    lock();
  }
}
