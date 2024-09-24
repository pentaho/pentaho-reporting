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
