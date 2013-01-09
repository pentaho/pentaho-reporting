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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.report.layouting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class DesignerTableContentProducer extends TableContentProducer
{
  private HashMap<InstanceID, Object> conflicts;

  public DesignerTableContentProducer(final SheetLayout sheetLayout,
                                      final OutputProcessorMetaData metaData)
  {
    super(sheetLayout, metaData);
    conflicts = new HashMap<InstanceID, Object>();
  }

  protected boolean startBox(final RenderBox box)
  {
    conflicts.remove(box.getInstanceId());
    return super.startBox(box);
  }

  protected void handleContentConflict(final RenderBox box)
  {
    // shall we collect more information?
    conflicts.put (box.getInstanceId(), Boolean.TRUE);
    super.handleContentConflict(box);
  }

  public Map<InstanceID, Object> computeConflicts (final RenderBox box)
  {
    computeDesigntimeConflicts(box);
    return Collections.unmodifiableMap(conflicts);
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    processBoxChilds(box);
  }
}
