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
* Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
* All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import java.util.Locale;
import java.util.TimeZone;

public class Olap4JTestUtil {
  /**
   * A zero-dimensional query resulting a single-cell result-set, having no row and no column-dimensions. This can be
   * displayed by having the "field" property of column and row-groups set to &lt;null&gt;
   */
  private static final String QUERY_1 = "select from [SteelWheelsSales]";
  /**
   * A one-dimensional query. Results in a table with one dimension and a measure.
   */
  private static final String QUERY_2 = "select [Product].Children on 0 from [SteelWheelsSales]";
  /**
   * A two-dimensional query, where one axis is empty. The result-set has no measures. (this ends up empty because the
   * parent of 'all' is null, and null members are implicitly filtered)
   */
  private static final String QUERY_3 = "select [Product].parent on 0, [Time].Children on 1 from [SteelWheelsSales]";
  private static final String QUERY_3A = "select [Time].Children on 0, [Product].parent on 1 from [SteelWheelsSales]";

  /**
   * A two-dimensional query, where one axis is empty. The result-set has no measures. (this ends up empty because the
   * parent of 'all' is null, and null members are implicitly filtered)
   */
  private static final String QUERY_4 =
    "select crossjoin([Markets].Children, {[Measures].[Quantity], [Measures].[Sales]}) on 0, " +
      "crossjoin([Product].Children, [Time].Children) on 1 from [SteelWheelsSales]";

  /**
   * Same as query4, but measures are not right above the cell set (i.e. the last dimension on the columns axis)
   */
  private static final String QUERY_5 =
    "select crossjoin({[Measures].[Quantity], [Measures].[Sales]}, [Markets].Children) on 0, " +
      "crossjoin([Product].Children, [Time].Children) on 1 from [SteelWheelsSales]";

  /**
   * Same as query4 but with measures on the columns
   */
  private static final String QUERY_6 =
    "select crossjoin([Product].Children, [Markets].Children) on 0, crossjoin({[Measures].[Quantity], " +
      "[Measures].[Sales]}, [Time].Children) on 1 from [SteelWheelsSales]";

  /**
   * Cells with properties.
   */
  private static final String QUERY_7 = "with member [Measures].[Foo] as  ' [Measures].[Sales] / 2 ',\n" +
    "   format_string = '$#,###',\n" +
    "   back_color = 'yellow',  \n" +
    "   my_property = iif([Measures].CurrentMember > 10, \"foo\", \"bar\")\n" +
    "select {[Measures].[Foo], [Measures].[Sales]} on 0,\n" +
    " [Product].Children on 1\n" +
    "from [SteelWheelsSales]";

  /**
   * A query with a ragged hierarchy.
   */
  private static final String QUERY_8 = "select {[Markets].[All Markets].[APAC], [Markets].[All Markets].[EMEA], " +
    "[Markets].[All Markets].[Japan], [Markets].[All Markets], " +
    "[Markets].[All Markets].[NA]} ON COLUMNS,\n" +
    "  Hierarchize(Union(Union(Union(Union(Union(Union(Crossjoin({[Product].[All Products].[Classic Cars]}, " +
    "{[Time].[All Years].[2003], [Time].[All Years].[2004], [Time].[All Years].[2005]}), " +
    "Crossjoin({[Product].[All Products].[Motorcycles]}, {[Time].[All Years].[2003], " +
    "[Time].[All Years].[2004], [Time].[All Years].[2005]})), " +
    "Union(Crossjoin({[Product].[All Products].[Planes]}, {[Time].[All Years].[2003], " +
    "[Time].[All Years].[2004], [Time].[All Years].[2005]}), Crossjoin({[Product].[All Products].[Planes]}, " +
    "[Time].[All Years].[2004].Children))), Crossjoin({[Product].[All Products].[Ships]}, " +
    "{[Time].[All Years].[2003], [Time].[All Years].[2004], [Time].[All Years].[2005]})), " +
    "Crossjoin({[Product].[All Products].[Trains]}, {[Time].[All Years].[2003], " +
    "[Time].[All Years].[2004], [Time].[All Years].[2005]})), Crossjoin({[Product].[All Products].[Trucks " +
    "and Buses]}, {[Time].[All Years].[2003], [Time].[All Years].[2004], [Time].[All Years].[2005]})), " +
    "Crossjoin({[Product].[All Products].[Vintage Cars]}, {[Time].[All Years].[2003], " +
    "[Time].[All Years].[2004], [Time].[All Years].[2005]}))) ON ROWS\n" +
    "from [SteelWheelsSales]\n";

  /**
   * A query with a ragged hierarchy (flipped).
   */
  private static final String QUERY_9 = "select " +
    "  Hierarchize(Union(Union(Union(Union(Union(Union(Crossjoin({[Product].[All Products].[Classic Cars]}, " +
    "{[Time].[All Years].[2003], [Time].[All Years].[2004], [Time].[All Years].[2005]}), " +
    "Crossjoin({[Product].[All Products].[Motorcycles]}, {[Time].[All Years].[2003], " +
    "[Time].[All Years].[2004], [Time].[All Years].[2005]})), " +
    "Union(Crossjoin({[Product].[All Products].[Planes]}, {[Time].[All Years].[2003], " +
    "[Time].[All Years].[2004], [Time].[All Years].[2005]}), Crossjoin({[Product].[All Products].[Planes]}, " +
    "[Time].[All Years].[2004].Children))), Crossjoin({[Product].[All Products].[Ships]}, " +
    "{[Time].[All Years].[2003], [Time].[All Years].[2004], [Time].[All Years].[2005]})), " +
    "Crossjoin({[Product].[All Products].[Trains]}, {[Time].[All Years].[2003], " +
    "[Time].[All Years].[2004], [Time].[All Years].[2005]})), Crossjoin({[Product].[All Products].[Trucks " +
    "and Buses]}, {[Time].[All Years].[2003], [Time].[All Years].[2004], [Time].[All Years].[2005]})), " +
    "Crossjoin({[Product].[All Products].[Vintage Cars]}, {[Time].[All Years].[2003], " +
    "[Time].[All Years].[2004], [Time].[All Years].[2005]}))) " +
    "ON COLUMNS,\n" +
    "{[Markets].[All Markets].[APAC], [Markets].[All Markets].[EMEA], " +
    "[Markets].[All Markets].[Japan], [Markets].[All Markets], " +
    "[Markets].[All Markets].[NA]} " +
    "ON ROWS\n" +
    "from [SteelWheelsSales]\n";

  public static final String QUERY_UNION_OK = "SELECT\n" +
    " {[Time].[Years].[2003] : [Time].[Years].[2005]} ON COLUMNS,\n" +
    "NON EMPTY(\n" +
    "Union ( \n" +
    "[Product].Children * {[Markets].[All Markets], [Markets].Children},\n" +
    "[Product].[All Products] * [Markets].[All Markets] \n" +
    ") \n" +
    ") ON ROWS\n" +
    "FROM [SteelWheelsSales]\n" +
    "WHERE [Measures].[Quantity]\n";

  public static final String QUERY_UNION_FLIPPED = "SELECT\n" +
    " {[Time].[Years].[2003] : [Time].[Years].[2005]} ON ROWS,\n" +
    "NON EMPTY(\n" +
    "Union ( \n" +
    "[Product].Children * {[Markets].[All Markets], [Markets].Children},\n" +
    "[Product].[All Products] * [Markets].[All Markets] \n" +
    ") \n" +
    ") ON COLUMNS\n" +
    "FROM [SteelWheelsSales]\n" +
    "WHERE [Measures].[Quantity]\n";

  public static final String QUERY_UNION_BROKEN = "SELECT\n" +
    " {[Time].[Years].[2003] : [Time].[Years].[2005]} ON COLUMNS,\n" +
    "NON EMPTY(\n" +
    "Union ( \n" +
    "[Product].[All Products] * [Markets].[All Markets], \n" +
    "[Product].Children * {[Markets].[All Markets], [Markets].Children}\n" +
    ") \n" +
    ") ON ROWS\n" +
    "FROM [SteelWheelsSales]\n" +
    "WHERE [Measures].[Quantity]\n";

  private static final String QUERY_10 = "select NON EMPTY {[Measures].[Quantity],[Measures].[Sales]} ON COLUMNS,\n" +
    "NON EMPTY ([Time].Children) ON ROWS\n" +
    "from [SteelWheelsSales]";

  private static final String QUERY_11 =
    "SELECT [Product].Children ON COLUMNS, " +
      "Hierarchize({[Time].[Years].Members, [Time].[Quarters].Members, [Time].[Months].Members}) ON ROWS " +
      "FROM [SteelWheelsSales]";

  public static String[][] createQueryArray( final String id ) {
    return new String[][] {
      { QUERY_1, "query1" + id + "-results.txt" },
      { QUERY_2, "query2" + id + "-results.txt" },
      { QUERY_3, "query3" + id + "-results.txt" },
      { QUERY_3A, "query3a" + id + "-results.txt" },
      { QUERY_4, "query4" + id + "-results.txt" },
      { QUERY_5, "query5" + id + "-results.txt" },
      { QUERY_6, "query6" + id + "-results.txt" },
      { QUERY_7, "query7" + id + "-results.txt" },
      { QUERY_8, "query8" + id + "-results.txt" },
      { QUERY_9, "query9" + id + "-results.txt" },
      { QUERY_UNION_OK, "query-prd-5276-1" + id + "-results.txt" },
      { QUERY_UNION_BROKEN, "query-prd-5276-2" + id + "-results.txt" },
      { QUERY_UNION_FLIPPED, "query-prd-5276-3" + id + "-results.txt" },
      { QUERY_10, "query10" + id + "-results.txt" },
      { QUERY_11, "query11" + id + "-results.txt" },
    };
  }

  public static void main( String[] args ) throws Exception {
    Locale.setDefault( Locale.US );
    TimeZone.setDefault( TimeZone.getTimeZone( "UTC" ) );

    BandedOlap4JDriverT._main( args );
    DenormalizedOlap4JDriverT._main( args );
    LegacyBandedOlap4JDriverT._main( args );
    BandedMDXTableModelT._main( args );
  }
}
