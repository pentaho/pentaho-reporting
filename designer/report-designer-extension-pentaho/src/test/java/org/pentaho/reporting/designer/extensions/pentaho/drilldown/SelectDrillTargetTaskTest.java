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

package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.awt.Component;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;

public class SelectDrillTargetTaskTest {

  PentahoPathModel wrapper;
  Component uiContext;
  Runnable triggerRefreshParameterTask;
  ReportDocumentContext activeContext;
  AuthenticationData loginData;

  @Before
  public void setUp() throws Exception {
    wrapper = mock( PentahoPathModel.class );
    uiContext = mock( Component.class );
    triggerRefreshParameterTask = mock( Runnable.class );
    activeContext = mock( ReportDocumentContext.class );
    loginData = mock( AuthenticationData.class );
  }

  @Test
  public void testSelectDrillTargetTask() {
    SelectDrillTargetTask selectDrillTargetTask =
        new SelectDrillTargetTask( wrapper, uiContext, triggerRefreshParameterTask, activeContext );
    assertNotNull( selectDrillTargetTask );
  }

  @Test
  public void testSetLoginData() {
    SelectDrillTargetTask selectDrillTargetTask =
        new SelectDrillTargetTask( wrapper, uiContext, triggerRefreshParameterTask, activeContext );
    assertNotNull( selectDrillTargetTask );
    selectDrillTargetTask.setLoginData( loginData, true );
    assertEquals( loginData, selectDrillTargetTask.getLoginData() );
  }

}
