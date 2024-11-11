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


package org.pentaho.reporting.engine.classic.extensions.modules.connections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MessagesIT {

  private static final String INCORRECT_KEY = "test_msg_key";

  @Test
  public void testGetInstance() {
    assertThat( Messages.getInstance(), is( notNullValue() ) );

    String msg = Messages.getInstance().getString( INCORRECT_KEY );
    assertThat( msg, is( equalTo( "!" + INCORRECT_KEY + "!" ) ) );
  }
}
