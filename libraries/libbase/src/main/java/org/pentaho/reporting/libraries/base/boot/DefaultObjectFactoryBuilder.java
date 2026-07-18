/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.base.boot;

public class DefaultObjectFactoryBuilder implements ObjectFactoryBuilder {
  public DefaultObjectFactoryBuilder() {
  }

  public ObjectFactory createObjectFactory( final SubSystem subSystem ) {
    return new DefaultObjectFactory( subSystem.getGlobalConfig() );
  }
}
