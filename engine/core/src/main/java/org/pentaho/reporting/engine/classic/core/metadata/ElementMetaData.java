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

package org.pentaho.reporting.engine.classic.core.metadata;

import java.awt.Image;
import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.style.StyleKey;

public interface ElementMetaData extends MetaData {
  public enum TypeClassification {
    DATA, HEADER, RELATIONAL_HEADER, FOOTER, RELATIONAL_FOOTER, SECTION, SUBREPORT, CONTROL
  }

  /**
   * @param locale
   *          the locale.
   * @param iconKind
   *          one of java.beans.BeanInfo#ICON_COLOR_16x16, java.beans.BeanInfo#ICON_COLOR_32x32,
   *          java.beans.BeanInfo#ICON_MONO_16x16, or java.beans.BeanInfo#ICON_MONO_32x32
   * @return the image or null, if there is no icon.
   * @see java.beans.BeanInfo#getIcon(int) for the general semantics
   */
  public Image getIcon( final Locale locale, int iconKind );

  public AttributeMetaData[] getAttributeDescriptions();

  public StyleMetaData[] getStyleDescriptions();

  public AttributeMetaData getAttributeDescription( String namespace, String name );

  public StyleMetaData getStyleDescription( StyleKey name );

  public ElementType create() throws InstantiationException;

  /**
   * Indicates whether the element described here is a container element, like "band". Such element-types are tied to a
   * specific element instance and cannot be shared freely.
   *
   * @return
   */
  public boolean isContainerElement();

  public Class<?> getContentType();

  public Class<? extends ElementType> getElementType();

  public TypeClassification getReportElementType();

  /**
   * The namespace of the elemnet in an PRPT bundle.
   *
   * @return
   */
  public String getNamespace();
}
