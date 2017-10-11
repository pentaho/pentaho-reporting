/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
