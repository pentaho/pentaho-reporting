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

import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * A default factory for all commonly used java base classes from java.lang, java.awt etc.
 *
 * @author Thomas Morgner
 */
public class ExtraShapesClassFactory extends ClassFactoryImpl {

  /**
   * DefaultConstructor. Creates the object factory for all java base classes.
   */
  public ExtraShapesClassFactory() {
    registerClass( RoundRectangle2D.class, new RoundRectangle2DObjectDescription() );
    registerClass( Ellipse2D.class, new Ellipse2DObjectDescription() );
  }

}
