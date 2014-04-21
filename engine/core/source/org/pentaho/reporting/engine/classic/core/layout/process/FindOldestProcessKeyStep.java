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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

public class FindOldestProcessKeyStep extends IterateSimpleStructureProcessStep
{
  private ReportStateKey key;
  private boolean finishedPaginate;

  public FindOldestProcessKeyStep()
  {
  }

  public ReportStateKey find(final RenderBox box)
  {
    if (box.getProcessKeyStepAge() == box.getChangeTracker() &&
        box.isProcessKeyFinish() == box.isFinishedPaginate())
    {
      return box.getProcessKeyCached();
    }

    key = null;
    finishedPaginate = box.isFinishedPaginate();
    startProcessing(box);
    box.setProcessKeyCached(key);
    return key;
  }

  protected void processOtherNode(final RenderNode node)
  {
    final ReportStateKey stateKey = node.getStateKey();
    if (stateKey == null || stateKey.isInlineSubReportState())
    {
      return;
    }

    if (key == null)
    {
      key = stateKey;
      return;
    }

    if (stateKey.getSequenceCounter() > key.getSequenceCounter())
    {
      key = stateKey;
    }
  }

  protected boolean startBox(final RenderBox box)
  {
    if (box.getProcessKeyStepAge() == box.getChangeTracker() &&
        box.isProcessKeyFinish() == box.isFinishedPaginate())
    {
      key = box.getProcessKeyCached();
      return false;
    }

    processOtherNode(box);
    if (finishedPaginate == true)
    {
      box.setFinishedPaginate(finishedPaginate);
    }
    box.setProcessKeyCached(key);
    return true;
  }
}
