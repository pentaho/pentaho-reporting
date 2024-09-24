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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import junit.framework.TestCase;
import org.apache.commons.vfs2.FileSystemException;

import java.io.File;
import java.net.URL;

/**
 * Todo: Document me!
 * <p/>
 * Date: 13.01.11 Time: 15:04
 *
 * @author Thomas Morgner.
 */
public class Prd3139Test extends TestCase {
  public Prd3139Test() {
  }

  public Prd3139Test( final String name ) {
    super( name );
  }

  public void testSchemaResolve() throws FileSystemException {
    final URL url = Prd3139Test.class.getResource
      ( "/org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/steelwheels.mondrian.xml" );
    final String fileTxt = url.getFile();
    final File file = new File( fileTxt ).getAbsoluteFile();
    assertTrue( file.canRead() );
    assertNotNull(
      SchemaResolver.resolveSchema( null, null, "../../../../../../../../../../../../../../" + file.getPath() ) );
  }
}
