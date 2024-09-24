/*
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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.typing.sequence;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.typing.ArrayCallback;
import org.pentaho.reporting.libraries.formula.typing.NumberSequence;
import org.pentaho.reporting.libraries.formula.typing.Type;

/**
 * @author Cedric Pronzato
 */
public class DefaultNumberSequence extends AnySequence implements NumberSequence {
  /**
   * Empty number sequence.
   */
  public DefaultNumberSequence( final FormulaContext context ) {
    super( context );
  }

  /**
   * Number sequence bounded to only one number item.
   *
   * @param n A number
   */
  public DefaultNumberSequence( final LValue n, final FormulaContext context ) {
    super( n, context );
  }

  /**
   * Number sequence bounded to an array.
   *
   * @param array
   */
  public DefaultNumberSequence( final ArrayCallback array, final FormulaContext context ) {
    super( array, context );
  }

  public DefaultNumberSequence( final AnySequence anySequence ) {
    super( anySequence );
  }

  protected boolean isValidNext( final LValue o ) throws EvaluationException {
    if ( o == null ) {
      return false;
    }
    final Type type = o.getValueType();
    if ( type == null ) {
      throw new IllegalStateException();
    }
    if ( type.isFlagSet( Type.NUMERIC_TYPE ) ) {
      return true;
    }
    return false;
  }

  public Number nextNumber() throws EvaluationException {
    final Object value = super.next();
    if ( value instanceof Number ) {
      return (Number) value;
    }

    return null;
  }
}
