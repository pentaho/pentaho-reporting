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
 * The function registry contains all information about all function available. It is also the central point from where
 * to get function meta-data or where to instantiate functions.
 * <p/>
 * All functions are queried by their cannonical name.
 *
 * @author Thomas Morgner
 */
public interface FunctionRegistry {
  public FunctionCategory[] getCategories();

  public Function[] getFunctions();

  public Function[] getFunctionsByCategory( FunctionCategory category );

  public String[] getFunctionNames();

  public String[] getFunctionNamesByCategory( FunctionCategory category );

  public Function createFunction( String name );

  public FunctionDescription getMetaData( String name );
}
