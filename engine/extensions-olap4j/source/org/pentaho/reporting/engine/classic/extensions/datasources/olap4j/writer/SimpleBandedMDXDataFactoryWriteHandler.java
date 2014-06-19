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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.writer;

import java.io.IOException;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.Olap4JDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.SimpleBandedMDXDataFactory;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

public class SimpleBandedMDXDataFactoryWriteHandler extends AbstractMDXDataFactoryBundleWriteHandler
{
  public SimpleBandedMDXDataFactoryWriteHandler()
  {
  }

  public void write(final ReportWriterContext reportWriter,
                    final XmlWriter xmlWriter,
                    final DataFactory dataFactory)
      throws IOException, ReportWriterException
  {
    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration("data", Olap4JDataFactoryModule.NAMESPACE);

    xmlWriter.writeTag(Olap4JDataFactoryModule.NAMESPACE, "simple-banded-mdx-datasource", rootAttrs, XmlWriter.OPEN);

    final SimpleBandedMDXDataFactory pmdDataFactory = (SimpleBandedMDXDataFactory) dataFactory;
    try
    {
      writeBody(pmdDataFactory, xmlWriter);
    }
    catch (BundleWriterException e)
    {
      throw new ReportWriterException("Failed", e);
    }
    xmlWriter.writeCloseTag();
  }


}
