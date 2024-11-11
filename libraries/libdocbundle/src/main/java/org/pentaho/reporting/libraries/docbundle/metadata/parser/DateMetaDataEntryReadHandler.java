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

import org.pentaho.reporting.libraries.docbundle.BundleUtilities;

public class DateMetaDataEntryReadHandler extends TextMetaDataEntryReadHandler {
  public DateMetaDataEntryReadHandler() {
  }

  /**
   * Returns the object for this element.
   *
   * @return the object.
   */
  public Object getObject() {
    return BundleUtilities.parseDate( getResult() );
  }
}
