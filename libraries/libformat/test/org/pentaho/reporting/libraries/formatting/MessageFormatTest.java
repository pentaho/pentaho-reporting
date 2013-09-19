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
 * Copyright (c) 2008 - 2009 Pentaho Corperation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.formatting;

import java.util.Date;

import junit.framework.TestCase;

public class MessageFormatTest extends TestCase
{
  public MessageFormatTest()
  {
  }

  public MessageFormatTest(final String name)
  {
    super(name);
  }

  public void testNonCrash()
  {
    FastMessageFormat messageFormat = new FastMessageFormat("{0,date,dd MMM yyyy}");
    System.out.println (messageFormat.format(new Object[]{new Date()}));

  }

}
