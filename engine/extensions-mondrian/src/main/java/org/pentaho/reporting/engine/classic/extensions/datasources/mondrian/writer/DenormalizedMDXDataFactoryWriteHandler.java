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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.writer;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DenormalizedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.MondrianDataFactoryModule;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

/**
 * Todo: Document me!
 *
 * @author : Thomas Morgner
 */
public class DenormalizedMDXDataFactoryWriteHandler extends AbstractNamedMDXDataFactoryWriteHandler {
  public DenormalizedMDXDataFactoryWriteHandler() {
  }

  public void write( final ReportWriterContext reportWriter,
                     final XmlWriter xmlWriter,
                     final DataFactory dataFactory )
    throws IOException, ReportWriterException {
    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", MondrianDataFactoryModule.NAMESPACE );

    xmlWriter.writeTag( MondrianDataFactoryModule.NAMESPACE, "denormalized-mdx-datasource", rootAttrs, XmlWriter.OPEN );

    final DenormalizedMDXDataFactory pmdDataFactory = (DenormalizedMDXDataFactory) dataFactory;
    writeBody( reportWriter, pmdDataFactory, xmlWriter );
    xmlWriter.writeCloseTag();
  }
}
