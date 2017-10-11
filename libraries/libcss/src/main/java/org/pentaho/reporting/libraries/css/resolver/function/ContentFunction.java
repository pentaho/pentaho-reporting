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
