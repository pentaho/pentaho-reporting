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
 * Copyright (c) 2002-2024 Hitachi Vantara..  All rights reserved.
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
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.EmbeddedWizard;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentBundle;

public class NewWizardReportActionTest {

  ReportDesignerContext reportDesignerContext;
  ReportDesignerView reportDesignerView;
  Window window;
  Component component;
  EmbeddedWizard wizard;
  MasterReport masterReport;
  MemoryDocumentBundle bundle;

  @Before
  public void setUp() throws Exception {
    reportDesignerContext = mock( ReportDesignerContext.class );
    reportDesignerView = mock( ReportDesignerView.class );
    window = mock( Window.class );
    component = mock( Component.class );
    wizard = mock( EmbeddedWizard.class );
    masterReport = mock( MasterReport.class );
    bundle = new MemoryDocumentBundle();
  }

  @Test
  public void testConstructor() {
    NewWizardReportAction newWizardReportAction = new NewWizardReportAction();
    assertEquals( newWizardReportAction.getValue( Action.NAME ), Messages.getString( "NewWizardReportAction.MenuTitle" ) );
    assertEquals( newWizardReportAction.getValue( "WIZARD.BUTTON.TEXT" ), Messages
        .getString( "NewWizardReportAction.ButtonTitle" ) );
    assertEquals( newWizardReportAction.getValue( Action.ACCELERATOR_KEY ), Messages
        .getOptionalKeyStroke( "NewWizardReportAction.Accelerator" ) );
  }

  @Test
  public void testActionPerformed() {
    NewWizardReportAction reportActionSpy = spy( new NewWizardReportAction() );
    doReturn( reportDesignerContext ).when( reportActionSpy ).getReportDesignerContext();
    doReturn( reportDesignerView ).when( reportDesignerContext ).getView();
    doReturn( component ).when( reportDesignerView ).getParent();
    doReturn( window ).when( reportActionSpy ).getWindowAncestor( component );
    doReturn( wizard ).when( reportActionSpy ).getEmbeddedWizard( window );
    try {
      doReturn( masterReport ).when( reportActionSpy ).runDialog( wizard );
    } catch ( ReportProcessingException e ) {
      e.printStackTrace();
    }
    doReturn( bundle ).when( masterReport ).getBundle();

    reportActionSpy.actionPerformed( null );

    verify( reportActionSpy, times( 1 ) ).getReportDesignerContext();
    verify( reportDesignerContext, times( 1 ) ).getView();
    verify( reportDesignerView, times( 1 ) ).getParent();
    verify( reportActionSpy, times( 1 ) ).getWindowAncestor( (Component) any() );
    verify( reportActionSpy, times( 1 ) ).getEmbeddedWizard( (Window) any() );
  }

}
