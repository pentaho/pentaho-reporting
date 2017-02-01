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
* Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
* Copyright (c) 2011 - 2012 De Bortoli Wines Pty Limited (Australia). All Rights Reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.openerp.writer;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.DataFactoryWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.openerp.OpenERPDataFactory;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

/**
 * @author Pieter van der Merwe
 */
public class OpenERPDataFactoryWriteHandler implements DataFactoryWriteHandler {
  public OpenERPDataFactoryWriteHandler() {
  }

  /**
   * Writes a data-source into a XML-stream.
   *
   * @param reportWriter   the writer context that holds all factories.
   * @param xmlWriter      the XML writer that will receive the generated XML data.
   * @param rawDataFactory the data factory that should be written.
   * @throws IOException           if any error occured
   * @throws ReportWriterException if the data factory cannot be written.
   */
  public void write( final ReportWriterContext reportWriter,
                     final XmlWriter xmlWriter,
                     final DataFactory rawDataFactory )
    throws IOException, ReportWriterException {
    final OpenERPDataFactory dataFactory = (OpenERPDataFactory) rawDataFactory;

    OpenERPDataFactoryHelper.writeXML( dataFactory, xmlWriter );

  }
}
