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


package org.pentaho.reporting.engine.classic.core.metadata;

import org.pentaho.reporting.engine.classic.core.ReportProcessTask;

public interface ReportProcessTaskMetaData extends MetaData {
  public String[] getAlias();

  public ReportProcessTask create();
}
