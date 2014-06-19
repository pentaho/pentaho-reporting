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

package org.pentaho.reporting.designer.core.util.undo;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Handles insert, remove and replacement of elements. Insert: old is null, remove: new is null.
 *
 * @author Thomas Morgner
 */
public class BandedSubreportEditUndoEntry implements UndoEntry
{
  private InstanceID target;
  private int position;
  private SubReport oldElement;
  private SubReport newElement;

  public BandedSubreportEditUndoEntry(final InstanceID target,
                                      final int position,
                                      final SubReport oldElement,
                                      final SubReport newElement)
  {
    this.target = target;
    this.position = position;
    this.oldElement = oldElement;
    this.newElement = newElement;
  }

  public void undo(final ReportDocumentContext renderContext)
  {
    final AbstractRootLevelBand elementById = (AbstractRootLevelBand)
        ModelUtility.findElementById(renderContext.getReportDefinition(), target);
    if (newElement != null)
    {
      elementById.removeSubreport(newElement);
    }
    if (oldElement != null)
    {
      elementById.addSubReport(position, oldElement);
    }
  }

  public void redo(final ReportDocumentContext renderContext)
  {
    final AbstractRootLevelBand elementById = (AbstractRootLevelBand)
        ModelUtility.findElementById(renderContext.getReportDefinition(), target);
    if (oldElement != null)
    {
      elementById.removeSubreport(oldElement);
    }
    if (newElement != null)
    {
      elementById.addSubReport(position, newElement);
    }
  }

  public UndoEntry merge(final UndoEntry newEntry)
  {
    return null;
  }
}
