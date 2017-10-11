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

package org.pentaho.reporting.engine.classic.core.dom;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.DetailsFooterType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.DetailsHeaderType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.GroupDataBodyType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.NoDataBandType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.PageFooterType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.PageHeaderType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.RelationalGroupType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ReportFooterType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ReportHeaderType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.WatermarkType;

public class NodeMatchingIT extends TestCase {
  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testMatchStandardElements() throws Exception {
    final MasterReport report = new MasterReport();
    assertNotNull( report.getChildElementByType( ItemBandType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( NoDataBandType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( DetailsHeaderType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( DetailsFooterType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( GroupDataBodyType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( RelationalGroupType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( ReportHeaderType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( ReportFooterType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( PageHeaderType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( PageFooterType.INSTANCE ) );
    assertNotNull( report.getChildElementByType( WatermarkType.INSTANCE ) );
  }
}
