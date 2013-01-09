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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.parser;

import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.DefaultSessionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.SessionProvider;
import org.xml.sax.SAXException;

/**
 * Creation-Date: Jan 22, 2007, 2:34:37 PM
 *
 * @author Thomas Morgner
 */
public class DefaultSessionProviderReadHandler
    extends AbstractXmlReadHandler implements SessionProviderReadHandler
{
  public DefaultSessionProviderReadHandler()
  {
  }

  /**
   * Returns the object for this element or null, if this element does not
   * create an object.
   *
   * @return the object.
   */
  public Object getObject() throws SAXException
  {
    return getProvider();
  }

  public SessionProvider getProvider()
  {
    return new DefaultSessionProvider();
  }
}