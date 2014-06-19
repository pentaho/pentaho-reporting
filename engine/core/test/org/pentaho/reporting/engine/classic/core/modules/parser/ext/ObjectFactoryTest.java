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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.parser.ext;

import java.awt.geom.Line2D;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.DefaultClassFactory;

public class ObjectFactoryTest extends TestCase
{
  public ObjectFactoryTest(final String s)
  {
    super(s);
  }

  public void testObjectQuery()
  {
    final DefaultClassFactory fact = new DefaultClassFactory();
    final ObjectDescription line2DDescr = fact.getDescriptionForClass(Line2D.class);
    assertNotNull(line2DDescr);
    final ObjectDescription od = fact.getSuperClassObjectDescription(Line2D.Float.class, null);
    assertEquals(line2DDescr.getClass(), od.getClass());
  }
}
