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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects;

import org.pentaho.reporting.engine.classic.core.layout.BandLayoutManager;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactoryImpl;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassLoaderObjectDescription;

/**
 * A band layout class factory.
 *
 * @author Thomas Morgner
 */
public class BandLayoutClassFactory extends ClassFactoryImpl
{
  /**
   * Creates a new band layout class factory.
   */
  public BandLayoutClassFactory()
  {
    registerClass(BandLayoutManager.class, new ClassLoaderObjectDescription());
  }
}
