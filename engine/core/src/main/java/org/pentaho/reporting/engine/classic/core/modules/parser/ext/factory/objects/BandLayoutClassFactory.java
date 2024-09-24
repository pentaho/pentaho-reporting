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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects;

import org.pentaho.reporting.engine.classic.core.layout.BandLayoutManager;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactoryImpl;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassLoaderObjectDescription;

/**
 * A band layout class factory.
 *
 * @author Thomas Morgner
 */
public class BandLayoutClassFactory extends ClassFactoryImpl {
  /**
   * Creates a new band layout class factory.
   */
  public BandLayoutClassFactory() {
    registerClass( BandLayoutManager.class, new ClassLoaderObjectDescription() );
  }
}
