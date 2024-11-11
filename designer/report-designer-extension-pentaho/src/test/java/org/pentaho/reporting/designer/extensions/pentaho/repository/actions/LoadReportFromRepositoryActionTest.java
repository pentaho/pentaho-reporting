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
