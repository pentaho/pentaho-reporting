/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
