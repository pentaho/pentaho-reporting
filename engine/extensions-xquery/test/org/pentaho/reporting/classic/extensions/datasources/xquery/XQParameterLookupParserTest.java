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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.classic.extensions.datasources.xquery;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.extensions.datasources.xquery.XQParameterLookupParser;

/**
 *
 */
public class XQParameterLookupParserTest extends TestCase
{

  public static final String TEST1 = "declare variable $x as xs:integer external;  \n" +
      "declare variable $x external;\n" +
      "vxcvcxv;\n" +
      "vcxv  vxcv";
  public static final String TEST2 = "declare variable $x as xs:integer external;  \n" +
      "declare variable ${report.name} as xs:string external;  \n" +
      "vxcvcxv;\n" +
      "vcxv  vxcv";
  public static final String TEST3 = "declare variable $x as xs:integer external;  \n" +
      "declare variable $report.name as xs:string external;  \n" +
      "vxcvcxv;\n" +
      "vcxv  vxcv";


  public void testConventionalExternalDeclaration()
  {
    final XQParameterLookupParser parser = new XQParameterLookupParser();
    final String s = parser.translateAndLookup(TEST1);
    assertEquals("No fields should have been detected", 0, parser.getFields().size());
    assertEquals("The query content should stay the same as the original query", TEST1, s);
  }

  public void testReportingExternalDeclaration()
  {
    final XQParameterLookupParser parser = new XQParameterLookupParser();
    final String s = parser.translateAndLookup(TEST2);
    assertEquals("report.name property not found", 1, parser.getFields().size());
    assertEquals("report.name property not saved", "report.name", parser.getFields().get(0));
    assertEquals("The query content should stay the same as the original query", TEST3, s);
  }

}
