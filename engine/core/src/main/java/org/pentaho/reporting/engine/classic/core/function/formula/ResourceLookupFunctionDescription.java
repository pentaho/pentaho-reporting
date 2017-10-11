/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.function.text.TextFunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * Created by dima.prokopenko@gmail.com on 9/29/2016.
 */
public class ResourceLookupFunctionDescription extends AbstractFunctionDescription {
  public ResourceLookupFunctionDescription() {
    super( ResourceLookupFunction.NAME,
      "org.pentaho.reporting.engine.classic.core.function.formula.ResourceLookupFunction" );
  }

  @Override public Type getValueType() {
    return TextType.TYPE;
  }

  @Override public FunctionCategory getCategory() {
    return TextFunctionCategory.CATEGORY;
  }

  @Override public int getParameterCount() {
    return 2;
  }

  @Override public Type getParameterType( int position ) {
    return TextType.TYPE;
  }

  @Override public boolean isParameterMandatory( int position ) {
    return position == 0 || position == 1;
  }
}
