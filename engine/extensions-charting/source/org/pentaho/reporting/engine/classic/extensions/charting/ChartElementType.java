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

package org.pentaho.reporting.engine.classic.extensions.charting;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentType;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import pt.webdetails.cgg.scripts.CompoundScriptResourceLoader;
import pt.webdetails.cgg.scripts.DefaultScriptFactory;
import pt.webdetails.cgg.scripts.SystemScriptResourceLoader;
import pt.webdetails.cgg.scripts.VirtualScriptResourceLoader;

public class ChartElementType extends ContentType
{
  public static final ChartElementType INSTANCE = new ChartElementType();

  public ChartElementType()
  {
    super("chart");
  }

  protected String queryChartValue(final ReportElement element)
  {
    if (element == null)
    {
      throw new NullPointerException("Element must never be null.");
    }

    // todo: Allow CLOBs and other object types as well.
    final Object attribute = element.getAttribute(ChartingModule.NAMESPACE, "chart-definition");
    if (attribute instanceof String)
    {
      return (String) attribute;
    }
    return null;
  }


  public Object getValue(final ExpressionRuntime runtime,
                         final ReportElement element)
  {
    String rawValue = queryChartValue(element);
    if (rawValue == null)
    {
      return filter(runtime, element,
          element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE));
    }

    VirtualScriptResourceLoader vloader = new VirtualScriptResourceLoader();
    vloader.put("*basescript*", rawValue);

    ProcessingContext processingContext = runtime.getProcessingContext();
    CompoundScriptResourceLoader loader = new CompoundScriptResourceLoader(vloader,
        new ResourceBundleScriptLoader(processingContext.getResourceManager(), processingContext.getContentBase()),
        new SystemScriptResourceLoader());
    DefaultScriptFactory scriptFactory = new DefaultScriptFactory();
    scriptFactory.setResourceLoader(loader);

    RawCgg cgg = new RawCgg();
    cgg.setScriptFactory(scriptFactory);

    CggDrawable drawable = new CggDrawable(cgg, "*basescript*");
    drawable.setParameter(runtime.getDataRow());
    return drawable;
  }
}
