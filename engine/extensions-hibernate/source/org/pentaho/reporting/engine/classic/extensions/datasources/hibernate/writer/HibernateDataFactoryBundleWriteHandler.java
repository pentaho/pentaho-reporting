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
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleDataFactoryWriterHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.HibernateDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.SessionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.HQLDataFactory;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;

/**
 * Creation-Date: Jan 19, 2007, 4:44:05 PM
 *
 * @author Thomas Morgner
 */
public class HibernateDataFactoryBundleWriteHandler implements BundleDataFactoryWriterHandler
{
  public static final String PREFIX =
      "org.pentaho.reporting.engine.classic.extensions.datasources.hibernate.writer.session-provider.";

  public HibernateDataFactoryBundleWriteHandler()
  {
  }

  /**
   * Writes a data-source into a own file. The name of file inside the bundle is returned
   * as string. The file name returned is always absolute and can be made relative by using the IOUtils of
   * LibBase. If the writer-handler did not generate a file on its own, it should return null.
   *
   * @param bundle      the bundle where to write to.
   * @param dataFactory the data factory that should be written.
   * @param state       the writer state to hold the current processing information.
   * @return the name of the newly generated file or null if no file was created.
   * @throws IOException           if any error occured
   * @throws BundleWriterException if a bundle-management error occured.
   */
  public String writeDataFactory(final WriteableDocumentBundle bundle,
                                 final DataFactory dataFactory,
                                 final BundleWriterState state)
      throws IOException, BundleWriterException
  {
    final HQLDataFactory hqlDataFactory = (HQLDataFactory) dataFactory;

    final String fileName = BundleUtilities.getUniqueName(bundle, state.getFileName(), "datasources/hibernate-ds{0}.xml");
    if (fileName == null)
    {
      throw new IOException("Unable to generate unique name for Inline-Data-Source");
    }

    final OutputStream outputStream = bundle.createEntry(fileName, "text/xml");
    final DefaultTagDescription tagDescription = new DefaultTagDescription(ClassicEngineBoot.getInstance().getGlobalConfig(), HibernateDataFactoryModule.TAG_DEF_PREFIX);
    final XmlWriter xmlWriter = new XmlWriter(new OutputStreamWriter(outputStream, "UTF-8"), tagDescription, "  ", "\n");
    final AttributeList rootAttrs = new AttributeList();
    if (xmlWriter.isNamespaceDefined(HibernateDataFactoryModule.NAMESPACE) == false)
    {
      rootAttrs.addNamespaceDeclaration("data", HibernateDataFactoryModule.NAMESPACE);
    }
    xmlWriter.writeTag(HibernateDataFactoryModule.NAMESPACE, "hibernate-datasource", rootAttrs, XmlWriter.OPEN);

    try
    {
      writeConnectionInfo(xmlWriter, hqlDataFactory.getSessionProvider());
    }
    catch (ReportWriterException e)
    {
      throw new BundleWriterException("Failed to write connection info", e);
    }

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
    xmlWriter.close();
    return fileName;
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
      final SessionProviderWriteHandler handler =
          (SessionProviderWriteHandler) ObjectUtilities.loadAndInstantiate
              (value, HQLDataFactory.class, SessionProviderWriteHandler.class);
      if (handler != null)
      {
        handler.write(xmlWriter, connectionProvider);
      }
    }
  }
}