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
