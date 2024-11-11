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


package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionCategory;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;

/**
 * Creation-Date: 05.11.2006, 15:05:38
 *
 * @author Thomas Morgner
 */
public class UserDefinedFunctionCategory extends AbstractFunctionCategory {
  public static final FunctionCategory CATEGORY = new UserDefinedFunctionCategory();

  private UserDefinedFunctionCategory() {
    super( "org.pentaho.reporting.libraries.formula.function.userdefined.category" );
  }
}
