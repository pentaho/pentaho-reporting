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
import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;

/**
 * A content function is only valid when evaluating either the 'content' property or the 'string-set' property. These
 * functions produce ContentToken.
 * <p/>
 * ContentFunctions are only valid as first-level functions. That means, when evaluating nested function calls like
 * 'url(attr(blah))', the URL function will be resolved as ContentFunction, but the attr-function will be resolved as
 * ordinary CSSValue function.
 *
 * @author Thomas Morgner
 */
public interface ContentFunction {
  public ContentToken evaluate( final DocumentContext layoutProcess,
                                final LayoutElement element,
                                final CSSFunctionValue function )
    throws FunctionEvaluationException;

}
