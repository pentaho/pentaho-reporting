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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model;

import java.util.ArrayList;

/**
 * This class encapsulates a whole test run, which is an alternating sequence of sources and result validators.
 *
 * @author Thomas Morgner
 */
public class ValidationSequence {
  private ArrayList list;
  private int pageWidth;
  private boolean strict;

  public ValidationSequence() {
    list = new ArrayList();
  }

  public void addSourceChunk( final SourceChunk sourceChunk ) {
    list.add( sourceChunk );
  }

  public void addResultTable( final ResultTable resultTable ) {
    list.add( resultTable );
  }

  public int getPageWidth() {
    return pageWidth;
  }

  public void setPageWidth( final int pageWidth ) {
    this.pageWidth = pageWidth;
  }

  public ArrayList getContents() {
    return list;
  }

  public boolean isStrict() {
    return strict;
  }

  public void setStrict( final boolean strict ) {
    this.strict = strict;
  }
}
