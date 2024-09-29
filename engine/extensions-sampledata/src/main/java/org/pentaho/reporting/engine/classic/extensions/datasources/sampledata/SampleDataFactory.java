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


package org.pentaho.reporting.engine.classic.extensions.datasources.sampledata;

import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;

public class SampleDataFactory extends SQLReportDataFactory {
  public SampleDataFactory() {
    super( createDefaultProvider() );
    setQuery( "default", "SELECT\n" +
      "CUSTOMERS.FIRST_NAME, CUSTOMERS.LAST_NAME,\n" +
      "PRODUCTS.PRODUCT_NAME, PRODUCTS.PRODUCT_DESCRIPTION, PRODUCTS.PRICE\n" +
      "FROM CUSTOMERS\n" +
      "JOIN ORDERS ON CUSTOMERS.CUSTOMER_ID=ORDERS.CUSTOMER_ID\n" +
      "JOIN ORDER_ITEMS ON ORDER_ITEMS.ORDER_ID=ORDERS.ORDER_ID\n" +
      "JOIN PRODUCTS ON ORDER_ITEMS.PRODUCT_ID=PRODUCTS.PRODUCT_ID\n" +
      "ORDER BY\n" +
      "CUSTOMERS.FIRST_NAME, CUSTOMERS.LAST_NAME, PRODUCTS.PRODUCT_NAME", null, null );
  }

  private static ConnectionProvider createDefaultProvider() {
    final DriverConnectionProvider drc = new DriverConnectionProvider();
    drc.setDriver( "org.hsqldb.jdbcDriver" );
    drc.setUrl( "jdbc:hsqldb:mem:SampleData" );
    drc.setProperty( "user", "sa" );
    drc.setProperty( "password", "" );
    return drc;
  }
}
