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
 * Copyright (c) 2002-2019 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.bugs;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.naming.spi.NamingManager;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.pentaho.reporting.engine.classic.bugs.Prd5143Test.createPageableHTML;

public class Pir1421Test {

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    if ( NamingManager.hasInitialContextFactoryBuilder() == false ) {
      NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );
    }
  }

  @Test
  public void testPir1421() throws Exception {
    final URL resource = getClass().getResource( "Pir-1421.prpt" );
    assertNotNull( resource );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource parsed = mgr.createDirectly( resource, MasterReport.class );
    final MasterReport report = (MasterReport) parsed.getResource();

    String pageableHTML = createPageableHTML( report, 0 );
    assertTrue( pageableHTML.contains( "Orde\u200Br Numb\u200Ber" ) );
  }
}
