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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
