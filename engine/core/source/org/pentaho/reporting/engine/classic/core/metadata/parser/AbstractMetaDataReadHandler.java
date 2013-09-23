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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class AbstractMetaDataReadHandler extends AbstractXmlReadHandler
{
  private String name;
  private boolean preferred;
  private boolean expert;
  private boolean hidden;
  private boolean deprecated;
  private String bundle;
  private boolean experimental;
  private int compatibilityLevel;

  protected AbstractMetaDataReadHandler()
  {
  }

  protected AbstractMetaDataReadHandler(final String bundle)
  {
    this.bundle = bundle;
  }

  protected void startParsing(final Attributes attrs) throws SAXException
  {
    if (isDerivedName() == false)
    {
      name = attrs.getValue(getUri(), "name"); // NON-NLS
      if (name == null)
      {
        throw new ParseException("Attribute 'name' is undefined", getLocator());
      }
    }
    final String bundleFromAttributes = attrs.getValue(getUri(), "bundle-name"); // NON-NLS
    if (bundleFromAttributes != null)
    {
      bundle = bundleFromAttributes;
    }

    experimental = "true".equals(attrs.getValue(getUri(), "experimental")); // NON-NLS
    expert = "true".equals(attrs.getValue(getUri(), "expert")); // NON-NLS
    hidden = "true".equals(attrs.getValue(getUri(), "hidden")); // NON-NLS
    preferred = "true".equals(attrs.getValue(getUri(), "preferred")); // NON-NLS
    deprecated = "true".equals(attrs.getValue(getUri(), "deprecated")); // NON-NLS
    compatibilityLevel = ReportParserUtil.parseVersion(attrs.getValue(getUri(), "compatibility-level"));
  }

  protected boolean isDerivedName()
  {
    return false;
  }

  public int getCompatibilityLevel()
  {
    return compatibilityLevel;
  }

  public String getName()
  {
    return name;
  }

  public boolean isPreferred()
  {
    return preferred;
  }

  public boolean isExpert()
  {
    return expert;
  }

  public boolean isHidden()
  {
    return hidden;
  }

  public boolean isDeprecated()
  {
    return deprecated;
  }

  public String getBundle()
  {
    return bundle;
  }

  public boolean isExperimental()
  {
    return experimental;
  }
}
