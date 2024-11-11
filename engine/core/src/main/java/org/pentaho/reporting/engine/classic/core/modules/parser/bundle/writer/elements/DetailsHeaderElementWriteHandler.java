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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

@Deprecated
public class DetailsHeaderElementWriteHandler extends AbstractElementWriteHandler {
  public DetailsHeaderElementWriteHandler() {
  }

  /**
   * Writes a single element as XML structure.
   *
   * @param bundle
   *          the bundle to which to write to.
   * @param state
   *          the current write-state.
   * @param xmlWriter
   *          the xml writer.
   * @param element
   *          the element.
   * @throws IOException
   *           if an IO error occured.
   * @throws BundleWriterException
   *           if an Bundle writer.
   */
  public void writeElement( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final XmlWriter xmlWriter, final Element element ) throws IOException, BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( xmlWriter == null ) {
      throw new NullPointerException();
    }
    if ( element == null ) {
      throw new NullPointerException();
    }

    final AttributeList attList = createMainAttributes( element, xmlWriter );
    xmlWriter.writeTag( BundleNamespaces.LAYOUT, "details-header", attList, XmlWriterSupport.OPEN );
    writeElementBody( bundle, state, element, xmlWriter );
    writeChildElements( bundle, state, xmlWriter, (Band) element );
    xmlWriter.writeCloseTag();

  }
}
