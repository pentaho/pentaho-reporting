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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.lvalues;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.ErrorType;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

/**
 * A reference that queries the datarow.
 *
 * @author Thomas Morgner
 */
public class ContextLookup extends AbstractLValue {
  private String name;
  private static final long serialVersionUID = 2882834743999159722L;

  public ContextLookup( final String name ) {
    this( name, null );
  }

  public ContextLookup( final String name, final ParsePosition parsePosition ) {
    this.name = name;
    setParsePosition( parsePosition );
  }

  public TypeValuePair evaluate() throws EvaluationException {
    final FormulaContext context = getContext();
    final Type type = context.resolveReferenceType( name );
    final Object value = context.resolveReference( name );
    if ( value == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
    }
    return new TypeValuePair( type, value );
  }

  public Type getValueType() {
    try {
      final FormulaContext context = getContext();
      return context.resolveReferenceType( name );
    } catch ( final EvaluationException evalex ) {
      // exception ignored.
      return ErrorType.TYPE;
    }
  }

  public String toString() {
    return FormulaUtil.quoteReference( name );
  }

  /**
   * Checks whether the LValue is constant. Constant lvalues always return the same value.
   *
   * @return
   */
  public boolean isConstant() {
    return false;
  }

  public String getName() {
    return name;
  }
}
