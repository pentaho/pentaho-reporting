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


package org.pentaho.reporting.engine.classic.core.modules.parser.data.sequence.writer;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.Sequence;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleDataFactoryWriterHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.data.sequence.SequenceDataFactoryModule;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class SequenceDataFactoryWriteHandler implements BundleDataFactoryWriterHandler {
  public SequenceDataFactoryWriteHandler() {
  }

  /**
   * Writes a data-source into a own file. The name of file inside the bundle is returned as string. The file name
   * returned is always absolute and can be made relative by using the IOUtils of LibBase. If the writer-handler did not
   * generate a file on its own, it should return null.
   *
   * @param bundle
   *          the bundle where to write to.
   * @param state
   *          the writer state to hold the current processing information.
   * @return the name of the newly generated file or null if no file was created.
   * @throws IOException
   *           if any error occured
   * @throws BundleWriterException
   *           if a bundle-management error occured.
   */
  public String writeDataFactory( final WriteableDocumentBundle bundle, final DataFactory dataFactory,
      final BundleWriterState state ) throws IOException, BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }

    final SequenceDataFactory sequenceDataFactory = (SequenceDataFactory) dataFactory;

    final String fileName =
        BundleUtilities.getUniqueName( bundle, state.getFileName(), "datasources/sequence-ds{0}.xml" );
    if ( fileName == null ) {
      throw new IOException( "Unable to generate unique name for Sequence-Data-Source" );
    }

    try {
      final OutputStream outputStream = bundle.createEntry( fileName, "text/xml" );
      final DefaultTagDescription tagDescription = new DefaultTagDescription();
      tagDescription.setNamespaceHasCData( SequenceDataFactoryModule.NAMESPACE, false );
      tagDescription.setElementHasCData( SequenceDataFactoryModule.NAMESPACE, "property", true );

      final XmlWriter xmlWriter =
          new XmlWriter( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ", "\n" );
      final AttributeList rootAttrs = new AttributeList();
      rootAttrs.addNamespaceDeclaration( "data", SequenceDataFactoryModule.NAMESPACE );
      xmlWriter.writeTag( SequenceDataFactoryModule.NAMESPACE, "sequence-datasource", rootAttrs, XmlWriterSupport.OPEN );

      final String[] tables = sequenceDataFactory.getQueryNames();
      for ( int i = 0; i < tables.length; i++ ) {
        final String queryName = tables[i];
        final Sequence sequence = sequenceDataFactory.getSequence( queryName );

        final AttributeList sequenceAttributes = new AttributeList();
        sequenceAttributes.setAttribute( SequenceDataFactoryModule.NAMESPACE, "name", queryName );
        sequenceAttributes.setAttribute( SequenceDataFactoryModule.NAMESPACE, "class", sequence.getClass().getName() );
        xmlWriter.writeTag( SequenceDataFactoryModule.NAMESPACE, "sequence", sequenceAttributes, XmlWriterSupport.OPEN );

        final SequenceDescription sequenceDescription = sequence.getSequenceDescription();
        final int parameterCount = sequenceDescription.getParameterCount();
        for ( int p = 0; p < parameterCount; p++ ) {
          final String paramName = sequenceDescription.getParameterName( p );
          final Object parameter = sequence.getParameter( paramName );
          if ( parameter == null ) {
            continue;
          }
          final String attrValue = ConverterRegistry.toAttributeValue( parameter );
          xmlWriter
              .writeTag( SequenceDataFactoryModule.NAMESPACE, "property", "name", paramName, XmlWriterSupport.OPEN );
          xmlWriter.writeTextNormalized( attrValue, true );
          xmlWriter.writeCloseTag();
        }

        xmlWriter.writeCloseTag();
      }
      xmlWriter.writeCloseTag();
      xmlWriter.close();
      return fileName;
    } catch ( BeanException e ) {
      throw new BundleWriterException( "Unable to serialize sequence parameter", e );
    }
  }
}
