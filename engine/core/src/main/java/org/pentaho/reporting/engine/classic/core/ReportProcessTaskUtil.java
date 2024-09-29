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


package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.util.NoCloseOutputStream;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;

import java.io.OutputStream;

public class ReportProcessTaskUtil {
  private ReportProcessTaskUtil() {
  }

  public static void configureBodyStream( final ReportProcessTask task, final OutputStream outputStream,
      final String fileName, final String suffix ) {
    if ( task == null ) {
      throw new NullPointerException();
    }
    if ( outputStream == null ) {
      throw new NullPointerException();
    }
    if ( fileName == null ) {
      throw new NullPointerException();
    }

    final StreamRepository streamRepository = new StreamRepository( new NoCloseOutputStream( outputStream ) );
    final ContentLocation targetRoot = streamRepository.getRoot();

    task.setBodyContentLocation( targetRoot );
    task.setBodyNameGenerator( new DefaultNameGenerator( targetRoot, fileName, suffix ) );
  }
}
