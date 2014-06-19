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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import java.util.HashMap;

import org.pentaho.reporting.engine.classic.core.metadata.DefaultStyleKeyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class StyleGroupRefReadHandler extends AbstractXmlReadHandler
{
  private HashMap<StyleKey,StyleMetaData> styles;
  private GlobalMetaDefinition styleGroups;
  private String bundle;

  /**
   * @param styles      the style-keys for which metadata is created.
   * @param styleGroups the global collection of style-groups.
   * @param bundle      the default resource-bundle that is used if the group defines no own bundle.
   * @noinspection AssignmentToCollectionOrArrayFieldFromParameter
   */
  public StyleGroupRefReadHandler(final HashMap<StyleKey,StyleMetaData> styles,
                                  final GlobalMetaDefinition styleGroups,
                                  final String bundle)
  {
    this.styles = styles;
    this.styleGroups = styleGroups;
    this.bundle = bundle;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    final String name = attrs.getValue(getUri(), "ref");
    if (name == null)
    {
      throw new ParseException("Attribute 'ref' is undefined", getLocator());
    }
    final StyleGroup group = styleGroups.getStyleGroup(name);
    if (group == null)
    {
      throw new ParseException
          ("Attribute 'ref' is invalid. There is no style-group '" + name + "' defined.", getLocator());
    }

    final StyleReadHandler[] data = group.getMetaData();
    for (int i = 0; i < data.length; i++)
    {
      final StyleReadHandler handler = data[i];
      final String keyName = handler.getName();
      final StyleKey key = StyleKey.getStyleKey(keyName);
      if (key == null)
      {
        throw new ParseException("There is no such key '" + keyName + "' defined.");
      }
      final String bundle;
      if (handler.getBundleName() != null)
      {
        bundle = handler.getBundleName();
      }
      else
      {
        bundle = this.bundle;
      }

      final DefaultStyleKeyMetaData metaData = new DefaultStyleKeyMetaData
          (key, handler.getPropertyEditor(), bundle, "style.",
              handler.isExpert(), handler.isPreferred(), handler.isHidden(), handler.isDeprecated(),
              handler.isExperimental(), handler.getCompatibilityLevel());
      styles.put(key, metaData);
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    return null;
  }
}
