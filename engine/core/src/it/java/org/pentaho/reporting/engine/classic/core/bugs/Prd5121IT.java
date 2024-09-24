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

package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.ui.Messages;

public class Prd5121IT {
  @Test
  public void testMessages() {
    Assert.assertEquals( "Add", Messages.getString( "QueryAddAction.Name" ) );
  }
}
