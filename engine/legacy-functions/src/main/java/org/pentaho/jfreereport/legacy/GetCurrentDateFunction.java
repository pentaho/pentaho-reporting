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

package org.pentaho.jfreereport.legacy;

import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;

import java.util.Date;

/**
 * @deprecated These functions are no longer supported.
 */
public class GetCurrentDateFunction extends AbstractFunction {


  public GetCurrentDateFunction() {
  }


  public GetCurrentDateFunction( final String name ) {
    this();
    setName( name );
  }


  public Object getValue() {
    return new Date();
  }
}
