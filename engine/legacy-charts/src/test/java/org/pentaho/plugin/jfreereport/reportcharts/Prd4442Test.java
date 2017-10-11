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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.plugin.jfreereport.reportcharts;

import junit.framework.TestCase;
import org.jfree.data.general.DefaultPieDataset;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;

public class Prd4442Test extends TestCase {
  public Prd4442Test() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testPieURL() throws Exception {
    DebugExpressionRuntime runtime = new DebugExpressionRuntime();
    FormulaPieURLGenerator gen = new FormulaPieURLGenerator( runtime, "=[chart::item]" );

    DefaultPieDataset dataSet = new DefaultPieDataset();
    dataSet.setValue( "Key-1", 5 );
    dataSet.setValue( "Key-2", 7 );
    dataSet.setValue( "Key-3", 10 );
    assertEquals( "5.0", gen.generateURL( dataSet, "Key-1", 0 ) );
    assertEquals( "7.0", gen.generateURL( dataSet, "Key-2", 1 ) );
    assertEquals( "10.0", gen.generateURL( dataSet, "Key-3", 2 ) );
  }

  public void testPieTooltip() throws Exception {
    DebugExpressionRuntime runtime = new DebugExpressionRuntime();
    FormulaPieTooltipGenerator gen = new FormulaPieTooltipGenerator( runtime, "=[chart::item]" );

    DefaultPieDataset dataSet = new DefaultPieDataset();
    dataSet.setValue( "Key-1", 5 );
    dataSet.setValue( "Key-2", 7 );
    dataSet.setValue( "Key-3", 10 );
    assertEquals( "5.0", gen.generateToolTip( dataSet, "Key-1" ) );
    assertEquals( "7.0", gen.generateToolTip( dataSet, "Key-2" ) );
    assertEquals( "10.0", gen.generateToolTip( dataSet, "Key-3" ) );
  }
}
