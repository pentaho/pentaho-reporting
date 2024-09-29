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


package org.pentaho.reporting.engine.classic.core.layout.output;

/**
 * A generic dummy class that reports the export-descriptor "none/none".
 *
 * @author Thomas Morgner
 */
public final class GenericOutputProcessorMetaData extends AbstractOutputProcessorMetaData {
  private String exportDescriptor;

  public GenericOutputProcessorMetaData() {
    this( "none/none" );
  }

  public GenericOutputProcessorMetaData( final String exportDescriptor ) {
    if ( exportDescriptor == null ) {
      throw new NullPointerException();
    }
    this.exportDescriptor = exportDescriptor;
  }

  /**
   * The export descriptor is a string that describes the output characteristics. For libLayout outputs, it should start
   * with the output class (one of 'pageable', 'flow' or 'stream'), followed by '/liblayout/' and finally followed by
   * the output type (ie. PDF, Print, etc).
   *
   * @return the export descriptor.
   */
  public String getExportDescriptor() {
    return exportDescriptor;
  }
}
