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
