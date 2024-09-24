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

package org.pentaho.reporting.designer.actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.testsupport.TestReportDesignerContext;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;

import java.awt.event.ActionEvent;

public class AbstractReportContextActionText {
  private static class TestAction extends AbstractElementSelectionAction {
    private TestAction() {
    }

    protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {

    }

    public DocumentContextSelectionModel getSelectionModel() {
      return super.getSelectionModel();
    }

    public void actionPerformed( final ActionEvent e ) {
      //
    }
  }

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    ReportDesignerBoot.getInstance().start();
  }

  @Test
  public void testActionInit() throws Exception {
    TestReportDesignerContext designerContext = new TestReportDesignerContext();
    int i = designerContext.addMasterReport( new MasterReport() );
    designerContext.setActiveDocument( designerContext.getDocumentContext( i ) );

    final TestAction crosstabAction = new TestAction();
    crosstabAction.setReportDesignerContext( designerContext );
    Assert.assertNotNull( crosstabAction.getSelectionModel() );
  }
}
