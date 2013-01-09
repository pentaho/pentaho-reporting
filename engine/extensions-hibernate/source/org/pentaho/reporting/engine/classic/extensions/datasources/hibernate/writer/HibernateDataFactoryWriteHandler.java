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

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.DataFactoryWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.HQLDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.HibernateDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.SessionProvider;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: Jan 19, 2007, 4:44:05 PM
 *
 * @author Thomas Morgner
 */
public class HibernateDataFactoryWriteHandler implements DataFactoryWriteHandler
{
  public static final String PREFIX =
      "org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.writer.session-provider.";

  public HibernateDataFactoryWriteHandler()
  {
  }

  /**
   * Writes a data-source into a XML-stream.
   *
   * @param reportWriter the writer context that holds all factories.
   * @param xmlWriter    the XML writer that will receive the generated XML data.
   * @param dataFactory  the data factory that should be written.
   * @throws IOException           if any error occured
   * @throws ReportWriterException if the data factory cannot be written.
   */
  public void write(final ReportWriterContext reportWriter,
                    final XmlWriter xmlWriter,
                    final DataFactory dataFactory)
      throws IOException, ReportWriterException
  {
    final HQLDataFactory hqlDataFactory = (HQLDataFactory) dataFactory;

    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration("data", HibernateDataFactoryModule.NAMESPACE);
    xmlWriter.writeTag(HibernateDataFactoryModule.NAMESPACE, "hibernate-datasource", rootAttrs, XmlWriter.OPEN);

    writeConnectionInfo(xmlWriter, hqlDataFactory.getSessionProvider());

    final String[] queryNames = hqlDataFactory.getQueryNames();
    for (int i = 0; i < queryNames.length; i++)
    {
      final String queryName = queryNames[i];
      final String query = hqlDataFactory.getQuery(queryName);
      xmlWriter.writeTag(HibernateDataFactoryModule.NAMESPACE, "query", "name", queryName, XmlWriter.OPEN);
      xmlWriter.writeTextNormalized(query, false);
      xmlWriter.writeCloseTag();
    }
    xmlWriter.writeCloseTag();
  }

  private void writeConnectionInfo(final XmlWriter xmlWriter,
                                   final SessionProvider connectionProvider)
      throws IOException, ReportWriterException
  {
    final String configKey = HibernateDataFactoryWriteHandler.PREFIX + connectionProvider.getClass().getName();
    final Configuration globalConfig = ClassicEngineBoot.getInstance().getGlobalConfig();
    final String value = globalConfig.getConfigProperty(configKey);
    if (value != null)
    {
      final SessionProviderWriteHandler handler = ObjectUtilities.loadAndInstantiate
              (value, HQLDataFactory.class, SessionProviderWriteHandler.class);
      if (handler != null)
      {
        handler.write(xmlWriter, connectionProvider);
      }
    }
  }
}