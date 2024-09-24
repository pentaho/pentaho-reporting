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
 * Copyright (c) 2005-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.sparklines.xml;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.BarSparklineType;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.LineSparklineType;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.PieSparklineType;

public class SparklineElementReadHandlerIT {

  @BeforeClass
  public static void init() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testBarSparklineEementReadHandler() throws Exception {
    BarSparklineElementReadHandler handler = new BarSparklineElementReadHandler();
    assertThat( handler, is( notNullValue() ) );
    assertThat( handler.getElement(), is( notNullValue() ) );
    assertThat( handler.getElement().getElementType(), is( instanceOf( BarSparklineType.class ) ) );
  }

  @Test
  public void testLineSparklineEementReadHandler() throws Exception {
    LineSparklineElementReadHandler handler = new LineSparklineElementReadHandler();
    assertThat( handler, is( notNullValue() ) );
    assertThat( handler.getElement(), is( notNullValue() ) );
    assertThat( handler.getElement().getElementType(), is( instanceOf( LineSparklineType.class ) ) );
  }

  @Test
  public void testPieSparklineEementReadHandler() throws Exception {
    PieSparklineElementReadHandler handler = new PieSparklineElementReadHandler();
    assertThat( handler, is( notNullValue() ) );
    assertThat( handler.getElement(), is( notNullValue() ) );
    assertThat( handler.getElement().getElementType(), is( instanceOf( PieSparklineType.class ) ) );
  }
}
