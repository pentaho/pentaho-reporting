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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * The content file is included exactly once in each bundle. This file orchestates the parsing process.
 *
 * @author Thomas Morgner
 */
public class ContentFileWriter implements BundleWriterHandler {
  public ContentFileWriter() {
  }

  /**
   * Returns a relatively high processing order indicating this BundleWriterHandler should be one of the last processed
   *
   * @return the relative processing order for this BundleWriterHandler
   */
  public int getProcessingOrder() {
    return 100000;
  }

  public String writeReport( final WriteableDocumentBundle bundle, final BundleWriterState state ) throws IOException,
    BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }

    final BundleWriterState contentFileState = new BundleWriterState( state, "content.xml" );

    final OutputStream outputStream =
        new BufferedOutputStream( bundle.createEntry( contentFileState.getFileName(), "text/xml" ) );
    final DefaultTagDescription tagDescription = BundleWriterHandlerRegistry.getInstance().createWriterTagDescription();
    final XmlWriter writer =
        new XmlWriter( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ", "\n" );
    writer.writeXmlDeclaration( "UTF-8" );

    final AttributeList rootAttributes = new AttributeList();
    rootAttributes.addNamespaceDeclaration( "", BundleNamespaces.CONTENT );

    writer.writeTag( BundleNamespaces.CONTENT, "content", rootAttributes, XmlWriterSupport.OPEN );

    writer.writeComment( " The content.xml file remains intentionally empty. This file can be used to " );
    writer.writeComment( " inject global templates later. " );

    writer.writeCloseTag();
    writer.close();
    return contentFileState.getFileName();

  }
}
