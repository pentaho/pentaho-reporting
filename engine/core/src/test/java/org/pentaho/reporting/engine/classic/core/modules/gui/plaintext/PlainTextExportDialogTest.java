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

package org.pentaho.reporting.engine.classic.core.modules.gui.plaintext;

import junit.framework.TestCase;

public class PlainTextExportDialogTest extends TestCase {
  public PlainTextExportDialogTest() {
  }

  public PlainTextExportDialogTest( final String s ) {
    super( s );
  }

  public void testSelectEncoding() {
    if ( "true".equals( System.getProperty( "java.awt.headless", "false" ) ) ) {
      return;
    }
    final PlainTextExportDialog d = new PlainTextExportDialog();
    d.setModal( true );
    d.setEncoding( "Cp850" );
    assertEquals( "Cp850", d.getEncoding() );

    d.setSelectedPrinter( PlainTextExportDialog.TYPE_EPSON9_OUTPUT );
    assertEquals( "Cp850", d.getEncoding() );

    d.setSelectedPrinter( PlainTextExportDialog.TYPE_EPSON24_OUTPUT );
    assertEquals( "Cp850", d.getEncoding() );

    d.setSelectedPrinter( PlainTextExportDialog.TYPE_IBM_OUTPUT );
    assertEquals( "Cp850", d.getEncoding() );

    d.setSelectedPrinter( PlainTextExportDialog.TYPE_PLAIN_OUTPUT );
    assertEquals( "Cp850", d.getEncoding() );
  }
}
