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


package org.pentaho.reporting.libraries.pensol;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;

public class VfsTest extends TestCase {
  public VfsTest() {
  }

  public VfsTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    LibPensolBoot.getInstance().start();
  }

  public void testParse() throws IOException {
    final InputStream stream = TestSolutionFileModel.class.getResourceAsStream
      ( "/org/pentaho/reporting/libraries/pensol/SolutionRepositoryService.xml" );
    try {
      TestSolutionFileModel model = new TestSolutionFileModel();
      model.performParse( stream );
    } finally {
      if ( stream != null ) {
        stream.close();
      }
    }

  }
}
