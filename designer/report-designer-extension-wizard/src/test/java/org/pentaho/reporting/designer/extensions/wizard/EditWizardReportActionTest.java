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
 * Copyright (c) 2002 - 2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.extensions.wizard;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.awt.Component;
import java.awt.Window;

import javax.swing.Action;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.ReportDesignerDesignTimeContext;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.EmbeddedWizard;

public class EditWizardReportActionTest {

  ReportDesignerContext reportDesignerContext;
  ReportDocumentContext reportDocumentContext;
  ReportDesignerView reportDesignerView;
  Component component;
  Window window;
  EmbeddedWizard dialog;
  AbstractReportDefinition abstractReportDefinition;
  MasterReport masterReport;
  SubReport subReport, subReport1, subReport2;
  Element element1, element2;
  Section section;

  @Before
  public void setUp() throws Exception {
    reportDesignerContext = mock( ReportDesignerContext.class );
    reportDocumentContext = mock( ReportDocumentContext.class );
    reportDesignerView = mock( ReportDesignerView.class );
    component = mock( Component.class );
    window = mock( Window.class );
    dialog = mock( EmbeddedWizard.class );
    abstractReportDefinition = mock( AbstractReportDefinition.class );
    masterReport = mock( MasterReport.class );
    subReport = mock( SubReport.class );
    subReport1 = mock( SubReport.class );
    subReport2 = mock( SubReport.class );
    element1 = mock( Element.class );
    element2 = mock( Element.class );
    section = mock( AbstractRootLevelBand.class );
  }

  @Test
  public void testConstructor() {
    EditWizardReportAction editWizardReportAction = new EditWizardReportAction();
    String nameValue = (String) editWizardReportAction.getValue( Action.NAME );
    assertEquals( nameValue, Messages.getString( "EditWizardReportAction.MenuText" ) );
  }

  @Test
  public void testActionPerformedWithInvalidConfig() {
    EditWizardReportAction reportActionSpy = spy( new EditWizardReportAction() );
    reportActionSpy.actionPerformed( null );

    verify( reportActionSpy, times( 1 ) ).getReportDesignerContext();

    doReturn( reportDesignerContext ).when( reportActionSpy ).getReportDesignerContext();
    reportActionSpy.actionPerformed( null );
  }

  @Test
  public void testActionPerformedWithMasterReport() {
    EditWizardReportAction reportActionSpy = spy( new EditWizardReportAction() );
    doReturn( reportDesignerContext ).when( reportActionSpy ).getReportDesignerContext();
    doReturn( window ).when( reportActionSpy ).getWindowAncestor( component );
    doReturn( dialog ).when( reportActionSpy ).createDialog( (Window) any(),
        (ReportDesignerDesignTimeContext) any() );
    doReturn( reportDocumentContext ).when( reportDesignerContext ).getActiveContext();
    doReturn( abstractReportDefinition ).when( reportDocumentContext ).getReportDefinition();
    doReturn( abstractReportDefinition ).when( abstractReportDefinition ).derive();
    doReturn( reportDesignerView ).when( reportDesignerContext ).getView();
    doReturn( component ).when( reportDesignerView ).getParent();
    try {
      doReturn( masterReport ).when( dialog ).run( abstractReportDefinition );
    } catch ( ReportProcessingException e ) {
      e.printStackTrace();
    }
    reportActionSpy.actionPerformed( null );

    verify( reportActionSpy, times( 1 ) ).getReportDesignerContext();
    verify( reportActionSpy, times( 1 ) ).createDialog( (Window) any(),
        (ReportDesignerDesignTimeContext) any() );
    verify( reportActionSpy, times( 1 ) ).getWindowAncestor( component );
    verify( reportDesignerContext, times( 2 ) ).getActiveContext();
    verify( reportDesignerContext, times( 1 ) ).getView();
    try {
      verify( reportDesignerContext, times( 1 ) ).addMasterReport( (MasterReport) any() );
    } catch ( ReportDataFactoryException e1 ) {
      e1.printStackTrace();
    }
    verify( reportDocumentContext, times( 1 ) ).getReportDefinition();
    verify( abstractReportDefinition, times( 1 ) ).derive();
    try {
      verify( dialog ).run( (AbstractReportDefinition) any() );
    } catch ( ReportProcessingException e ) {
      e.printStackTrace();
    }
    verify( reportDesignerView, times( 1 ) ).getParent();
  }

  @Test
  public void testActionPerformedWithRootBandSubreport() {
    EditWizardReportAction reportActionSpy = spy( new EditWizardReportAction() );
    doReturn( reportDesignerContext ).when( reportActionSpy ).getReportDesignerContext();
    doReturn( window ).when( reportActionSpy ).getWindowAncestor( component );
    doReturn( dialog ).when( reportActionSpy ).createDialog( (Window) any(),
        (ReportDesignerDesignTimeContext) any() );
    doReturn( reportDocumentContext ).when( reportDesignerContext ).getActiveContext();
    doReturn( abstractReportDefinition ).when( reportDocumentContext ).getReportDefinition();
    doReturn( abstractReportDefinition ).when( abstractReportDefinition ).derive();
    doReturn( reportDesignerView ).when( reportDesignerContext ).getView();
    doReturn( component ).when( reportDesignerView ).getParent();
    try {
      doReturn( subReport ).when( dialog ).run( abstractReportDefinition );
    } catch ( ReportProcessingException e ) {
      e.printStackTrace();
    }
    doReturn( new SubReport[] { subReport1, subReport2 } ).when( (AbstractRootLevelBand) section ).getSubReports();
    doReturn( new Element[] { element1, element2 } ).when( (AbstractRootLevelBand) section ).getElementArray();
    doReturn( section ).when( reportActionSpy ).getParentSection( abstractReportDefinition );

    reportActionSpy.actionPerformed( null );

    verify( reportActionSpy, times( 1 ) ).getReportDesignerContext();
    verify( reportActionSpy, times( 1 ) ).createDialog( (Window) any(),
        (ReportDesignerDesignTimeContext) any() );
    verify( reportActionSpy, times( 1 ) ).getWindowAncestor( component );
    verify( reportDesignerContext, times( 2 ) ).getActiveContext();
    verify( reportDesignerContext, times( 1 ) ).getView();
    verify( reportDocumentContext, times( 1 ) ).getReportDefinition();
    verify( abstractReportDefinition, times( 1 ) ).derive();
    try {
      verify( dialog ).run( (AbstractReportDefinition) any() );
    } catch ( ReportProcessingException e ) {
      e.printStackTrace();
    }
    verify( reportDesignerView, times( 1 ) ).getParent();
    verify( (AbstractRootLevelBand) section, times( 1 ) ).getSubReports();
    verify( (AbstractRootLevelBand) section, times( 1 ) ).getElementArray();
  }

}
