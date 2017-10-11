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