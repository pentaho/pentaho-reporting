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

package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;

public class PentahoSugarDrillDownControllerTest {

  DrillDownParameter drillDownParameterSolution;
  DrillDownParameter drillDownParameterPath;
  DrillDownParameter drillDownParameterName;
  PentahoPathModel pathWrapper;

  @Before
  public void setUp() throws Exception {
    drillDownParameterSolution = mock( DrillDownParameter.class );
    drillDownParameterPath = mock( DrillDownParameter.class );
    drillDownParameterName = mock( DrillDownParameter.class );
    pathWrapper = mock( PentahoPathModel.class );
  }

  @Test
  public void testGetProfileName() {
    PentahoSugarDrillDownController drillDownController = new PentahoSugarDrillDownController();
    assertNotNull( drillDownController );
    String profileName = drillDownController.getProfileName();
    assert ( profileName.equals( "pentaho-sugar" ) );
  }

  @Test
  public void testPentahoSugarDrillDownController() {
    PentahoSugarDrillDownController drillDownController = new PentahoSugarDrillDownController();
    assertNotNull( drillDownController );
  }

  @Test
  public void testFilterParameterDrillDownParameterArray() {
    DrillDownParameter[] drillDownParameters = {
      drillDownParameterSolution,
      drillDownParameterPath,
      drillDownParameterName
    };
    PentahoSugarDrillDownController remoteDrillDownControllerSpy = spy( new PentahoSugarDrillDownController() );
    doReturn( pathWrapper ).when( remoteDrillDownControllerSpy ).getPentahoPathWrapper();
    DrillDownParameter[] result = remoteDrillDownControllerSpy.filterParameter( drillDownParameters );
    assertNotNull( result );
    assertEquals( 0, result.length );

    doReturn( "testSolution" ).when( pathWrapper ).getSolution();
    doReturn( "path:in:solution" ).when( pathWrapper ).getPath();
    doReturn( "filename" ).when( pathWrapper ).getName();
    doReturn( "testSolution/path/in/solution/filename" ).when( pathWrapper ).getLocalPath();
    doReturn( "solution" ).when( drillDownParameterSolution ).getName();
    doReturn( "path" ).when( drillDownParameterPath ).getName();
    doReturn( "name" ).when( drillDownParameterName ).getName();
    doReturn( "testSolution" ).when( drillDownParameterSolution ).getFormulaFragment();
    doReturn( "path:in:solution" ).when( drillDownParameterPath ).getFormulaFragment();
    doReturn( "filename" ).when( drillDownParameterName ).getFormulaFragment();

    result = remoteDrillDownControllerSpy.filterParameter( drillDownParameters );
    assertNotNull( result );
    assertEquals( 1, result.length );
  }

}
