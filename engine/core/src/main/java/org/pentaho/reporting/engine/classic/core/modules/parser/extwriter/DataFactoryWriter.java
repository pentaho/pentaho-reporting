/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

/**
 * Creation-Date: Jan 18, 2007, 6:39:15 PM
 *
 * @author Thomas Morgner
 */
public class DataFactoryWriter extends AbstractXMLDefinitionWriter {
  private static final String PREFIX =
      "org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.handler.datafactories.";

  public DataFactoryWriter( final ReportWriterContext reportWriter, final XmlWriter xmlWriter ) {
    super( reportWriter, xmlWriter );
  }

  /**
   * Writes the report definition portion. Every DefinitionWriter handles one or more elements of the JFreeReport object
   * tree, DefinitionWriter traverse the object tree and write the known objects or forward objects to other definition
   * writers.
   *
   * @throws java.io.IOException
   *           if there is an I/O problem.
   * @throws ReportWriterException
   *           if the report serialisation failed.
   */
  public void write() throws IOException, ReportWriterException {
    final AbstractReportDefinition reportDef = getReport();
    if ( reportDef instanceof MasterReport == false ) {
      // subreports have no data-factory at all.
      return;
    }

    // first, try to find a suitable writer implementation.
    final MasterReport report = (MasterReport) getReport();
    final DataFactory dataFactory = report.getDataFactory();

    final DataFactoryWriteHandler handler = DataFactoryWriter.lookupWriteHandler( dataFactory );
    if ( handler != null ) {
      handler.write( getReportWriter(), getXmlWriter(), dataFactory );
      return;
    }

    // then fall back to the default ..
    DataFactoryWriter.writeDefaultDataFactory( dataFactory, getXmlWriter() );
  }

  public static DataFactoryWriteHandler lookupWriteHandler( final DataFactory dataFactory ) {
    final String configKey = PREFIX + dataFactory.getClass().getName();
    final Configuration globalConfig = ClassicEngineBoot.getInstance().getGlobalConfig();
    final String value = globalConfig.getConfigProperty( configKey );
    if ( value != null ) {
      return (DataFactoryWriteHandler) ObjectUtilities.loadAndInstantiate( value, DataFactoryWriter.class,
          DataFactoryWriteHandler.class );
    }
    return null;
  }

  public static void writeDefaultDataFactory( final DataFactory dataFactory, final XmlWriter writer )
    throws IOException {
    String dataFactoryClass = null;
    if ( dataFactory != null ) {
      if ( hasPublicDefaultConstructor( dataFactory.getClass() ) ) {
        dataFactoryClass = dataFactory.getClass().getName();
      }
    }

    if ( dataFactoryClass == null ) {
      return;
    }

    final AttributeList attr = new AttributeList();
    attr.setAttribute( ExtParserModule.NAMESPACE, "type", dataFactoryClass );
    writer.writeTag( ExtParserModule.NAMESPACE, "data-factory", attr, XmlWriterSupport.CLOSE );
  }

}
