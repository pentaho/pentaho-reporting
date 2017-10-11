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

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.filter.types.bands.ReportHeaderType;

/**
 * A report band that is printed once only at the beginning of the report.
 * <p/>
 * A flag can be set forcing the report generator to start a new page after printing the report header.
 * <p/>
 * Note that if there is a page header on the first page of your report, it will be printed above the report header, the
 * logic being that the page header *always* appears at the top of the page. In many cases, it makes better sense to
 * suppress the page header on the first page of the report (leaving just the report header on page 1).
 *
 * @author David Gilbert
 * @author Thomas Morgner
 */
public class ReportHeader extends AbstractRootLevelBand {
  /**
   * Constructs a report header, initially containing no elements.
   */
  public ReportHeader() {
    setElementType( new ReportHeaderType() );
  }
}
