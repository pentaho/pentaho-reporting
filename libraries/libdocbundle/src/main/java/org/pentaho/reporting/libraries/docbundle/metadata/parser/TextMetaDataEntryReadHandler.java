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


package org.pentaho.reporting.libraries.docbundle.metadata.parser;

import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;

/**
 * A simple read handler for text meta-data.
 *
 * @author Thomas Morgner
 */
public class TextMetaDataEntryReadHandler extends StringReadHandler implements BundleMetaDataEntryReadHandler {
  public TextMetaDataEntryReadHandler() {
  }

  public String getMetaDataNameSpace() {
    return getUri();
  }

  public String getMetaDataName() {
    return getTagName();
  }
}
