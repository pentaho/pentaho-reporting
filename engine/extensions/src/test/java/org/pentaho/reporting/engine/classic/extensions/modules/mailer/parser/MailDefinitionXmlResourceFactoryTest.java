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
