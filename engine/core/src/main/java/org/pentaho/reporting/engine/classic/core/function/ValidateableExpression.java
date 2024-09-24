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
