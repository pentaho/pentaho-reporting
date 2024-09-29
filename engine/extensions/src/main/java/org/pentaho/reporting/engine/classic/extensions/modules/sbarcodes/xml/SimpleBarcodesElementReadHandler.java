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


package org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.xml;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.AbstractElementReadHandler;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.SimpleBarcodesType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

/**
 * This class is responsible to read SimpleBarcodes element properties (attributes and styles) from XML Unified File
 * Format.
 *
 * @author Cedric Pronzato
 */
public class SimpleBarcodesElementReadHandler extends AbstractElementReadHandler {
  public SimpleBarcodesElementReadHandler() throws ParseException {
    super( new SimpleBarcodesType() );
  }
}
