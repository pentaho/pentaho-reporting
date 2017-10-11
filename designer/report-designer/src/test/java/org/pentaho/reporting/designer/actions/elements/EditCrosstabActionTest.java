/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.actions.elements;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.actions.elements.EditCrosstabAction;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.testsupport.TestReportDesignerContext;
import org.pentaho.reporting.engine.classic.core.CrosstabElement;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.SubReport;

import java.awt.event.ActionEvent;

public class EditCrosstabActionTest {
  @Before
  public void setUp() throws Exception {
    ReportDesignerBoot.getInstance().start();
  }

  @Test
  public void testSelectionOnCrosstabReport() {

    WorkspaceSettings.getInstance().setExperimentalFeaturesVisible( true );

    final MasterReport report = new MasterReport();
    final CrosstabElement element = new CrosstabElement();
    report.getReportHeader().addElement( element );

    final TestReportDesignerContext rdc = new TestReportDesignerContext();
    rdc.addMasterReport( report );
    rdc.addSubReport( (ReportDocumentContext) rdc.getDocumentContext( 0 ), element );
    rdc.setActiveDocument( rdc.getDocumentContext( 1 ) );
    final ReportDocumentContext activeContext = rdc.getActiveContext();
    Assert.assertNotNull( activeContext );
    Assert.assertEquals( activeContext.getReportDefinition(), element );

    EditCrosstabAction action = new EditCrosstabAction();
    action.setReportDesignerContext( rdc );
    Assert.assertTrue( action.isEnabled() );
  }

  @Test
  public void testSelectionOnSubReport() {

    WorkspaceSettings.getInstance().setExperimentalFeaturesVisible( true );

    final MasterReport report = new MasterReport();
    final SubReport element = new SubReport();
    report.getReportHeader().addElement( element );

    final TestReportDesignerContext rdc = new TestReportDesignerContext();
    rdc.addMasterReport( report );
    rdc.addSubReport( (ReportDocumentContext) rdc.getDocumentContext( 0 ), element );
    rdc.setActiveDocument( rdc.getDocumentContext( 1 ) );
    final ReportDocumentContext activeContext = rdc.getActiveContext();
    Assert.assertNotNull( activeContext );
    Assert.assertEquals( activeContext.getReportDefinition(), element );

    EditCrosstabAction action = new EditCrosstabAction();
    action.setReportDesignerContext( rdc );
    Assert.assertFalse( action.isEnabled() );
  }

  @Test
  public void testSelectionOnCrosstabGroup() {
    WorkspaceSettings.getInstance().setExperimentalFeaturesVisible( true );

    final MasterReport report = new MasterReport();
    report.setRootGroup( new CrosstabGroup() );

    final TestReportDesignerContext rdc = new TestReportDesignerContext();
    rdc.addMasterReport( report );
    rdc.setActiveDocument( rdc.getDocumentContext( 0 ) );
    final ReportDocumentContext activeContext = rdc.getActiveContext();
    Assert.assertNotNull( activeContext );
    Assert.assertEquals( activeContext.getReportDefinition(), report );

    EditCrosstabAction action = new EditCrosstabAction();
    action.setReportDesignerContext( rdc );
    Assert.assertFalse( action.isEnabled() );

    rdc.getActiveContext().getSelectionModel().add( report.getRootGroup() );
    Assert.assertTrue( action.isEnabled() );
  }

  @Test
  public void testCreateUndoActionOnMasterReport() {
    WorkspaceSettings.getInstance().setExperimentalFeaturesVisible( true );

    final MasterReport report = new MasterReport();
    CrosstabGroup rootGroup = new CrosstabGroup();
    report.setRootGroup( rootGroup );

    final TestReportDesignerContext rdc = new TestReportDesignerContext();
    rdc.addMasterReport( report );
    rdc.setActiveDocument( rdc.getDocumentContext( 0 ) );
    final ReportDocumentContext activeContext = rdc.getActiveContext();
    Assert.assertNotNull( activeContext );
    Assert.assertEquals( activeContext.getReportDefinition(), report );

    activeContext.getSelectionModel().add( rootGroup );

    CrosstabGroup crosstabGroup = new CrosstabGroup();

    EditCrosstabAction action = new NonEditingCrosstabAction( crosstabGroup );
    action.setReportDesignerContext( rdc );
    action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "Edit" ) );
    Assert.assertFalse( rdc.getActiveContext().getUndo().isRedoPossible() );
    Assert.assertTrue( rdc.getActiveContext().getUndo().isUndoPossible() );
    Assert.assertEquals( crosstabGroup.getObjectID(), report.getRootGroup().getObjectID() );

    rdc.getActiveContext().getUndo().undo( rdc.getActiveContext() );
    Assert.assertEquals( rootGroup.getObjectID(), report.getRootGroup().getObjectID() );

    rdc.getActiveContext().getUndo().redo( rdc.getActiveContext() );
    Assert.assertEquals( crosstabGroup.getObjectID(), report.getRootGroup().getObjectID() );
  }

  @Test
  public void testCreateUndoActionOnCrosstabReport() {
    WorkspaceSettings.getInstance().setExperimentalFeaturesVisible( true );

    final MasterReport report = new MasterReport();
    final CrosstabElement element = new CrosstabElement();
    Group rootGroup = element.getRootGroup();
    report.getReportHeader().addElement( element );

    final TestReportDesignerContext rdc = new TestReportDesignerContext();
    rdc.addMasterReport( report );
    rdc.addSubReport( (ReportDocumentContext) rdc.getDocumentContext( 0 ), element );
    rdc.setActiveDocument( rdc.getDocumentContext( 1 ) );
    final ReportDocumentContext activeContext = rdc.getActiveContext();
    Assert.assertNotNull( activeContext );
    Assert.assertEquals( activeContext.getReportDefinition(), element );

    CrosstabGroup crosstabGroup = new CrosstabGroup();

    EditCrosstabAction action = new NonEditingCrosstabAction( crosstabGroup );
    action.setReportDesignerContext( rdc );
    action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "Edit" ) );
    Assert.assertFalse( rdc.getActiveContext().getUndo().isRedoPossible() );
    Assert.assertTrue( rdc.getActiveContext().getUndo().isUndoPossible() );
    Assert.assertEquals( crosstabGroup.getObjectID(), element.getRootGroup().getObjectID() );

    rdc.getActiveContext().getUndo().undo( rdc.getActiveContext() );
    Assert.assertEquals( rootGroup.getObjectID(), element.getRootGroup().getObjectID() );

    rdc.getActiveContext().getUndo().redo( rdc.getActiveContext() );
    Assert.assertEquals( crosstabGroup.getObjectID(), element.getRootGroup().getObjectID() );
  }


  @Test
  public void testCreateUndoActionOnDeepStructure() {
    WorkspaceSettings.getInstance().setExperimentalFeaturesVisible( true );

    final MasterReport report = new MasterReport();
    final CrosstabElement element = new CrosstabElement();

    CrosstabGroup rootGroup = new CrosstabGroup();

    RelationalGroup relGroup = new RelationalGroup();
    relGroup.setBody( new SubGroupBody( rootGroup ) );
    element.setRootGroup( relGroup );

    report.getReportHeader().addElement( element );

    final TestReportDesignerContext rdc = new TestReportDesignerContext();
    rdc.addMasterReport( report );
    rdc.addSubReport( (ReportDocumentContext) rdc.getDocumentContext( 0 ), element );
    rdc.setActiveDocument( rdc.getDocumentContext( 1 ) );
    final ReportDocumentContext activeContext = rdc.getActiveContext();
    Assert.assertNotNull( activeContext );
    Assert.assertEquals( activeContext.getReportDefinition(), element );

    CrosstabGroup crosstabGroup = new CrosstabGroup();

    EditCrosstabAction action = new NonEditingCrosstabAction( crosstabGroup );
    action.setReportDesignerContext( rdc );
    action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "Edit" ) );
    Assert.assertFalse( rdc.getActiveContext().getUndo().isRedoPossible() );
    Assert.assertTrue( rdc.getActiveContext().getUndo().isUndoPossible() );
    Assert.assertEquals( crosstabGroup.getObjectID(), element.getRootGroup().getBody().getGroup().getObjectID() );

    rdc.getActiveContext().getUndo().undo( rdc.getActiveContext() );
    Assert.assertEquals( rootGroup.getObjectID(), element.getRootGroup().getBody().getGroup().getObjectID() );

    rdc.getActiveContext().getUndo().redo( rdc.getActiveContext() );
    Assert.assertEquals( crosstabGroup.getObjectID(), element.getRootGroup().getBody().getGroup().getObjectID() );
  }

  protected static final class NonEditingCrosstabAction extends EditCrosstabAction {
    private CrosstabGroup crosstab;

    public NonEditingCrosstabAction( final CrosstabGroup crosstab ) {
      this.crosstab = crosstab;
    }

    protected CrosstabGroup performEdit( final CrosstabGroup selectedCrosstab ) {
      return crosstab;
    }
  }
}
