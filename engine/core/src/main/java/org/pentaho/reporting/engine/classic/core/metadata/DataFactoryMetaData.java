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
