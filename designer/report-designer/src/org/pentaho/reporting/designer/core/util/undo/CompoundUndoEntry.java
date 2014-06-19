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

public class CompoundUndoEntry implements UndoEntry
{
  private UndoEntry[] undoEntries;

  public CompoundUndoEntry(final UndoEntry... undoEntries)
  {
    this.undoEntries = undoEntries.clone();
  }

  public void undo(final ReportDocumentContext renderContext)
  {
    for (int i = undoEntries.length - 1; i >= 0; i--)
    {
      final UndoEntry undoEntry = undoEntries[i];
      undoEntry.undo(renderContext);
    }
  }

  public void redo(final ReportDocumentContext renderContext)
  {
    for (int i = 0; i < undoEntries.length; i++)
    {
      final UndoEntry undoEntry = undoEntries[i];
      undoEntry.redo(renderContext);
    }
  }

  public UndoEntry merge(final UndoEntry newEntry)
  {
    return null;
  }
}
