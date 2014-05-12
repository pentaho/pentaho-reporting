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

package org.pentaho.reporting.designer.core.editor.report.lineal;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.lineal.GuideLine;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class UpdateVerticalGuidelineUndoEntry implements UndoEntry
{
  private int index;
  private GuideLine guideLine;
  private GuideLine oldGuideLine;
  private InstanceID id;

  public UpdateVerticalGuidelineUndoEntry(final int index,
                                          final GuideLine guideLine,
                                          final GuideLine oldGuideLine,
                                          final InstanceID id)
  {
    this.index = index;
    this.guideLine = guideLine;
    this.oldGuideLine = oldGuideLine;
    this.id = id;
  }

  public void undo(final ReportDocumentContext context)
  {
    final AbstractReportDefinition abstractReportDefinition = context.getReportDefinition();
    final Band band = (Band) ModelUtility.findElementById(abstractReportDefinition, id);
    final LinealModel linealModel = ModelUtility.getVerticalLinealModel(band);
    linealModel.updateGuideLine(index, oldGuideLine);
  }

  public void redo(final ReportDocumentContext context)
  {
    final AbstractReportDefinition abstractReportDefinition = context.getReportDefinition();
    final Band band = (Band) ModelUtility.findElementById(abstractReportDefinition, id);
    final LinealModel linealModel = ModelUtility.getVerticalLinealModel(band);
    linealModel.updateGuideLine(index, guideLine);
  }

  public UndoEntry merge(final UndoEntry other)
  {
    return null;
  }
}
