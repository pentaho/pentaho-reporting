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


package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.writer;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.DataFactoryWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.LegacyBandedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.Olap4JDataFactoryModule;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

public class LegacyBandedMDXDataFactoryWriteHandler extends AbstractNamedMDXDataFactoryBundleWriteHandler
  implements DataFactoryWriteHandler {
  public LegacyBandedMDXDataFactoryWriteHandler() {
  }

  public void write( final ReportWriterContext reportWriter,
                     final XmlWriter xmlWriter,
                     final DataFactory dataFactory )
    throws IOException, ReportWriterException {
    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", Olap4JDataFactoryModule.NAMESPACE );

    xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "legacy-banded-mdx-datasource", rootAttrs, XmlWriter.OPEN );

    final LegacyBandedMDXDataFactory pmdDataFactory = (LegacyBandedMDXDataFactory) dataFactory;
    try {
      writeBody( pmdDataFactory, xmlWriter );
    } catch ( BundleWriterException e ) {
      throw new ReportWriterException( "Failed", e );
    }
    xmlWriter.writeCloseTag();
  }
}
