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

package org.pentaho.reporting.engine.classic.testcases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.libraries.base.util.HashNMap;

@SuppressWarnings("HardCodedStringLiteral")
public class AttributeMetaDataValidationTest extends TestCase
{
  private static final Log logger = LogFactory.getLog(AttributeMetaDataValidationTest.class);
  private ArrayList<String> missingProperties;

  public AttributeMetaDataValidationTest()
  {
  }

  public void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testMetaData()
  {
    int invalidExpressionsCounter = 0;
    final HashNMap<String,ElementMetaData> expressionsByGroup = new HashNMap<String,ElementMetaData>();
    missingProperties = new ArrayList<String>();

    final ElementTypeRegistry registry = ElementTypeRegistry.getInstance();
    final ElementMetaData[] elementMetaDatas = registry.getAllElementTypes();
    for (int i = 0; i < elementMetaDatas.length; i++)
    {
      final ElementMetaData metaData = elementMetaDatas[i];
      if (metaData == null)
      {
        logger.warn("Null Expression encountered");
        continue;
      }

      missingProperties.clear();

      try
      {
        final Object type = metaData.create();
      }
      catch (InstantiationException e)
      {
        fail("metadata creation failed");

      }

      final String typeName = metaData.getName();
      logger.debug("Processing " + typeName);

      final Locale locale = Locale.getDefault();
      final String displayName = metaData.getDisplayName(locale);
      if (isValid(displayName) == false)
      {
        logger.warn("ElementType '" + typeName + ": No valid display name");
      }
      if (metaData.isDeprecated())
      {
        final String deprecateMessage = metaData.getDeprecationMessage(locale);
        if (isValid(deprecateMessage) == false)
        {
          logger.warn("ElementType '" + typeName + ": No valid deprecate message");
        }
      }
      final String grouping = metaData.getGrouping(locale);
      if (isValid(grouping) == false)
      {
        logger.warn("ElementType '" + typeName + ": No valid grouping message");
      }

      expressionsByGroup.add(grouping, metaData);

      final StyleMetaData[] styleMetaDatas = metaData.getStyleDescriptions();
      for (int j = 0; j < styleMetaDatas.length; j++)
      {
        final StyleMetaData propertyMetaData = styleMetaDatas[j];
        final String propertyDisplayName = propertyMetaData.getDisplayName(locale);
        if (isValid(propertyDisplayName) == false)
        {
          logger.warn("ElementType '" + typeName + ": Style " + propertyMetaData.getName() + ": No DisplayName");
        }

        final String propertyGrouping = propertyMetaData.getGrouping(locale);
        if (isValid(propertyGrouping) == false)
        {
          logger.warn("ElementType '" + typeName + ": Style " + propertyMetaData.getName() + ": Grouping is not valid");
        }
        if (propertyMetaData.isDeprecated())
        {
          final String deprecateMessage = propertyMetaData.getDeprecationMessage(locale);
          if (isValid(deprecateMessage) == false)
          {
            logger.warn(
                "ElementType '" + typeName + ": Style " + propertyMetaData.getName() + ": No valid deprecate message");
          }
        }
      }


      final AttributeMetaData[] attributeMetaDatas = metaData.getAttributeDescriptions();
      for (int j = 0; j < attributeMetaDatas.length; j++)
      {
        final AttributeMetaData propertyMetaData = attributeMetaDatas[j];
        final String propertyDisplayName = propertyMetaData.getDisplayName(locale);
        if (isValid(propertyDisplayName) == false)
        {
          logger.warn("ElementType '" + typeName + ": Attr " + propertyMetaData.getName() + ": No DisplayName");
        }

        final String propertyGrouping = propertyMetaData.getGrouping(locale);
        if (isValid(propertyGrouping) == false)
        {
          logger.warn("ElementType '" + typeName + ": Attr " + propertyMetaData.getName() + ": Grouping is not valid");
        }
        if (propertyMetaData.isDeprecated())
        {
          final String deprecateMessage = propertyMetaData.getDeprecationMessage(locale);
          if (isValid(deprecateMessage) == false)
          {
            logger.warn(
                "ElementType '" + typeName + ": Attr " + propertyMetaData.getName() + ": No valid deprecate message");
          }
        }
      }

      System.err.flush();
      try
      {
        Thread.sleep(25);
      }
      catch (InterruptedException e)
      {
      }

      for (int x = 0; x < missingProperties.size(); x++)
      {
        final String property = missingProperties.get(x);
        System.out.println(property);
      }

      if (missingProperties.isEmpty() == false)
      {
        invalidExpressionsCounter += 1;
        missingProperties.clear();
      }
      System.out.flush();
      try
      {
        Thread.sleep(25);
      }
      catch (InterruptedException e)
      {
      }
    }

    assertEquals(0, invalidExpressionsCounter);

  }

  private boolean isValid(final String translation)
  {
    if (translation == null)
    {
      return false;
    }
    if (translation.length() > 2 &&
        translation.charAt(0) == '!' &&
        translation.charAt(translation.length() - 1) == '!')
    {
      final String retval = translation.substring(1, translation.length() - 1);
      missingProperties.add(retval);
      return false;
    }
    return true;
  }

}
