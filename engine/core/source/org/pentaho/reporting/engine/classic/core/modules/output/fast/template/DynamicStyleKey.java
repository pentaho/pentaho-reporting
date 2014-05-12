/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.AbstractStructureVisitor;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class DynamicStyleKey
{

  private static class StyleInfoCollection
  {
    private static class StyleInfo
    {
      private int styleKeyIndex;
      private Object styleValue;

      private StyleInfo(final int styleKeyIndex, final Object styleValue)
      {
        this.styleKeyIndex = styleKeyIndex;
        this.styleValue = styleValue;
      }

      public boolean equals(final Object o)
      {
        if (this == o)
        {
          return true;
        }
        if (o == null || getClass() != o.getClass())
        {
          return false;
        }

        final StyleInfo styleInfo = (StyleInfo) o;

        if (styleKeyIndex != styleInfo.styleKeyIndex)
        {
          return false;
        }
        if (styleValue != null ? !styleValue.equals(styleInfo.styleValue) : styleInfo.styleValue != null)
        {
          return false;
        }
        return true;
      }

      public int hashCode()
      {
        int result = styleKeyIndex;
        result = 31 * result + (styleValue != null ? styleValue.hashCode() : 0);
        return result;
      }
    }

    private ArrayList<StyleInfo> styleInfo;
    private Integer hashCode;
    private long styleChangeTracker;

    private StyleInfoCollection(int length)
    {
      styleInfo = new ArrayList<StyleInfo>(length);
    }

    public void add(StyleKey k, Object o)
    {
      styleInfo.add(new StyleInfo(k.getIdentifier(), o));
    }

    public long getStyleChangeTracker()
    {
      return styleChangeTracker;
    }

    private void setStyleChangeTracker(final long styleChangeTracker)
    {
      this.styleChangeTracker = styleChangeTracker;
    }

    public boolean equals(final Object o)
    {
      if (this == o)
      {
        return true;
      }
      if (o == null || getClass() != o.getClass())
      {
        return false;
      }
      final StyleInfoCollection that = (StyleInfoCollection) o;
      if (ObjectUtilities.equal(hashCode, that.hashCode) == false)
      {
        return false;
      }

      if (!styleInfo.equals(that.styleInfo))
      {
        return false;
      }

      return true;
    }

    public int hashCode()
    {
      if (hashCode == null)
      {
        hashCode = styleInfo.hashCode();
      }
      return hashCode;
    }
  }

  private InstanceID rootBandId;
  private Map<InstanceID, StyleInfoCollection> dynamicStylePerElement;
  private Integer hashCode;

  private DynamicStyleKey(final InstanceID rootBandId,
                          final Map<InstanceID, StyleInfoCollection> dynamicStylePerElement)
  {
    this.rootBandId = rootBandId;
    this.dynamicStylePerElement = dynamicStylePerElement;
  }

  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    final DynamicStyleKey that = (DynamicStyleKey) o;
    if (ObjectUtilities.equal(hashCode, that.hashCode) == false)
    {
      return false;
    }

    if (!dynamicStylePerElement.equals(that.dynamicStylePerElement))
    {
      return false;
    }
    if (!rootBandId.equals(that.rootBandId))
    {
      return false;
    }

    return true;
  }

  public int hashCode()
  {
    if (hashCode == null)
    {
      int result = rootBandId.hashCode();
      result = 31 * result + dynamicStylePerElement.hashCode();
      hashCode = result;
    }
    return hashCode;
  }

  public static DynamicStyleKey create(Band band)
  {
    Map<InstanceID, StyleInfoCollection> style = new DynamicStyleKeyProducer().collect(band);
    return new DynamicStyleKey(band.getObjectID(), style);
  }

  private static class DynamicStyleKeyProducer extends AbstractStructureVisitor
  {
    private HashMap<InstanceID, StyleKey[]> dynamicTemplateInfo;
    private HashMap<InstanceID, StyleInfoCollection> styleInfo;

    public Map<InstanceID, StyleInfoCollection> collect(Band band)
    {
      dynamicTemplateInfo = (HashMap<InstanceID, StyleKey[]>)
          band.getAttribute(AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.FAST_EXPORT_DYNAMIC_STASH);
      if (dynamicTemplateInfo == null)
      {
        return Collections.emptyMap();
      }
      styleInfo = new HashMap<InstanceID, StyleInfoCollection>();
      inspectElement(band);
      traverseSection(band);
      return styleInfo;
    }

    protected void traverseSection(final Section section)
    {
      traverseSectionWithoutSubReports(section);
    }

    protected void inspectElement(final ReportElement element)
    {
      StyleKey[] styleIndex = dynamicTemplateInfo.get(element.getObjectID());
      if (styleIndex == null || styleIndex.length == 0)
      {
        return;
      }

      StyleInfoCollection lastCollection = (StyleInfoCollection)
          element.getAttribute(AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.FAST_EXPORT_ELEMENT_STASH);
      SimpleStyleSheet computedStyle = element.getComputedStyle();
      if (lastCollection != null &&
          lastCollection.getStyleChangeTracker() == computedStyle.getChangeTrackerHash())
      {
        // no changes
        styleInfo.put(element.getObjectID(), lastCollection);
        return;
      }

      StyleInfoCollection collection = new StyleInfoCollection(styleIndex.length);
      collection.setStyleChangeTracker(computedStyle.getChangeTrackerHash());
      for (StyleKey styleKey : styleIndex)
      {
        collection.add(styleKey, computedStyle.getStyleProperty(styleKey));
      }
      styleInfo.put(element.getObjectID(), collection);
    }
  }
}
