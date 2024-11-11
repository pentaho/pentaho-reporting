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


package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;

import java.util.Locale;
import java.util.Map;

/**
 * A decorator interface that allows an expression to validate it's parameters. The validation is returned as map of
 * fieldnames and validation messages. If global validation messages need to be published, these messages will be stored
 * with a <code>null</code> key.
 *
 * @author Thomas Morgner.
 */
public interface ValidateableExpression extends Expression {
  public Map validateParameter( final DesignTimeContext designTimeContext, final Locale locale );

}
