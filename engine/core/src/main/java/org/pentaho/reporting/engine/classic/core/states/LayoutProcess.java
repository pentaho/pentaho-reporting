/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;

public interface LayoutProcess extends Cloneable {
  public static final int LEVEL_STRUCTURAL_PREPROCESSING = Integer.MAX_VALUE;
  public static final int LEVEL_PAGINATE = -2;
  public static final int LEVEL_COLLECT = -1;

  public LayoutProcess getParent();

  public boolean isPageListener();

  public OutputFunction getOutputFunction();

  public StructureFunction[] getCollectionFunctions();

  public LayoutProcess deriveForStorage();

  public LayoutProcess deriveForPagebreak();

  public Object clone();

  /**
   * This function must be implemented in a re-entrant way. Report events can cause nested report events to be fired.
   *
   * @param originalEvent
   */
  public void fireReportEvent( ReportEvent originalEvent );

  public void restart( final ReportState state ) throws ReportProcessingException;
}
