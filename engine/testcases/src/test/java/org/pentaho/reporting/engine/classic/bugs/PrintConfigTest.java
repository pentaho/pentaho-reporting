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

package org.pentaho.reporting.engine.classic.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class PrintConfigTest extends TestCase {
  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testEnvironment() throws Exception {
    final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
    final Iterator<String> propertyKeys = config.findPropertyKeys( "" );
    final ArrayList<String> keys = new ArrayList<String>();
    while ( propertyKeys.hasNext() ) {
      keys.add( propertyKeys.next() );
    }
    Collections.sort( keys );
    for ( final String key : keys ) {
      System.out.println( key + "=" + config.getConfigProperty( key ) );
    }
  }
}
