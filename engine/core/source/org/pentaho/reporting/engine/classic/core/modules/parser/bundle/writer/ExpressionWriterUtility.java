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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import java.io.IOException;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.FormulaFunction;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.MetaDataLookupException;
import org.pentaho.reporting.engine.classic.core.metadata.ResourceReference;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

public class ExpressionWriterUtility
{
  private ExpressionWriterUtility()
  {
  }

  public static boolean isElementLayoutExpressionActive(final BundleWriterState state)
  {
    if (state == null)
    {
      throw new NullPointerException();
    }

    final ExpressionCollection exp = state.getReport().getExpressions();
    final ExpressionRegistry registry = ExpressionRegistry.getInstance();
    for (int i = 0; i < exp.size(); i++)
    {
      final Expression expression = exp.getExpression(i);
      if (registry.isExpressionRegistered(expression.getClass().getName()) == false)
      {
        continue;
      }

      final ExpressionMetaData emd =
          registry.getExpressionMetaData(expression.getClass().getName());
      if (emd.isElementLayoutProcessor())
      {
        return true;
      }
    }
    return false;
  }

  public static void writeElementLayoutExpressions(final WriteableDocumentBundle bundle,
                                                   final BundleWriterState state,
                                                   final XmlWriter writer)
      throws IOException, BundleWriterException
  {
    if (state == null)
    {
      throw new NullPointerException();
    }
    if (writer == null)
    {
      throw new NullPointerException();
    }
    if (bundle == null)
    {
      throw new NullPointerException();
    }


    final ExpressionCollection exp = state.getReport().getExpressions();
    final ExpressionRegistry registry = ExpressionRegistry.getInstance();
    for (int i = 0; i < exp.size(); i++)
    {
      final Expression expression = exp.getExpression(i);
      if (registry.isExpressionRegistered(expression.getClass().getName()) == false)
      {
        continue;
      }

      final ExpressionMetaData emd =
          registry.getExpressionMetaData(expression.getClass().getName());
      if (emd.isElementLayoutProcessor())
      {
        writeExpression(bundle, state, expression, writer, BundleNamespaces.LAYOUT, "expression");// NON-NLS
      }
    }
  }

  public static boolean isGlobalLayoutExpressionActive(final BundleWriterState state)
  {
    if (state == null)
    {
      throw new NullPointerException();
    }

    final ExpressionCollection exp = state.getReport().getExpressions();
    final ExpressionRegistry registry = ExpressionRegistry.getInstance();
    for (int i = 0; i < exp.size(); i++)
    {
      final Expression expression = exp.getExpression(i);
      if (registry.isExpressionRegistered(expression.getClass().getName()) == false)
      {
        continue;
      }

      final ExpressionMetaData emd =
          registry.getExpressionMetaData(expression.getClass().getName());
      if (emd.isGlobalLayoutProcessor())
      {
        return true;
      }
    }
    return false;
  }

  public static void writeGlobalLayoutExpressions(final WriteableDocumentBundle bundle,
                                                  final BundleWriterState state,
                                                  final XmlWriter writer)
      throws IOException, BundleWriterException
  {
    if (state == null)
    {
      throw new NullPointerException();
    }
    if (writer == null)
    {
      throw new NullPointerException();
    }
    if (bundle == null)
    {
      throw new NullPointerException();
    }


    final ExpressionCollection exp = state.getReport().getExpressions();
    final ExpressionRegistry registry = ExpressionRegistry.getInstance();
    for (int i = 0; i < exp.size(); i++)
    {
      final Expression expression = exp.getExpression(i);
      if (registry.isExpressionRegistered(expression.getClass().getName()) == false)
      {
        continue;
      }

      final ExpressionMetaData emd =
          registry.getExpressionMetaData(expression.getClass().getName());
      if (emd.isGlobalLayoutProcessor())
      {
        writeExpression(bundle, state, expression, writer, BundleNamespaces.LAYOUT, "expression");// NON-NLS
      }
    }
  }

  public static void writeDataExpressions(final WriteableDocumentBundle bundle,
                                          final BundleWriterState state,
                                          final XmlWriter writer)
      throws IOException, BundleWriterException
  {
    if (state == null)
    {
      throw new NullPointerException();
    }
    if (writer == null)
    {
      throw new NullPointerException();
    }
    if (bundle == null)
    {
      throw new NullPointerException();
    }


    final ExpressionCollection exp = state.getReport().getExpressions();
    final ExpressionRegistry registry = ExpressionRegistry.getInstance();
    for (int i = 0; i < exp.size(); i++)
    {
      final Expression expression = exp.getExpression(i);
      if (registry.isExpressionRegistered(expression.getClass().getName()) == false)
      {
        continue;
      }

      final ExpressionMetaData emd =
          registry.getExpressionMetaData(expression.getClass().getName());
      if (emd.isGlobalLayoutProcessor() || emd.isElementLayoutProcessor())
      {
        continue;
      }

      writeExpression(bundle, state, expression, writer, BundleNamespaces.DATADEFINITION, "expression");// NON-NLS
    }
  }

  public static void writeExpression(final WriteableDocumentBundle bundle,
                                     final BundleWriterState state,
                                     final Expression expression,
                                     final XmlWriter writer,
                                     final String namespaceUri,
                                     final String expressionTag)
      throws IOException, BundleWriterException
  {
    if (state == null)
    {
      throw new NullPointerException();
    }
    if (bundle == null)
    {
      throw new NullPointerException();
    }
    if (expression == null)
    {
      throw new NullPointerException();
    }
    if (writer == null)
    {
      throw new NullPointerException();
    }
    if (namespaceUri == null)
    {
      throw new NullPointerException();
    }
    if (expressionTag == null)
    {
      throw new NullPointerException();
    }

    final AttributeList expressionAttrList = new AttributeList();
    if (expression.getName() != null)
    {
      expressionAttrList.setAttribute(namespaceUri, "name", expression.getName());
    }

    if (expression.getDependencyLevel() > 0)
    {
      expressionAttrList.setAttribute(namespaceUri, "deplevel",// NON-NLS
          String.valueOf(expression.getDependencyLevel()));
    }

    writeExpressionCore(bundle, state, expression, writer, namespaceUri, expressionTag, expressionAttrList);
  }

  public static void writeStyleExpression(final WriteableDocumentBundle bundle,
                                          final BundleWriterState state,
                                          final Expression expression,
                                          final XmlWriter writer,
                                          final StyleKey styleKey,
                                          final String namespaceUri,
                                          final String expressionTag)
      throws IOException, BundleWriterException
  {
    if (bundle == null)
    {
      throw new NullPointerException();
    }
    if (state == null)
    {
      throw new NullPointerException();
    }
    if (expression == null)
    {
      throw new NullPointerException();
    }
    if (writer == null)
    {
      throw new NullPointerException();
    }
    if (styleKey == null)
    {
      throw new NullPointerException();
    }
    if (namespaceUri == null)
    {
      throw new NullPointerException();
    }
    if (expressionTag == null)
    {
      throw new NullPointerException();
    }

    final AttributeList expressionAttrList = new AttributeList();
    expressionAttrList.setAttribute(namespaceUri, "style-key", styleKey.getName());// NON-NLS

    writeExpressionCore(bundle, state, expression, writer, namespaceUri, expressionTag, expressionAttrList);
  }

  public static void writeExpressionCore(final WriteableDocumentBundle bundle,
                                         final BundleWriterState state,
                                         final Expression expression,
                                         final XmlWriter writer,
                                         final String namespaceUri,
                                         final String expressionTag,
                                         final AttributeList expressionAttrList)
      throws IOException, BundleWriterException
  {
    if (bundle == null)
    {
      throw new NullPointerException();
    }
    if (state == null)
    {
      throw new NullPointerException();
    }
    if (expression == null)
    {
      throw new NullPointerException();
    }
    if (writer == null)
    {
      throw new NullPointerException();
    }
    if (namespaceUri == null)
    {
      throw new NullPointerException();
    }
    if (expressionTag == null)
    {
      throw new NullPointerException();
    }
    if (expressionAttrList == null)
    {
      throw new NullPointerException();
    }

    if (expression instanceof FormulaExpression)
    {
      final FormulaExpression fe = (FormulaExpression) expression;
      if (StringUtils.isEmpty(fe.getFormula()))
      {
        return;
      }
      expressionAttrList.setAttribute(namespaceUri, "formula", fe.getFormula());// NON-NLS
      writer.writeTag(namespaceUri, expressionTag, expressionAttrList, XmlWriterSupport.CLOSE);
      return;
    }

    if (expression instanceof FormulaFunction)
    {
      final FormulaFunction fe = (FormulaFunction) expression;
      if (StringUtils.isEmpty(fe.getFormula()))
      {
        return;
      }
      expressionAttrList.setAttribute(namespaceUri, "formula", fe.getFormula());// NON-NLS
      expressionAttrList.setAttribute(namespaceUri, "initial", fe.getInitial());// NON-NLS
      writer.writeTag(namespaceUri, expressionTag, expressionAttrList, XmlWriterSupport.CLOSE);
      return;
    }

    try
    {

      final String expressionId = expression.getClass().getName();
      expressionAttrList.setAttribute(namespaceUri, "class", expressionId);

      final ExpressionMetaData emd;
      if (ExpressionRegistry.getInstance().isExpressionRegistered(expressionId))
      {
        emd = ExpressionRegistry.getInstance().getExpressionMetaData(expressionId);
      }
      else
      {
        emd = null;
      }

      if (emd != null)
      {
        final BeanUtility bu = new BeanUtility(expression);
        final ExpressionPropertyMetaData[] expressionProperties = emd.getPropertyDescriptions();
        boolean propertiesOpen = false;
        for (int i = 0; i < expressionProperties.length; i++)
        {
          final ExpressionPropertyMetaData metaData = expressionProperties[i];
          final String propertyName = metaData.getName();
          if (isFilteredProperty(propertyName))
          {
            continue;
          }
          if (metaData.isComputed())
          {
            continue;
          }
          if (propertiesOpen == false)
          {
            writer.writeTag(namespaceUri, expressionTag, expressionAttrList, XmlWriterSupport.OPEN);
            writer.writeTag(namespaceUri, "properties", XmlWriterSupport.OPEN);// NON-NLS
            propertiesOpen = true;
          }

          copyStaticResources(bundle, state, expression, bu, expressionProperties);
          writeExpressionParameter(writer, bu, propertyName, namespaceUri);
        }

        if (propertiesOpen)
        {
          writer.writeCloseTag();
          writer.writeCloseTag();
        }
        else
        {
          writer.writeTag(namespaceUri, expressionTag, expressionAttrList, XmlWriterSupport.CLOSE);
        }
      }
      else
      {
        // the classic way, in case the expression does not provide any meta-data. This is
        // in the code for legacy reasons, as there are many expression implementations out there
        // that do not yet provide meta-data descriptions ..

        final BeanUtility beanUtility = new BeanUtility(expression);
        final String[] propertyNames = beanUtility.getProperties();

        for (int i = 0; i < propertyNames.length; i++)
        {
          final String key = propertyNames[i];
          // filter some of the standard properties. These are system-properties
          // and are set elsewhere
          if (isFilteredProperty(key))
          {
            continue;
          }

          writeExpressionParameter(writer, beanUtility, key, namespaceUri);
        }
      }
    }
    catch (IOException ioe)
    {
      throw ioe;
    }
    catch (Exception e)
    {
      throw new BundleWriterException("Unable to extract or write properties.", e);
    }
  }

  private static boolean isFilteredProperty(final String name)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }
    if ("name".equals(name))// NON-NLS
    {
      return true;
    }
    if ("dependencyLevel".equals(name))// NON-NLS
    {
      return true;
    }
    if ("runtime".equals(name))// NON-NLS
    {
      return true;
    }
    if ("active".equals(name))// NON-NLS
    {
      return true;
    }
    if ("preserve".equals(name))// NON-NLS
    {
      return true;
    }

    return false;
  }

  /**
   * Writes the parameters for an expression or function.
   *
   * @param propertyName the name of the properties that should be written.
   * @param namespaceUri the namespace that should be used when writing elements.
   * @param writer       the xml writer.
   * @param beanUtility  the bean utility containing the expression bean.
   * @throws IOException   if an IO error occurs.
   * @throws BeanException if a bean error occured.
   */
  private static void writeExpressionParameter
      (final XmlWriter writer,
       final BeanUtility beanUtility,
       final String propertyName,
       final String namespaceUri)
      throws IOException, BeanException
  {
    // filter some of the standard properties. These are system-properties
    // and are set elsewhere

    final Object property = beanUtility.getProperty(propertyName);
    final Class propertyType = beanUtility.getPropertyType(propertyName);
    final String value = beanUtility.getPropertyAsString(propertyName);
    if (value != null && property != null)
    {
      final AttributeList attList = new AttributeList();
      attList.setAttribute(namespaceUri, "name", propertyName);
      if (BeanUtility.isSameType(propertyType, property.getClass()) == false)
      {
        attList.setAttribute(namespaceUri, "class", property.getClass().getName());
      }
      writer.writeTag(namespaceUri, "property", attList, XmlWriterSupport.OPEN);
      writer.writeTextNormalized(value, false);
      writer.writeCloseTag();
    }
  }

  public static void copyStaticResources(final WriteableDocumentBundle bundle,
                                         final BundleWriterState state,
                                         final Expression expression,
                                         final BeanUtility beanUtility,
                                         final ExpressionPropertyMetaData[] datas)
      throws BundleWriterException, BeanException
  {
    if (bundle == null)
    {
      throw new NullPointerException();
    }
    if (state == null)
    {
      throw new NullPointerException();
    }
    if (expression == null)
    {
      throw new NullPointerException();
    }
    if (beanUtility == null)
    {
      throw new NullPointerException();
    }
    if (datas == null)
    {
      throw new NullPointerException();
    }

    final AbstractReportDefinition report = state.getReport();
    final ResourceKey contentBase = report.getContentBase();
    if (contentBase == null)
    {
      // treat all resources as linked resources ..
      return;
    }
    final ResourceKey defSource = report.getDefinitionSource();
    if (defSource == null)
    {
      // treat all resources as linked resources ..
      return;
    }

    if (ObjectUtilities.equal(contentBase.getParent(), defSource.getParent()) == false)
    {
      // treat all resources as linked resources ..
      return;
    }

    final Object contentBasePathRaw = contentBase.getIdentifier();
    if (contentBasePathRaw instanceof String == false)
    {
      return;
    }

    final String contentBasePath = String.valueOf(contentBasePathRaw);
    final ResourceManager resourceManager = state.getMasterReport().getResourceManager();

    for (int i = 0; i < datas.length; i++)
    {
      final ExpressionPropertyMetaData attributeMetaData = datas[i];
      final Object attValue = beanUtility.getProperty(attributeMetaData.getName());
      if (attValue == null)
      {
        continue;
      }
      final ResourceReference[] referencedResources = attributeMetaData.getReferencedResources
          (expression, attValue, report, resourceManager);
      for (int j = 0; j < referencedResources.length; j++)
      {
        final ResourceReference reference = referencedResources[j];
        if (reference.isLinked())
        {
          continue;
        }

        final ResourceKey path = reference.getPath();
        final Object identifier = path.getIdentifier();
        if (identifier instanceof String == false)
        {
          continue;
        }

        final String identifierString = String.valueOf(identifier);
        final String relativePath = IOUtils.getInstance().createRelativePath(identifierString, contentBasePath);
        try
        {
          BundleUtilities.copyInto(bundle, relativePath, path, resourceManager);
        }
        catch (Exception e)
        {
          throw new BundleWriterException("Failed to copy content from key " + path, e);
        }
      }
    }
  }
}
