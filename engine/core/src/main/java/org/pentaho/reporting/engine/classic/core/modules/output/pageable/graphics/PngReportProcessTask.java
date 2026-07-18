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



package org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics;

import org.pentaho.reporting.libraries.base.config.Configuration;

public class PngReportProcessTask extends Graphics2DReportProcessTask {
  public PngReportProcessTask() {
  }

  protected String computeMimeType( final Configuration configuration ) {
    return "image/png";
  }
}
