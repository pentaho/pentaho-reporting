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


package org.pentaho.reporting.libraries.css.parser.stylehandler.page;

import org.pentaho.reporting.libraries.css.keys.page.PagePolicy;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 30.11.2005, 18:38:12
 *
 * @author Thomas Morgner
 */
public class PagePolicyReadHandler extends OneOfConstantsReadHandler {
  public PagePolicyReadHandler() {
    super( false );
    addValue( PagePolicy.FIRST );
    addValue( PagePolicy.START );
    addValue( PagePolicy.LAST );
  }
}
