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

import javax.swing.Action;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;

public class LoadReportFromRepositoryActionTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testLoadReportFromRepositoryAction() {
    LoadReportFromRepositoryAction repoAction = new LoadReportFromRepositoryAction();
    assertEquals( Messages.getInstance().getString( "LoadReportFromRepositoryAction.Text" ), repoAction.getValue( Action.NAME  ) );
    assertEquals( Messages.getInstance().getString( "LoadReportFromRepositoryAction.Description" ), repoAction.getValue( Action.SHORT_DESCRIPTION ) );
    assertEquals( IconLoader.getInstance().getOpenIcon(), repoAction.getValue( Action.SMALL_ICON ) );
    assertEquals( Messages.getInstance().getOptionalKeyStroke( "LoadReportFromRepositoryAction.Accelerator" ), repoAction.getValue( Action.ACCELERATOR_KEY ) );
  }
}
