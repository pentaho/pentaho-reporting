/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
