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


package org.pentaho.reporting.libraries.css.resolver.function.content;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.resolver.FunctionEvaluationException;
import org.pentaho.reporting.libraries.css.resolver.function.ContentFunction;
import org.pentaho.reporting.libraries.css.resolver.function.FunctionUtilities;
import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.statics.ResourceContentToken;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSResourceValue;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.resourceloader.Resource;

/**
 * Creation-Date: 16.04.2006, 14:14:42
 *
 * @author Thomas Morgner
 */
public class UrlValueFunction implements ContentFunction {
  public UrlValueFunction() {
  }

  public ContentToken evaluate( final DocumentContext layoutProcess,
                                final LayoutElement element,
                                final CSSFunctionValue function )
    throws FunctionEvaluationException {
    final CSSValue[] params = function.getParameters();
    if ( params.length != 1 ) {
      throw new FunctionEvaluationException();
    }
    final CSSValue value = FunctionUtilities.resolveParameter( layoutProcess, element, params[ 0 ] );
    if ( value instanceof CSSResourceValue ) {
      final CSSResourceValue cssResourceValue =
        (CSSResourceValue) value;
      final Resource resource = cssResourceValue.getValue();
      return new ResourceContentToken( resource );
    }
    if ( value instanceof CSSStringValue == false ) {
      throw new FunctionEvaluationException
        ( "Not even remotely an URI: " + value );
    }
    final CSSStringValue strval = (CSSStringValue) value;
    final CSSResourceValue cssResourceValue =
      FunctionUtilities.loadResource( layoutProcess, strval.getValue() );
    final Resource resource = cssResourceValue.getValue();
    return new ResourceContentToken( resource );
  }
}
