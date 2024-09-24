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
