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


package org.pentaho.reporting.libraries.docbundle.metadata.writer;

import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

public interface BundleMetaDataEntryWriteHandler {
  public void write( BundleMetaDataXmlWriter bundleWriter,
                     XmlWriter writer,
                     String entryNamespace,
                     String entryName,
                     Object entryValue ) throws IOException;
}
