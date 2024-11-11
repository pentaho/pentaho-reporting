/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.testsupport;

import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import java.util.Hashtable;

public class DebugJndiContextFactoryBuilder implements InitialContextFactoryBuilder {
  public DebugJndiContextFactoryBuilder() {
  }

  public InitialContextFactory createInitialContextFactory( final Hashtable<?, ?> environment ) throws NamingException {
    return new DebugJndiContextFactory();
  }
}
