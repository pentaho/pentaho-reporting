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


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

import java.net.URL;

/**
 * A URL class factory.
 *
 * @author Thomas Morgner
 */
public class URLClassFactory extends ClassFactoryImpl {

  /**
   * Creates a new URL class factory.
   */
  public URLClassFactory() {
    registerClass( URL.class, new URLObjectDescription() );
  }
}
