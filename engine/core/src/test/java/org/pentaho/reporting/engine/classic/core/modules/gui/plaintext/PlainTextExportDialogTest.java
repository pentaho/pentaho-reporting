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
