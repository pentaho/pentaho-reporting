/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
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
