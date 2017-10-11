/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
