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
 * Copyright (c) 2018 Hitachi Vantara..  All rights reserved.
 */
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
