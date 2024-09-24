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
 * Copyright (c) 2005-2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.mailer.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data.DataDefinitionXmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;

public class MailDefinitionXmlResourceFactoryTest {

  private MailDefinitionXmlResourceFactory xmlResourceFactory;

  @Before
  public void setUp() {
    xmlResourceFactory = spy( new MailDefinitionXmlResourceFactory() );
    doNothing().when( xmlResourceFactory ).registerModule( any( XmlFactoryModule.class ) );
  }

  @Test
  @SuppressWarnings( { "unchecked" } )
  public void testGetFactoryType() {
    Class<MailDefinitionXmlResourceFactory> result = xmlResourceFactory.getFactoryType();
    assertThat( result, is( equalTo( MailDefinitionXmlResourceFactory.class ) ) );
  }

  @Test
  public void testInitializeDefaults() throws Exception {
    xmlResourceFactory.initializeDefaults();
    verify( xmlResourceFactory, never() ).registerModule( any( XmlFactoryModule.class ) );

    MailDefinitionXmlResourceFactory.register( DataDefinitionXmlFactoryModule.class );
    xmlResourceFactory.initializeDefaults();
    verify( xmlResourceFactory ).registerModule( any( XmlFactoryModule.class ) );
  }
}
