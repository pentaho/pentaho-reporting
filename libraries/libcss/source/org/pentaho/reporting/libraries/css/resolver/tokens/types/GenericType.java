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
 * Copyright (c) 2007 - 2009 Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.css.resolver.tokens.types;

import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;

/**
 * This is raw data. Whether or not the raw-data is interpreted is up to the
 * output target. However, it is suggested, that at least Images, Drawables
 * and Strings should be implemented (if the output format supports that).
 *
 * @author Thomas Morgner
 */
public interface GenericType extends ContentToken
{
  public Object getRaw();
}