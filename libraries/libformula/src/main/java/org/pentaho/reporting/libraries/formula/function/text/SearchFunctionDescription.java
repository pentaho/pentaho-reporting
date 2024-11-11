/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * Describes SearchFunction function.
 *
 * @author Cedric Pronzato
 * @see org.pentaho.reporting.libraries.formula.function.text.SearchFunction
 */
public class SearchFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 3300046053895569309L;

  public SearchFunctionDescription() {
    super( "SEARCH", "org.pentaho.reporting.libraries.formula.function.text.Search-Function" );
  }

  public FunctionCategory getCategory() {
    return TextFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 3;
  }

  public Type getParameterType( final int position ) {
    if ( position == 0 || position == 1 ) {
      return TextType.TYPE;
    }
    if ( position == 2 ) {
      return NumberType.GENERIC_NUMBER;
    }
    return null;
  }

  public Type getValueType() {
    return NumberType.GENERIC_NUMBER;
  }

  public boolean isParameterMandatory( final int position ) {
    if ( position == 0 || position == 1 ) {
      return true;
    } else {
      return false;
    }
  }

}
