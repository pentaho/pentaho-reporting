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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public interface BundleElementWriteHandler {
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
      final XmlWriter xmlWriter, final Element element ) throws IOException, BundleWriterException;
}
