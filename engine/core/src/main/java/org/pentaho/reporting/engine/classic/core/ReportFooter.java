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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.ReportFooterType;

/**
 * A report band that appears as the very last band on the report.
 * <p/>
 * Note that if there is a page footer on the last page of your report, it will be printed below the report footer, the
 * logic being that the page footer *always* appears at the bottom of the page. In many cases, it makes better sense to
 * suppress the page footer on the last page of the report (leaving just the report footer on the final page).
 *
 * @author David Gilbert
 */
public class ReportFooter extends AbstractRootLevelBand {
  /**
   * Constructs a report footer containing no elements.
   */
  public ReportFooter() {
    setElementType( new ReportFooterType() );
  }

}
