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

public class SelectFileFromRepositoryTaskTest {

  Component uiContext;

  @Before
  public void setUp() throws Exception {
    uiContext = mock( Component.class );
  }

  @Test
  public void testSelectFileFromRepositoryTask() {
    SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    assertNotNull( task );
  }

  @Test
  public void testGetSetFilters() {
    SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    assertNotNull( task );
    assertNull( task.getFilters() );
    String[] filters = new String[] { "Manny", "Moe", "Jack" };
    task.setFilters( filters );
    assertArrayEquals( filters, task.getFilters() );
  }
}
