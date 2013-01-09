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

package org.pentaho.reporting.engine.classic.extensions.datasources.hibernate;

import org.hibernate.Session;
import org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.SessionProvider;

/**
 * Creation-Date: Jan 12, 2007, 5:46:06 PM
 *
 * @author Thomas Morgner
 */
public class StaticSessionProvider implements SessionProvider
{
  private Session session;

  public StaticSessionProvider(final Session session)
  {
    this.session = session;
  }

  public Session getSession()
  {
    return session;
  }
}