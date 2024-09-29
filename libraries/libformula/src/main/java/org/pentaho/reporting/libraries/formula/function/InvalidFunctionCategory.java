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


package org.pentaho.reporting.libraries.formula.function;

/**
 * This category is used when a problem occured while loading a function metadata category. It usualy points a problem
 * in the configuration file or a missing function metadata class.
 *
 * @author Cedric Pronzato
 */
public class InvalidFunctionCategory extends AbstractFunctionCategory {
  public static final FunctionCategory CATEGORY = new InvalidFunctionCategory();

  private InvalidFunctionCategory() {
    super( "org.pentaho.reporting.libraries.formula.function.invalid.category" );
  }
}
