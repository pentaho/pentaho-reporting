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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

/**
 * Applies the createRollbackInformation-marker to all closed boxes and applies the pending marker to all currently open
 * boxes. During a roll-back, we can use these markers to identify boxes that have been added since the last
 * createRollbackInformation to remove them from the model.
 *
 * @author Thomas Morgner
 */
public final class ApplyCommitStep extends IterateSimpleStructureProcessStep
{
  public ApplyCommitStep()
  {
  }

  public void compute(final LogicalPageBox pageBox)
  {
    startProcessing(pageBox);
  }

  protected boolean startBox(final RenderBox box)
  {
    if (box.isCommited())
    {
      return false;
    }

    box.commit();
    return true;
  }
}
