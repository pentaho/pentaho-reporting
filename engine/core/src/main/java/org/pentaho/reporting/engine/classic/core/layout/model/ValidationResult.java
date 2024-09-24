/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.model;

public enum ValidationResult {
  UNKNOWN, OK, TABLE_BODY_NEEDS_MORE_ROWS, TABLE_ROW_OPEN, CANVAS_BOX_OPEN, BOX_OPEN_NEXT_PENDING, PLACEHOLDER_BOX_OPEN, PARAGRAPH_BOX_OPEN, CELL_BOX_OPEN, TABLE_BOX_OPEN, TABLE_BOX_MISSING_DATA, TABLE_BODY_MISSING_ROWS, TABLE_BOX_PREVENTS_PAGINATION,
}
