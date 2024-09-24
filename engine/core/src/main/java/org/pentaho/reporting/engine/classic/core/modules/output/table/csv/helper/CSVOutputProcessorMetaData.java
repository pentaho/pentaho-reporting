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

package org.pentaho.reporting.engine.classic.core.modules.output.table.csv.helper;

import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

/**
 * Creation-Date: 09.05.2007, 14:39:13
 *
 * @author Thomas Morgner
 */
public class CSVOutputProcessorMetaData extends AbstractOutputProcessorMetaData {
  public static final int PAGINATION_NONE = 0;
  public static final int PAGINATION_MANUAL = 1;
  public static final int PAGINATION_FULL = 2;
  private int paginationMode;

  public CSVOutputProcessorMetaData( final int paginationMode ) {
    this.paginationMode = paginationMode;
  }

  public void initialize( final Configuration configuration ) {
    super.initialize( configuration );

    final String localStrict =
        configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.csv.StrictLayout" );
    if ( localStrict != null ) {
      if ( "true".equals( localStrict ) ) {
        addFeature( AbstractTableOutputProcessor.STRICT_LAYOUT );
      }
    } else {
      final String globalStrict =
          configuration
              .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.base.StrictLayout" );
      if ( "true".equals( globalStrict ) ) {
        addFeature( AbstractTableOutputProcessor.STRICT_LAYOUT );
      }
    }

    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.base.UsePageBands" ) ) ) {
      addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.csv.UsePageBands" ) ) ) {
      addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    }

    if ( paginationMode == CSVOutputProcessorMetaData.PAGINATION_FULL ) {
      addFeature( OutputProcessorFeature.PAGEBREAKS );
    } else if ( paginationMode == CSVOutputProcessorMetaData.PAGINATION_MANUAL ) {
      addFeature( OutputProcessorFeature.PAGEBREAKS );
      addFeature( OutputProcessorFeature.ITERATIVE_RENDERING );
      addFeature( OutputProcessorFeature.UNALIGNED_PAGEBANDS );
    } else {
      addFeature( OutputProcessorFeature.ITERATIVE_RENDERING );
      addFeature( OutputProcessorFeature.UNALIGNED_PAGEBANDS );
    }

    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.csv.AssumeOverflowX" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_X );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.csv.AssumeOverflowY" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_Y );
    }
    addFeature( OutputProcessorFeature.IGNORE_ROTATION );
  }

  public boolean isContentSupported( final Object content ) {
    if ( content instanceof ImageContainer ) {
      return false;
    }
    if ( content instanceof DrawableWrapper ) {
      return false;
    }
    // we accept shapes, although they are not printable as they are regularily used for layouting ..
    return super.isContentSupported( content );
  }

  /**
   * The export descriptor is a string that describes the output characteristics. For libLayout outputs, it should start
   * with the output class (one of 'pageable', 'flow' or 'stream'), followed by '/liblayout/' and finally followed by
   * the output type (ie. PDF, Print, etc).
   *
   * @return the export descriptor.
   */
  public String getExportDescriptor() {
    return "table/csv";
  }
}
