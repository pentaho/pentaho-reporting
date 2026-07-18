/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;

public class LoadReportFromRepositoryActionTest {

  @Before
  public void setUp() {
    RepositorySession.clearSession();
  }

  @After
  public void tearDown() {
    RepositorySession.clearSession();
    LoadReportFromRepositoryAction.setOpenTaskFactory( null );
  }

  @Test
  public void testLoadReportFromRepositoryAction() {
    LoadReportFromRepositoryAction repoAction = new LoadReportFromRepositoryAction();
    assertEquals( Messages.getInstance().getString( "LoadReportFromRepositoryAction.Text" ),
        repoAction.getValue( Action.NAME ) );
    assertEquals( Messages.getInstance().getString( "LoadReportFromRepositoryAction.Description" ),
        repoAction.getValue( Action.SHORT_DESCRIPTION ) );
    assertEquals( IconLoader.getInstance().getOpenIcon(), repoAction.getValue( Action.SMALL_ICON ) );
    assertEquals( Messages.getInstance().getOptionalKeyStroke( "LoadReportFromRepositoryAction.Accelerator" ),
        repoAction.getValue( Action.ACCELERATOR_KEY ) );
  }

  @Test
  public void testActionPerformedNullContextReturnsEarly() {
    // No context set → actionPerformed should return immediately without NPE
    final LoadReportFromRepositoryAction action = new LoadReportFromRepositoryAction();
    action.actionPerformed( mock( ActionEvent.class ) ); // no exception
    assertNotNull( action );
  }

  @Test
  public void testActionPerformedWithContextSchedulesLoginTask() {
    final ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    final ReportDesignerView view = mock( ReportDesignerView.class );
    final Component parent = mock( Component.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );

    try ( MockedConstruction<OpenFileFromRepositoryTask> ignored1 =
              mockConstruction( OpenFileFromRepositoryTask.class );
          MockedConstruction<LoginTask> ignored2 =
              mockConstruction( LoginTask.class );
          MockedStatic<SwingUtilities> swingUtils = mockStatic( SwingUtilities.class ) ) {

      final LoadReportFromRepositoryAction action = new LoadReportFromRepositoryAction();
      action.setReportDesignerContext( ctx );
      action.actionPerformed( mock( ActionEvent.class ) );

      swingUtils.verify( () -> SwingUtilities.invokeLater( any( LoginTask.class ) ) );
    }
  }

  @Test
  public void testActionPerformedWithFactoryRoutesToFactoryTask() {
    // When an EE-style factory is registered, the action must schedule the factory's task and
    // must NOT fall back to the standard LoginTask/OpenFileFromRepositoryTask flow.
    final ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    final ReportDesignerView view = mock( ReportDesignerView.class );
    final Component parent = mock( Component.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );

    final Runnable factoryTask = mock( Runnable.class );
    LoadReportFromRepositoryAction.setOpenTaskFactory( context -> factoryTask );

    try ( MockedConstruction<LoginTask> loginCtor = mockConstruction( LoginTask.class );
          MockedStatic<SwingUtilities> swingUtils = mockStatic( SwingUtilities.class ) ) {

      final LoadReportFromRepositoryAction action = new LoadReportFromRepositoryAction();
      action.setReportDesignerContext( ctx );
      action.actionPerformed( mock( ActionEvent.class ) );

      swingUtils.verify( () -> SwingUtilities.invokeLater( factoryTask ) );
      org.junit.Assert.assertEquals( 0, loginCtor.constructed().size() );
    }
  }

  @Test
  public void testActionPerformedFactoryReturningNullFallsBackToStandardFlow() {
    final ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    final ReportDesignerView view = mock( ReportDesignerView.class );
    final Component parent = mock( Component.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );

    LoadReportFromRepositoryAction.setOpenTaskFactory( context -> null );

    try ( MockedConstruction<OpenFileFromRepositoryTask> ignored1 =
              mockConstruction( OpenFileFromRepositoryTask.class );
          MockedConstruction<LoginTask> ignored2 =
              mockConstruction( LoginTask.class );
          MockedStatic<SwingUtilities> swingUtils = mockStatic( SwingUtilities.class ) ) {

      final LoadReportFromRepositoryAction action = new LoadReportFromRepositoryAction();
      action.setReportDesignerContext( ctx );
      action.actionPerformed( mock( ActionEvent.class ) );

      swingUtils.verify( () -> SwingUtilities.invokeLater( any( LoginTask.class ) ) );
    }
  }
}

