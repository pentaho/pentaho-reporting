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

package org.pentaho.reporting.engine.classic.core.modules.parser.data.external.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleDataFactoryWriterHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.data.external.ExternalDataFactoryModule;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

public class ExternalDataFactoryWriteHandler implements BundleDataFactoryWriterHandler
{
  public ExternalDataFactoryWriteHandler()
  {
  }

  /**
   * Writes a data-source into a own file. The name of file inside the bundle is returned as string. The file name
   * returned is always absolute and can be made relative by using the IOUtils of LibBase. If the writer-handler did not
   * generate a file on its own, it should return null.
   *
   * @param bundle      the bundle where to write to.
   * @param dataFactory the data factory that should be written.
   * @param state       the writer state to hold the current processing information.
   * @return the name of the newly generated file or null if no file was created.
   * @throws java.io.IOException if any error occured
   * @throws org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException
   *                             if a bundle-management error occured.
   */
  public String writeDataFactory(final WriteableDocumentBundle bundle,
                                 final DataFactory dataFactory,
                                 final BundleWriterState state) throws IOException, BundleWriterException
  {
    if (bundle == null)
    {
      throw new NullPointerException();
    }
    if (dataFactory == null)
    {
      throw new NullPointerException();
    }
    if (state == null)
    {
      throw new NullPointerException();
    }

    final String fileName = BundleUtilities.getUniqueName(bundle, state.getFileName(), "datasources/external-ds{0}.xml");
    if (fileName == null)
    {
      throw new IOException("Unable to generate unique name for External-Data-Source");
    }

    final OutputStream outputStream = bundle.createEntry(fileName, "text/xml");
    final DefaultTagDescription tagDescription = new DefaultTagDescription();
    final XmlWriter xmlWriter = new XmlWriter(new OutputStreamWriter(outputStream, "UTF-8"), tagDescription, "  ",
        "\n");
    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration("data", ExternalDataFactoryModule.NAMESPACE);
    xmlWriter.writeTag(ExternalDataFactoryModule.NAMESPACE, "external-datasource", rootAttrs, XmlWriterSupport.CLOSE);
    xmlWriter.close();

    return fileName;
  }
}
