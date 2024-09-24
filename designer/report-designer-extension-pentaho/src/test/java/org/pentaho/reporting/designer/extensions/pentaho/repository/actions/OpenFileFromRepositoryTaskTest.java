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
