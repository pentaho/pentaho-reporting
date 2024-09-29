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


package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * Describes URLBuilderFunction function.
 *
 * @author Dmitriy Stepanov
 * @see URLBuilderFunction
 */
public class URLBuilderFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 6476113508712888095L;

  public URLBuilderFunctionDescription() {
    super( "URLBUILDER", "org.pentaho.reporting.libraries.formula.function.text.URLBuilder-Function" );
  }

  public FunctionCategory getCategory() {
    return TextFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 3;
  }

  public Type getParameterType( final int position ) {
    return TextType.TYPE;
  }

  public Type getValueType() {
    return TextType.TYPE;
  }

  public boolean isParameterMandatory( final int position ) {
    return position != 2;
  }

}
