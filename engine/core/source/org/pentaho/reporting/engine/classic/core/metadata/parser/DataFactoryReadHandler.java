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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryCore;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DataFactoryReadHandler extends AbstractMetaDataReadHandler
{
  private String bundleName;
  private boolean editable;
  private boolean freeformQuery;
  private boolean formattingMetadataSource;
  private DataFactoryCore dataFactoryCore;
  private int compatibilityLevel;
  private String keyPrefix;

  public DataFactoryReadHandler()
  {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    super.startParsing(attrs);
    compatibilityLevel = ReportParserUtil.parseVersion(attrs.getValue(getUri(), "compatibility-level"));// NON-NLS
    keyPrefix = attrs.getValue(getUri(), "key-prefix"); // NON-NLS
    if (keyPrefix == null)
    {
      keyPrefix = "";
    }
    final String editable = attrs.getValue(getUri(), "editable"); // NON-NLS
    this.editable = editable == null || "true".equals(editable);
    freeformQuery = "true".equals(attrs.getValue(getUri(), "freeform-query")); // NON-NLS
    formattingMetadataSource = "true".equals(attrs.getValue(getUri(), "metadata-source")); // NON-NLS

    bundleName = attrs.getValue(getUri(), "bundle-name"); // NON-NLS

    final String metaDataCoreClass = attrs.getValue(getUri(), "impl"); // NON-NLS
    if (metaDataCoreClass != null)
    {
      dataFactoryCore = ObjectUtilities.loadAndInstantiate
          (metaDataCoreClass, DataFactoryReadHandler.class, DataFactoryCore.class);
      if (dataFactoryCore == null)
      {
        throw new ParseException("Attribute 'impl' references a invalid DataFactoryPropertyCore implementation.", getLocator());
      }
    }
    else
    {
      dataFactoryCore = new DefaultDataFactoryCore();
    }
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    return new DefaultDataFactoryMetaData
        (getName(), bundleName, keyPrefix, isExpert(), isPreferred(), isHidden(), isDeprecated(),
            editable, freeformQuery, formattingMetadataSource, isExperimental(),
            dataFactoryCore, compatibilityLevel);
  }
}
