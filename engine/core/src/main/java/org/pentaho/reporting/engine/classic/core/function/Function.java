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


package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.event.ReportListener;

/**
 * The interface for report functions. A report function separates the business logic from presentation of the result.
 * The function is called whenever JFreeReport changes its state while generating the report. The working model for the
 * functions is based on cloning the state of the function on certain checkpoints to support the ReportState
 * implementation of JFreeReport.
 * <p/>
 * Although functions support the ReportListener interface, they are not directly added to a report. A report
 * FunctionCollection is used to control the functions. Functions are required to be cloneable.
 * <p/>
 *
 * @author Thomas Morgner
 */
public interface Function extends ReportListener, Expression {
}
