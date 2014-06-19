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

package org.pentaho.reporting.designer.core.editor.report.drag;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class CompoundDragOperation implements MouseDragOperation
{
  private ArrayList<MouseDragOperation> operations;

  public CompoundDragOperation()
  {
    operations = new ArrayList<MouseDragOperation>();
  }

  public void add(final MouseDragOperation operation)
  {
    operations.add(operation);
  }

  public void update(final Point2D normalizedPoint, final double zoomFactor)
  {
    for (int i = 0; i < operations.size(); i++)
    {
      final MouseDragOperation operation = operations.get(i);
      operation.update(normalizedPoint, zoomFactor);
    }
  }

  public void finish()
  {
    for (int i = 0; i < operations.size(); i++)
    {
      final MouseDragOperation operation = operations.get(i);
      operation.finish();
    }
  }

}
