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

package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

import junit.framework.TestCase;
import org.junit.Assert;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;

import java.awt.Color;

public class DefalutStyleBuilderTest extends TestCase {

  public void testPrintEdgeAsCSS() {
    DefaultStyleBuilder dsb = new DefaultStyleBuilder( new DefaultStyleBuilderFactory() );
    BorderEdge be = new BorderEdge( BorderStyle.DOT_DASH, Color.BLACK, 100 );
    String css = dsb.printEdgeAsCSS( be );

    Assert.assertTrue( css.contains( "t dashed black" ) );
    be = new BorderEdge( BorderStyle.DOT_DOT_DASH, Color.BLACK, 100 );
    css = dsb.printEdgeAsCSS( be );
    System.out.println( css );
    Assert.assertTrue( css.contains( "pt dotted black" ) );
  }
}
