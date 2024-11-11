/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.writer;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.Olap4JDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.SimpleBandedMDXDataFactory;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

public class SimpleBandedMDXDataFactoryWriteHandler extends AbstractMDXDataFactoryBundleWriteHandler {
  public SimpleBandedMDXDataFactoryWriteHandler() {
  }

  public void write( final ReportWriterContext reportWriter,
                     final XmlWriter xmlWriter,
                     final DataFactory dataFactory )
    throws IOException, ReportWriterException {
    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", Olap4JDataFactoryModule.NAMESPACE );

    xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "simple-banded-mdx-datasource", rootAttrs, XmlWriter.OPEN );

    final SimpleBandedMDXDataFactory pmdDataFactory = (SimpleBandedMDXDataFactory) dataFactory;
    try {
      writeBody( pmdDataFactory, xmlWriter );
    } catch ( BundleWriterException e ) {
      throw new ReportWriterException( "Failed", e );
    }
    xmlWriter.writeCloseTag();
  }


}
