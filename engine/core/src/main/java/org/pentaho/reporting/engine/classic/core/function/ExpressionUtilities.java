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
