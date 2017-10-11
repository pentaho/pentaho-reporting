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

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.Serializable;

/**
 * The attribute core enables semantic inspections of attribute values.
 */
public interface AttributeCore extends Serializable {
  public String[] getReferencedFields( AttributeMetaData metaData, ReportElement element, Object attributeValue );

  public String[] getReferencedGroups( AttributeMetaData metaData, ReportElement element, Object attributeValue );

  public ResourceReference[] getReferencedResources( AttributeMetaData metaData, ReportElement element,
      ResourceManager resourceManager, Object attributeValue );

  public String[] getExtraCalculationFields( AttributeMetaData metaData );
}
