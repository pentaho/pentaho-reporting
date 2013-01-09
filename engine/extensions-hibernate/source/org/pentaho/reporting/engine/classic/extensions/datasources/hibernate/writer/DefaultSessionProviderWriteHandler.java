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

package org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.writer;

import java.io.IOException;

import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.SessionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.HibernateDataFactoryModule;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

/**
 * Creation-Date: Jan 19, 2007, 5:03:22 PM
 *
 * @author Thomas Morgner
 */
public class DefaultSessionProviderWriteHandler
    implements SessionProviderWriteHandler
{
  public DefaultSessionProviderWriteHandler()
  {
  }

  public void write(final XmlWriter xmlWriter,
                    final SessionProvider connectionProvider)
      throws IOException, ReportWriterException
  {
    xmlWriter.writeTag
        (HibernateDataFactoryModule.NAMESPACE, "default-session-provider", XmlWriter.CLOSE);
  }
}