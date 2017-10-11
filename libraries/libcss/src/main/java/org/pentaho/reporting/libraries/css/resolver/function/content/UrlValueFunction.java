/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
