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

package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class SequenceQuoterFunctionDescription extends AbstractFunctionDescription {

  private static final long serialVersionUID = -5731648885718950878L;

  public SequenceQuoterFunctionDescription() {
    super( "SEQUENCEQUOTER", "org.pentaho.reporting.libraries.formula.function.userdefined.SequenceQuoter-Function" );
  }

  @Override
  public Type getValueType() {
    return TextType.TYPE;
  }

  @Override
  public FunctionCategory getCategory() {
    return UserDefinedFunctionCategory.CATEGORY;
  }

  @Override
  public int getParameterCount() {
    return 3;
  }

  @Override
  public Type getParameterType( int position ) {
    if ( position == 0 ) {
      return AnyType.ANY_ARRAY;
    }
    return TextType.TYPE;
  }

  @Override
  public boolean isParameterMandatory( int position ) {
    if ( position == 0 ) {
      return true;
    }
    return false;
  }

}
