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
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;

public class PublishToServerActionTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testPublishToServerAction() {
    PublishToServerAction publishAction = new PublishToServerAction();
    assertNotNull( publishAction );
    assertEquals( Messages.getInstance().getString( "PublishToServerAction.Text" ), publishAction.getValue( Action.NAME ) );
    assertEquals( Messages.getInstance().getString( "PublishToServerAction.Description" ), publishAction.getValue( Action.SHORT_DESCRIPTION ) );
    assertEquals( Messages.getInstance().getString( "PublishToServerAction.Description" ), publishAction.getValue( Action.SHORT_DESCRIPTION ) );
    assertNotNull( publishAction.getValue( Action.SMALL_ICON ) );
    assertEquals( Messages.getInstance().getKeyStroke( "PublishToServerAction.Accelerator" ), publishAction.getValue( Action.ACCELERATOR_KEY ) );

  }

}
