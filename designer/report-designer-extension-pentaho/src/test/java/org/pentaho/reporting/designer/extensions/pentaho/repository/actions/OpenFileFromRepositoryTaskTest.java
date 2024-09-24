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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.awt.Component;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;

public class OpenFileFromRepositoryTaskTest {

  ReportDesignerContext designerContext;
  Component uiContext;
  AuthenticationData loginData;

  @Before
  public void setUp() throws Exception {
    designerContext = mock( ReportDesignerContext.class );
    uiContext = mock( Component.class );
    loginData = mock( AuthenticationData.class );
  }

  @Test
  public void testOpenFileFromRepositoryTask() {
    OpenFileFromRepositoryTask openFileTask = new OpenFileFromRepositoryTask( designerContext, uiContext );
    assertNotNull( openFileTask );
  }

  @Test
  public void testSetLoginData() {
    OpenFileFromRepositoryTask openFileTask = new OpenFileFromRepositoryTask( designerContext, uiContext );
    openFileTask.setLoginData( loginData, true );
    assertNotNull( openFileTask );

  }
}
