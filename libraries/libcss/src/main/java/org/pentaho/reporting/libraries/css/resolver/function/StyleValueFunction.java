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

package org.pentaho.reporting.libraries.css.resolver.function;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.resolver.FunctionEvaluationException;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 15.04.2006, 18:34:06
 *
 * @author Thomas Morgner
 */
public interface StyleValueFunction {
  public CSSValue evaluate( final DocumentContext layoutProcess,
                            final LayoutElement element,
                            final CSSFunctionValue function )
    throws FunctionEvaluationException;

  public boolean isAutoResolveable();
}
