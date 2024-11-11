/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
