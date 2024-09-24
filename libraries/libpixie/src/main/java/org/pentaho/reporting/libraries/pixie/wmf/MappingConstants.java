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

package org.pentaho.reporting.libraries.pixie.wmf;

/**
 * Various MappingConstants defined in the Windows API.
 */
public interface MappingConstants {
  public static final int MM_TEXT = 1;
  public static final int MM_LOMETRIC = 2;
  public static final int MM_HIMETRIC = 3;
  public static final int MM_LOENGLISH = 4;
  public static final int MM_HIENGLISH = 5;
  public static final int MM_TWIPS = 6;
  public static final int MM_ISOTROPIC = 7;
  public static final int MM_ANISOTROPIC = 8;

  /* Min and Max Mapping Mode values */
  public static final int MM_MIN = MM_TEXT;
  public static final int MM_MAX = MM_ANISOTROPIC;
  public static final int MM_MAX_FIXEDSCALE = MM_TWIPS;
}
