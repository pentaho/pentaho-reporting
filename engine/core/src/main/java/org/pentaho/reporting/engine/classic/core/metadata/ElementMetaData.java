/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
