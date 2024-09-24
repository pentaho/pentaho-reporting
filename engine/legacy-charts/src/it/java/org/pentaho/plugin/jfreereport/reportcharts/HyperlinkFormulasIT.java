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

package org.pentaho.plugin.jfreereport.reportcharts;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.extensions.legacy.charts.LegacyChartType;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

import static org.junit.Assert.*;

public class HyperlinkFormulasIT {

  @Before
  public void before() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void getFormulas() throws ResourceException {
    final URL url = getClass().getResource( "Prd-3883.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    assertNotNull( report );
    final ReportElement chart = findChart( report );
    assertNotNull( chart );
    final Expression attributeExpression =
      chart.getAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
    assertNotNull( attributeExpression );
    assertTrue( attributeExpression instanceof ChartExpression );
    ChartExpression chartExpression = (ChartExpression) attributeExpression;
    final String[] hyperlinkFormulas = chartExpression.getHyperlinkFormulas();
    assertNotNull( hyperlinkFormulas );
    assertEquals( 1, hyperlinkFormulas.length );
    assertTrue( hyperlinkFormulas[ 0 ].startsWith( "=DRILLDOWN" ) );
  }

  private ReportElement findChart( final Section section ) {
    final int count = section.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final ReportElement element = section.getElement( i );

      if ( element.getElementType() instanceof LegacyChartType ) {
        return element;
      }
      if ( element instanceof Section ) {
        final ReportElement chart = findChart( (Section) element );
        if ( chart != null ) {
          return chart;
        }
      }
    }
    return null;
  }


}
