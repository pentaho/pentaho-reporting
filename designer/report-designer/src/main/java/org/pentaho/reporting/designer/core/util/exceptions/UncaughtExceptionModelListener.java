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


package org.pentaho.reporting.designer.core.util.exceptions;

import java.util.EventListener;

/**
 * User: Martin Date: 24.02.2006 Time: 09:37:37
 */
public interface UncaughtExceptionModelListener extends EventListener {
  void exceptionCaught( Throwable throwable );


  void exceptionsCleared();


  void exceptionsViewed();
}
