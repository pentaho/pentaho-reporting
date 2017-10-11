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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function;

import java.math.BigDecimal;
import java.util.ResourceBundle;

/**
 * A collection of utility methods which may be useful for expression-implementors.
 *
 * @author Thomas Morgner
 */
public class ExpressionUtilities {
  /**
   * Hidden default constructor.
   */
  private ExpressionUtilities() {
  }

  public static BigDecimal convertToBigDecimal( final Object o ) {
    if ( o instanceof Number ) {
      return convertToBigDecimal( (Number) o );
    }
    return null;
  }

  public static BigDecimal convertToBigDecimal( final Number number ) {
    if ( number == null ) {
      throw new NullPointerException();
    }

    if ( number instanceof BigDecimal ) {
      return (BigDecimal) number;
    } else if ( number instanceof Integer || number instanceof Long ) {
      return new BigDecimal( number.longValue() );
    }
    return new BigDecimal( number.toString() );
  }

  /**
   * Retursn the default resource-bundle. The name of the default resource-bundle is defined in the report
   * configuration.
   *
   * @param expression
   *          the expression that asked for the resource-bundle.
   * @return the resource-bundle.
   */
  public static ResourceBundle getDefaultResourceBundle( final Expression expression ) {
    if ( expression == null ) {
      throw new NullPointerException( "Expression is null" );
    }
    final String resourceBundleName =
        expression.getReportConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.ResourceBundle" );
    return expression.getResourceBundleFactory().getResourceBundle( resourceBundleName );
  }
}
