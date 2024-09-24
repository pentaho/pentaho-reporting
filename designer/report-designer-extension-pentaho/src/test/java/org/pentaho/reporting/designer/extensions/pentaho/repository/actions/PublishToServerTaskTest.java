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

package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.awt.Component;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;

public class PublishToServerTaskTest {

  ReportDesignerContext reportDesignerContext;
  Component uiContext;
  AuthenticationData loginData;

  @Before
  public void setUp() throws Exception {
    reportDesignerContext = mock( ReportDesignerContext.class );
    uiContext = mock( Component.class );
    loginData = mock( AuthenticationData.class );
  }

  @Test
  public void testPublishToServerTask() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    assertNotNull( task );
  }

  @Test
  public void testSetLoginData() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    assertNotNull( task );
    task.setLoginData( loginData, true );
  }

}
