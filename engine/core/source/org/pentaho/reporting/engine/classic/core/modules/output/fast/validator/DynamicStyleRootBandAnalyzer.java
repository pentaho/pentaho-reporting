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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.validator;

import java.util.HashMap;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.AbstractStructureVisitor;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.HashNMap;

public class DynamicStyleRootBandAnalyzer extends AbstractStructureVisitor
{
  private HashNMap<InstanceID, StyleKey> dynamicTemplateInfo;
  private HashNMap<String, StyleKey> styleByElementName;
  private HashNMap<InstanceID, StyleKey> styleById;

  public DynamicStyleRootBandAnalyzer(final HashNMap<String, StyleKey> styleByElementName,
                                      final HashNMap<InstanceID, StyleKey> styleById)
  {
    this.styleByElementName = styleByElementName;
    this.styleById = styleById;
    this.dynamicTemplateInfo = new HashNMap<InstanceID, StyleKey>();
  }

  public void compute(Section rootLevelBand)
  {
    this.dynamicTemplateInfo.clear();
    inspectElement(rootLevelBand);
    traverseSection(rootLevelBand);

    HashMap<InstanceID, StyleKey[]> stash = buildStash();
    rootLevelBand.setAttribute
        (AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.FAST_EXPORT_DYNAMIC_STASH, stash);
  }

  private HashMap<InstanceID, StyleKey[]> buildStash()
  {
    HashMap<InstanceID, StyleKey[]> stash = new HashMap<InstanceID, StyleKey[]>();

    for (InstanceID id : this.dynamicTemplateInfo.keySet())
    {
      int valueCount = this.dynamicTemplateInfo.getValueCount(id);
      StyleKey[] styleKeys = this.dynamicTemplateInfo.toArray(id, new StyleKey[valueCount]);
      stash.put(id, styleKeys);
    }
    return stash;
  }


  protected void traverseSection(final Section section)
  {
    traverseSectionWithoutSubReports(section);
  }

  protected void inspectElement(final ReportElement element)
  {

    Object hide = element.getAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ONLY_SHOW_CHANGING_VALUES);
    if (Boolean.TRUE.equals(hide))
    {
      dynamicTemplateInfo.add(element.getObjectID(), ElementStyleKeys.VISIBLE);
    }

    traverseStyleExpressions(element);
  }

  protected void inspectStyleExpression(final ReportElement element,
                                        final StyleKey styleKey,
                                        final Expression expression,
                                        final ExpressionMetaData expressionMetaData)
  {
    dynamicTemplateInfo.add(element.getObjectID(), styleKey);
  }
}
