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

import java.awt.Image;
import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public interface DataFactoryMetaData extends MetaData {
  /**
   * Returns the icon for the datasource to be used in the UI.
   *
   * @param locale
   *          the locale.
   * @param iconKind
   *          one of java.beans.BeanInfo#ICON_COLOR_16x16, java.beans.BeanInfo#ICON_COLOR_32x32,
   *          java.beans.BeanInfo#ICON_MONO_16x16, or java.beans.BeanInfo#ICON_MONO_32x32
   * @return the image or null, if there is no icon.
   * @see java.beans.BeanInfo#getIcon(int) for the general semantics
   */
  public Image getIcon( final Locale locale, int iconKind );

  /**
   * Returns the used parameter in the query. The data factory must be open.
   *
   * @param element
   *          the element.
   * @param queryName
   *          the query name.
   * @return the list of parameters or null, if there is no information on referenced fields.
   */
  public String[] getReferencedFields( DataFactory element, String queryName, DataRow parameter );

  public ResourceReference[] getReferencedResources( DataFactory element, ResourceManager resourceManager,
      String queryName, DataRow parameter );

  public boolean isEditable();

  public boolean isEditorAvailable();

  public boolean isFreeFormQuery();

  public boolean isFormattingMetaDataSource();

  public DataSourcePlugin createEditor();

  public String getDisplayConnectionName( DataFactory dataFactory );

  public Object getQueryHash( DataFactory element, String queryName, DataRow parameter );
}
