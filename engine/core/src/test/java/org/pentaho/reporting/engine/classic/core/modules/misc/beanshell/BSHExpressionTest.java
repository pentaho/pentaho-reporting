/*
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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.beanshell;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.states.DataRowConnector;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;

import javax.swing.table.DefaultTableModel;

public class BSHExpressionTest extends TestCase {
  public BSHExpressionTest( final String s ) {
    super( s );
  }

  public void testCreate() throws Exception {
    assertTrue( DataRow.class.isAssignableFrom( DataRowConnector.class ) );
    final BSHExpression ex = new BSHExpression();
    ex.setExpression( "" );

    final DefaultProcessingContext processingContext = new DefaultProcessingContext();

    ex.setRuntime( new DebugExpressionRuntime( new DefaultTableModel(), 0, processingContext ) );
    assertNull( ex.getValue() );
    // must not crash
  }
}
