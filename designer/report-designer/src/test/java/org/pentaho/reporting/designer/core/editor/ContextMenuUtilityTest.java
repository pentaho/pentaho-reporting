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


package org.pentaho.reporting.designer.core.editor;

import javax.swing.JMenuItem;
import javax.swing.table.DefaultTableModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportQueryNode;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;

public class ContextMenuUtilityTest {

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testToggleActivationItem() throws Exception {

    final MasterReport masterReport = new MasterReport();
    final CompoundDataFactory compoundDataFactory = new CompoundDataFactory();
    final TableDataFactory tableDataFactory = new TableDataFactory();
    tableDataFactory.addTable( "default", new DefaultTableModel() );
    compoundDataFactory.add( tableDataFactory );
    masterReport.setDataFactory( compoundDataFactory );

    final ReportRenderContext doc = new ReportRenderContext( masterReport );
    final JMenuItem item = new JMenuItem();
    final ReportQueryNode reportQueryNode = new ReportQueryNode( compoundDataFactory, "default", true );

    ContextMenuUtility.toggleActivationItem( doc, reportQueryNode, item );

    Assert.assertTrue( item.isEnabled() );

    final TableDataFactory tableDataFactory2 = new TableDataFactory();
    tableDataFactory2.addTable( "default", new DefaultTableModel() );
    compoundDataFactory.add( tableDataFactory2 );

    ContextMenuUtility.toggleActivationItem( doc, reportQueryNode, item );

    Assert.assertFalse( item.isEnabled() );
  }
}