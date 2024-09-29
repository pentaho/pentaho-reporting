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


package org.pentaho.reporting.libraries.pensol;

import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;

public class PentahoSolutionsFileSystemConfigBuilder extends DefaultFileSystemConfigBuilder {
  private static final String TIMEOUT_KEY = "timeout";

  public PentahoSolutionsFileSystemConfigBuilder() {
  }

  public void setTimeOut( final FileSystemOptions opts, final int timeOut ) {
    setParam( opts, TIMEOUT_KEY, timeOut );
  }

  public int getTimeOut( final FileSystemOptions opts ) {
    final Integer param = (Integer) getParam( opts, TIMEOUT_KEY );
    if ( param != null ) {
      return param;
    }
    return 0;
  }
}
