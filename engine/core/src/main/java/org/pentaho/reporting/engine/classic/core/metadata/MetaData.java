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

package org.pentaho.reporting.engine.classic.core.metadata;

import java.util.Locale;

public interface MetaData {
  public static final String VALUEROLE_FORMULA = "Formula";
  public static final String VALUEROLE_VALUE = "Value";
  public static final String VALUEROLE_RESOURCE = "Resource";
  public static final String VALUEROLE_CONTENT = "Content";
  public static final String VALUEROLE_FIELD = "Field";
  public static final String VALUEROLE_GROUP = "Group";
  public static final String VALUEROLE_QUERY = "Query";
  public static final String VALUEROLE_MESSAGE = "Message";
  public static final String VALUEROLE_BUNDLE_KEY = "Bundle-Key";
  public static final String VALUEROLE_BUNDLE_NAME = "Bundle-Name";
  public static final String VALUEROLE_ELEMENT_NAME = "ElementName";
  public static final String VALUEROLE_DATEFORMAT = "DateFormat";
  public static final String VALUEROLE_NUMBERFORMAT = "NumberFormat";

  public String getName();

  public String getDisplayName( Locale locale );

  public String getMetaAttribute( String attributeName, Locale locale );

  public String getGrouping( Locale locale );

  public int getGroupingOrdinal( Locale locale );

  public int getItemOrdinal( Locale locale );

  public String getDeprecationMessage( Locale locale );

  public String getDescription( Locale locale );

  public boolean isDeprecated();

  public boolean isExpert();

  public boolean isPreferred();

  public boolean isHidden();

  @Deprecated
  public boolean isExperimental();

  public MaturityLevel getFeatureMaturityLevel();

  public int getCompatibilityLevel();

  public String getKeyPrefix();

  public String getBundleLocation();
}
