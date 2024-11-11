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
