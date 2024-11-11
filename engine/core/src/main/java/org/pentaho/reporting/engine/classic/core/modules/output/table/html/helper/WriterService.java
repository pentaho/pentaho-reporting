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


package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.libraries.base.util.MemoryStringWriter;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class WriterService {
  public static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";
  private MemoryStringWriter bufferWriter;
  private BufferedWriter writer;
  private XmlWriter xmlWriter;

  private WriterService( final BufferedWriter bufferedWriter ) {
    this.writer = bufferedWriter;
  }

  public WriterService( final BufferedWriter bufferedWriter, final MemoryStringWriter bufferWriter ) {
    this.writer = bufferedWriter;
    this.bufferWriter = bufferWriter;
  }

  public XmlWriter getXmlWriter() {
    return xmlWriter;
  }

  public MemoryStringWriter getBufferWriter() {
    return bufferWriter;
  }

  public XmlWriter createHeaderXmlWriter() {
    if ( isBuffered() == false ) {
      throw new IllegalStateException();
    }

    final XmlWriter docWriter = new XmlWriter( writer, xmlWriter.getTagDescription() );
    docWriter.addImpliedNamespace( HtmlPrinter.XHTML_NAMESPACE, "" );
    docWriter.setHtmlCompatiblityMode( true );
    return docWriter;
  }

  private void setXmlWriter( final XmlWriter xmlWriter ) {
    this.xmlWriter = xmlWriter;
  }

  private static DefaultTagDescription createTagDefinitions() {
    final DefaultTagDescription td = new DefaultTagDescription();
    td.setDefaultNamespace( XHTML_NAMESPACE );
    td.setNamespaceHasCData( XHTML_NAMESPACE, true );
    td.setNamespaceHasCData( XHTML_NAMESPACE, true );
    td.setElementHasCData( XHTML_NAMESPACE, "body", false );
    td.setElementHasCData( XHTML_NAMESPACE, "br", true );
    td.setElementHasCData( XHTML_NAMESPACE, "col", false );
    td.setElementHasCData( XHTML_NAMESPACE, "colgroup", false );
    td.setElementHasCData( XHTML_NAMESPACE, "div", true );
    td.setElementHasCData( XHTML_NAMESPACE, "head", false );
    td.setElementHasCData( XHTML_NAMESPACE, "html", false );
    td.setElementHasCData( XHTML_NAMESPACE, "img", true );
    td.setElementHasCData( XHTML_NAMESPACE, "input", true );
    td.setElementHasCData( XHTML_NAMESPACE, "meta", true );
    td.setElementHasCData( XHTML_NAMESPACE, "p", true );
    td.setElementHasCData( XHTML_NAMESPACE, "pre", true );
    td.setElementHasCData( XHTML_NAMESPACE, "span", true );
    td.setElementHasCData( XHTML_NAMESPACE, "style", false );
    td.setElementHasCData( XHTML_NAMESPACE, "table", false );
    td.setElementHasCData( XHTML_NAMESPACE, "tbody", false );
    td.setElementHasCData( XHTML_NAMESPACE, "td", true );
    td.setElementHasCData( XHTML_NAMESPACE, "tfoot", false );
    td.setElementHasCData( XHTML_NAMESPACE, "th", false );
    td.setElementHasCData( XHTML_NAMESPACE, "thead", false );
    td.setElementHasCData( XHTML_NAMESPACE, "title", true );
    td.setElementHasCData( XHTML_NAMESPACE, "tr", false );
    return td;
  }

  public boolean isBuffered() {
    return bufferWriter != null;
  }

  public static WriterService createPassThroughService( OutputStream out, String encoding )
    throws UnsupportedEncodingException {
    BufferedWriter bufferedWriter = new BufferedWriter( new OutputStreamWriter( out, encoding ) );

    XmlWriter xmlWriter = new XmlWriter( bufferedWriter, createTagDefinitions() );
    xmlWriter.addImpliedNamespace( HtmlPrinter.XHTML_NAMESPACE, "" );
    xmlWriter.setHtmlCompatiblityMode( true );

    WriterService writerService = new WriterService( bufferedWriter );
    writerService.setXmlWriter( xmlWriter );
    return writerService;
  }

  public static WriterService createBufferedService( OutputStream out, String encoding )
    throws UnsupportedEncodingException {
    MemoryStringWriter bufferWriter = new MemoryStringWriter( 1024 * 512 );
    XmlWriter xmlWriter = new XmlWriter( bufferWriter, createTagDefinitions() );
    xmlWriter.setAdditionalIndent( 1 );
    xmlWriter.addImpliedNamespace( HtmlPrinter.XHTML_NAMESPACE, "" );
    xmlWriter.setHtmlCompatiblityMode( true );

    BufferedWriter bufferedWriter = new BufferedWriter( new OutputStreamWriter( out, encoding ) );
    WriterService writerService = new WriterService( bufferedWriter, bufferWriter );
    writerService.setXmlWriter( xmlWriter );
    return writerService;
  }

  public void close() throws IOException {
    this.writer.close();
  }
}
