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
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.actions.report.SaveReportAction;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;

public class PublishToServerActionTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() {
    PublishToServerAction.setPublishTaskFactory( null );
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

  @Test
  public void testSetPublishTaskFactoryCanBeRegisteredAndCleared() {
    PublishToServerAction.setPublishTaskFactory( context -> null );
    PublishToServerAction.setPublishTaskFactory( null );
    assertNotNull( new PublishToServerAction() );
  }

  @Test
  public void testActionPerformedNoActiveContext() {

    PublishToServerAction action =
      new PublishToServerAction();

    ReportDesignerContext ctx =
      mock( ReportDesignerContext.class );

    action.setReportDesignerContext( ctx );

    action.actionPerformed( null );

    assertTrue( true );
  }

    private ReportDesignerContext createContext(
      ReportDocumentContext activeContext ) {

      ReportDesignerContext ctx =
        mock( ReportDesignerContext.class );

      ReportDesignerView view =
        mock( ReportDesignerView.class );

      when( view.getParent() )
        .thenReturn( new JPanel() );

      when( ctx.getView() )
        .thenReturn( view );

      when( ctx.getActiveContext() )
        .thenReturn( activeContext );

      return ctx;
    }


  @Test
  public void testActionPerformedCancelOption() {

    ReportDocumentContext doc =
      mock( ReportDocumentContext.class );

    when( doc.isChanged() )
      .thenReturn( true );

    PublishToServerAction action =
      new PublishToServerAction();

    action.setReportDesignerContext(
      createContext( doc ) );

    try ( MockedStatic<JOptionPane> pane =
            mockStatic( JOptionPane.class ) ) {

      pane.when(
          () -> JOptionPane.showConfirmDialog(
            any(),
            any(),
            any(),
            anyInt(),
            anyInt() ) )
        .thenReturn( JOptionPane.CANCEL_OPTION );

      action.actionPerformed( null );

      assertTrue( true );
    }
  }

  @Test
  public void testActionPerformedSaveReturnsFalse() {

    ReportDocumentContext doc =
      mock( ReportDocumentContext.class );

    when( doc.isChanged() )
      .thenReturn( true );

    PublishToServerAction action =
      new PublishToServerAction();

    action.setReportDesignerContext(
      createContext( doc ) );

    try (
      MockedStatic<JOptionPane> pane =
        mockStatic( JOptionPane.class );

      MockedConstruction<SaveReportAction> saveMock =
        mockConstruction(
          SaveReportAction.class,
          ( mock, context ) ->
            when(
              mock.saveReport(
                any(),
                any(),
                any() ) )
              .thenReturn( false ) )
    ) {

      pane.when(
          () -> JOptionPane.showConfirmDialog(
            any(),
            any(),
            any(),
            anyInt(),
            anyInt() ) )
        .thenReturn( JOptionPane.YES_OPTION );

      action.actionPerformed( null );

      assertEquals(
        1,
        saveMock.constructed().size() );
    }
  }

  @Test
  public void testActionPerformedSaveReturnsTrueCoverage() {

    ReportDocumentContext doc = mock( ReportDocumentContext.class );
    when( doc.isChanged() ).thenReturn( true );

    PublishToServerAction action = new PublishToServerAction();
    action.setReportDesignerContext( createContext( doc ) );

    try (
      MockedStatic<JOptionPane> pane =
        mockStatic( JOptionPane.class );

      MockedConstruction<SaveReportAction> saveMock =
        mockConstruction(
          SaveReportAction.class,
          ( mock, context ) ->
            when(
              mock.saveReport(
                any(),
                any(),
                any() ) )
              .thenReturn( true ) );

      MockedConstruction<PublishToServerTask> publishTask =
        mockConstruction( PublishToServerTask.class );

      MockedConstruction<LoginTask> loginTask =
        mockConstruction( LoginTask.class )
    ) {

      pane.when(
          () -> JOptionPane.showConfirmDialog(
            any(),
            any(),
            any(),
            anyInt(),
            anyInt() ) )
        .thenReturn( JOptionPane.YES_OPTION );

      action.actionPerformed( null );

      assertEquals( 1, saveMock.constructed().size() );
      assertEquals( 1, publishTask.constructed().size() );
      assertEquals( 1, loginTask.constructed().size() );
    }
  }

  @Test
  public void testActionPerformedNoOptionCoverage() {

    ReportDocumentContext doc = mock( ReportDocumentContext.class );
    when( doc.isChanged() ).thenReturn( true );

    PublishToServerAction action = new PublishToServerAction();
    action.setReportDesignerContext( createContext( doc ) );

    try (
      MockedStatic<JOptionPane> pane =
        mockStatic( JOptionPane.class );

      MockedConstruction<PublishToServerTask> publishTask =
        mockConstruction( PublishToServerTask.class );

      MockedConstruction<LoginTask> loginTask =
        mockConstruction( LoginTask.class )
    ) {

      pane.when(
          () -> JOptionPane.showConfirmDialog(
            any(),
            any(),
            any(),
            anyInt(),
            anyInt() ) )
        .thenReturn( JOptionPane.NO_OPTION );

      action.actionPerformed( null );

      assertEquals( 1, publishTask.constructed().size() );
      assertEquals( 1, loginTask.constructed().size() );
    }
  }

  @Test
  public void testActionPerformedFactoryReturnsNullTaskCoverage() {

    ReportDocumentContext doc = mock( ReportDocumentContext.class );
    when( doc.isChanged() ).thenReturn( false );

    PublishToServerAction action = new PublishToServerAction();
    action.setReportDesignerContext( createContext( doc ) );

    PublishToServerAction.setPublishTaskFactory(
      c -> null );

    try (
      MockedConstruction<PublishToServerTask> publishTask =
        mockConstruction( PublishToServerTask.class );

      MockedConstruction<LoginTask> loginTask =
        mockConstruction( LoginTask.class )
    ) {

      action.actionPerformed( null );

      assertEquals( 1, publishTask.constructed().size() );
      assertEquals( 1, loginTask.constructed().size() );
    }
  }

  @Test
  public void testActionPerformedFactoryReturnsRunnable() {

    ReportDocumentContext doc =
      mock( ReportDocumentContext.class );

    when( doc.isChanged() )
      .thenReturn( false );

    PublishToServerAction action =
      new PublishToServerAction();

    action.setReportDesignerContext(
      createContext( doc ) );

    PublishToServerAction.setPublishTaskFactory(
      c -> () -> { } );

    action.actionPerformed( null );

    assertTrue( true );
  }

  @Test
  public void testActionPerformedDefaultFlow() {

    ReportDocumentContext doc =
      mock( ReportDocumentContext.class );

    when( doc.isChanged() )
      .thenReturn( false );

    PublishToServerAction action =
      new PublishToServerAction();

    action.setReportDesignerContext(
      createContext( doc ) );

    PublishToServerAction.setPublishTaskFactory( null );

    try (
      MockedConstruction<PublishToServerTask> publishTask =
        mockConstruction( PublishToServerTask.class );

      MockedConstruction<LoginTask> loginTask =
        mockConstruction( LoginTask.class )
    ) {

      action.actionPerformed( null );

      assertEquals(
        1,
        publishTask.constructed().size() );

      assertEquals(
        1,
        loginTask.constructed().size() );
    }
  }
}
