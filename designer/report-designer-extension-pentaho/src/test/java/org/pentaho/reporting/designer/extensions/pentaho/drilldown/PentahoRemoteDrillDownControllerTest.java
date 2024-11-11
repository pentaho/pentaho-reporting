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


package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;

public class PentahoRemoteDrillDownControllerTest {

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
    PentahoRemoteDrillDownController drillDownController = new PentahoRemoteDrillDownController();
    assertNotNull( drillDownController );
    String profileName = drillDownController.getProfileName();
    assert ( profileName.equals( "pentaho" ) );
  }

  @Test
  public void testPentahoRemoteDrillDownController() {
    PentahoRemoteDrillDownController drillDownController = new PentahoRemoteDrillDownController();
    assertNotNull( drillDownController );
  }

  @Test
  public void testFilterParameterDrillDownParameterArray() {
    DrillDownParameter[] drillDownParameters = {
      drillDownParameterSolution,
      drillDownParameterPath,
      drillDownParameterName
    };
    PentahoRemoteDrillDownController remoteDrillDownControllerSpy = spy( new PentahoRemoteDrillDownController() );
    doReturn( pathWrapper ).when( remoteDrillDownControllerSpy ).getPentahoPathWrapper();
    DrillDownParameter[] result = remoteDrillDownControllerSpy.filterParameter( drillDownParameters );
    assertNotNull( result );
    assertEquals( result.length, 0 );

    doReturn( "testSolution" ).when( pathWrapper ).getSolution();
    doReturn( "path:in:solution" ).when( pathWrapper ).getPath();
    doReturn( "filename" ).when( pathWrapper ).getName();
    doReturn( "solution" ).when( drillDownParameterSolution ).getName();
    doReturn( "path" ).when( drillDownParameterPath ).getName();
    doReturn( "name" ).when( drillDownParameterName ).getName();

    result = remoteDrillDownControllerSpy.filterParameter( drillDownParameters );
    assertNotNull( result );
    assertEquals( result.length, 3 );
  }

}
