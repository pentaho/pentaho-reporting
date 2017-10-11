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

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.process.SubReportProcessType;

/**
 * A simple tagging interface for the transition from function-based layouting back to explicit layouting. Output
 * functions are always considered internal information, so there is no way to write a report-processor that is not
 * aware of the actual implementation of the output-function.
 *
 * @author Thomas Morgner
 */
public interface OutputFunction extends Function {
  /**
   * Creates a storage-copy of the output function. A storage copy must create a deep clone of all referenced objects so
   * that it is guaranteed that changes to either the original or the clone do not affect the other instance.
   * <p/>
   * Any failure to implement this method correctly will be a great source of very subtle bugs.
   *
   * @return the deep clone.
   */
  public OutputFunction deriveForStorage();

  /**
   * Creates a cheaper version of the deep-copy of the output function. A pagebreak-derivate is created on every
   * possible pagebreak position and must contain all undo/rollback information to restore the state of any shared
   * object when a roll-back is requested.
   * <p/>
   * Any failure to implement this method correctly will be a great source of very subtle bugs.
   *
   * @return the deep clone.
   */
  public OutputFunction deriveForPagebreak();

  public InlineSubreportMarker[] getInlineSubreports();

  public void clearInlineSubreports( final SubReportProcessType processType );

  public void restart( final ReportState state ) throws ReportProcessingException;

  public boolean createRollbackInformation();

  public void groupBodyFinished( ReportEvent event );
}
