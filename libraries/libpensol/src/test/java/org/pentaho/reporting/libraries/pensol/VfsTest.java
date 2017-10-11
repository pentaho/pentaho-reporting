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
