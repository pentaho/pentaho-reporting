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
import static org.mockito.Mockito.doReturn;

import java.awt.Component;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;

public class LoginTaskTest {

  ReportDesignerContext reportDesignerContext;
  Component component;
  AuthenticatedServerTask authServerTask;
  GlobalAuthenticationStore globalAuthStore;
  AuthenticationData loginData;

  @Before
  public void setUp() throws Exception {
    reportDesignerContext = mock( ReportDesignerContext.class );
    component = mock( Component.class );
    authServerTask = mock( AuthenticatedServerTask.class );
    globalAuthStore = mock( GlobalAuthenticationStore.class );
    loginData = mock( AuthenticationData.class );
  }

  @Test
  public void testLoginTaskReportDesignerContextComponentAuthenticatedServerTask() {
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();
    LoginTask loginTask = new LoginTask( reportDesignerContext, component, authServerTask );
    assertNotNull( loginTask );
  }

  @Test
  public void testLoginTaskReportDesignerContextComponentAuthenticatedServerTaskAuthenticationData() {
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();
    LoginTask loginTask = new LoginTask( reportDesignerContext, component, authServerTask, loginData );
    assertNotNull( loginTask );
  }

  @Test
  public void testLoginTaskReportDesignerContextComponentAuthenticatedServerTaskAuthenticationDataBoolean() {
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();
    LoginTask loginTask = new LoginTask( reportDesignerContext, component, authServerTask, loginData, true );
    assertNotNull( loginTask );
  }

  @Test
  public void testGetLoginData() {
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();
    LoginTask loginTask = new LoginTask( reportDesignerContext, component, authServerTask, loginData );
    assertNotNull( loginTask );
    assertEquals( loginData, loginTask.getLoginData() );
  }

}
