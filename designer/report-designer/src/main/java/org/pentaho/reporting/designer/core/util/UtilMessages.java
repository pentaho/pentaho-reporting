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

package org.pentaho.reporting.designer.core.util;

import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.Locale;

public class UtilMessages extends Messages {
  private static UtilMessages utilMessages;

  public static Messages getInstance() {
    if ( utilMessages == null ) {
      utilMessages = new UtilMessages();
    }
    return utilMessages;
  }

  /**
   * Creates a new Messages-collection. The locale and baseName will be used to create the resource-bundle that backs up
   * this implementation.
   */
  private UtilMessages() {
    super( Locale.getDefault(), "org.pentaho.reporting.designer.core.util.messages",  // NON-NLS
      ObjectUtilities.getClassLoader( UtilMessages.class ) );
  }
}
