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

package org.pentaho.reporting.libraries.formula.function.database;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionCategory;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;

public class DatabaseFunctionCategory extends AbstractFunctionCategory {
  public static final FunctionCategory CATEGORY = new DatabaseFunctionCategory();

  private DatabaseFunctionCategory() {
    super( "org.pentaho.reporting.libraries.formula.function.database.category" );
  }
}

