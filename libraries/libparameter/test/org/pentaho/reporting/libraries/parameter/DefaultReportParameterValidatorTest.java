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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.libraries.parameter;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.parameter.defaults.DefaultListParameter;
import org.pentaho.reporting.libraries.parameter.defaults.DefaultParameterContext;
import org.pentaho.reporting.libraries.parameter.defaults.DefaultParameterDataTable;
import org.pentaho.reporting.libraries.parameter.defaults.DefaultParameterDefinition;
import org.pentaho.reporting.libraries.parameter.defaults.DefaultParameterQuery;
import org.pentaho.reporting.libraries.parameter.validation.DefaultParameterValidationResult;
import org.pentaho.reporting.libraries.parameter.validation.DefaultParameterValidator;

public class DefaultReportParameterValidatorTest extends TestCase
{
  public DefaultReportParameterValidatorTest()
  {
  }

  public DefaultReportParameterValidatorTest(final String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    LibParameterBoot.getInstance().start();
  }

  public void testSelectDefault() throws Exception
  {
    final DefaultParameterDataTable tableModel = new DefaultParameterDataTable("key", "value");
    tableModel.setValue("key", 0, "key-value");
    tableModel.setValue("value", 0, "value-entry");

    final DefaultParameterQuery query = new DefaultParameterQuery();
    query.setQuery("test-query", tableModel);

    final DefaultListParameter listParameter =
        new DefaultListParameter("test", String.class, "test-query", "key");
    listParameter.setAllowResetOnInvalidValue(true);
    listParameter.setAllowMultiSelection(false);
    listParameter.setStrictValueCheck(true);
    listParameter.setAutoSelectFirstValue(true);
    listParameter.setMandatory(true);

    final DefaultParameterDefinition definition = new DefaultParameterDefinition();
    definition.addParameter(listParameter);

    final DefaultParameterContext paramContext = new DefaultParameterContext();
    paramContext.setDataFactory(query);

    final DefaultParameterValidator validator = new DefaultParameterValidator();
    final ParameterValidationResult result = validator.validate
        (new DefaultParameterValidationResult(), definition, paramContext);
    assertTrue(result.isParameterSetValid());
    
  }
}
