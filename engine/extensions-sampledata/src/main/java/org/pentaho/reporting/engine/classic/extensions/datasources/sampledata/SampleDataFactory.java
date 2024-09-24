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
