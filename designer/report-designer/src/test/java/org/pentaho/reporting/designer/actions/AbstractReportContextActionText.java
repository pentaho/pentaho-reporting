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
