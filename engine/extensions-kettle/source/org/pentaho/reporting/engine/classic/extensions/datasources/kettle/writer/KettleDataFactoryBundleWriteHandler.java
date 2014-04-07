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

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PasswordEncryptionService;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleDataFactoryWriterHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.AbstractKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.EmbeddedKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaArgument;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaParameter;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromFileProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromRepositoryProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

/**
 * Creation-Date: Jan 19, 2007, 4:44:05 PM
 *
 * @author Thomas Morgner
 */
public class KettleDataFactoryBundleWriteHandler implements BundleDataFactoryWriterHandler
{
  public KettleDataFactoryBundleWriteHandler()
  {
  }

  /**
   * Writes a data-source into a own file. The name of file inside the bundle is returned
   * as string. The file name returned is always absolute and can be made relative by using the IOUtils of
   * LibBase. If the writer-handler did not generate a file on its own, it should return null.
   *
   * @param bundle      the bundle where to write to.
   * @param dataFactory the data factory that should be written.
   * @param state       the writer state to hold the current processing information.
   * @return the name of the newly generated file or null if no file was created.
   * @throws IOException           if any error occured
   * @throws BundleWriterException if a bundle-management error occured.
   */
  public String writeDataFactory(final WriteableDocumentBundle bundle,
                                 final DataFactory dataFactory,
                                 final BundleWriterState state)
      throws IOException, BundleWriterException
  {
    final String fileName = BundleUtilities.getUniqueName(bundle, state.getFileName(), "datasources/kettle-ds{0}.xml");
    if (fileName == null)
    {
      throw new IOException("Unable to generate unique name for Inline-Data-Source");
    }

    final OutputStream outputStream = bundle.createEntry(fileName, "text/xml");
    final DefaultTagDescription tagDescription = new DefaultTagDescription
        (ClassicEngineBoot.getInstance().getGlobalConfig(), KettleDataFactoryModule.TAG_DEF_PREFIX);
    final XmlWriter xmlWriter = new XmlWriter(new OutputStreamWriter(outputStream, "UTF-8"), tagDescription, "  ", "\n");

    final KettleDataFactory kettleDataFactory = (KettleDataFactory) dataFactory;

    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration("data", KettleDataFactoryModule.NAMESPACE);
    xmlWriter.writeTag(KettleDataFactoryModule.NAMESPACE, "kettle-datasource", rootAttrs, XmlWriter.OPEN);

    final String[] queryNames = kettleDataFactory.getQueryNames();
    for (int i = 0; i < queryNames.length; i++)
    {
      final String queryName = queryNames[i];
      final KettleTransformationProducer prod = kettleDataFactory.getQuery(queryName);
      if (prod instanceof KettleTransFromFileProducer)
      {
        writeKettleFileProducer(xmlWriter, queryName, (KettleTransFromFileProducer) prod);
      }
      else if (prod instanceof KettleTransFromRepositoryProducer)
      {
        writeKettleRepositoryProducer(xmlWriter, queryName, (KettleTransFromRepositoryProducer) prod);
      }
      else if (prod instanceof EmbeddedKettleTransformationProducer)
      {
        writeKettleEmbeddedProducer(bundle, fileName, xmlWriter, queryName, (EmbeddedKettleTransformationProducer) prod);
      }
      else
      {
        throw new BundleWriterException("Failed to write Kettle-Producer: Unknown implementation.");
      }
    }
    xmlWriter.writeCloseTag();
    xmlWriter.close();
    return fileName;
  }

  private void writeKettleFileProducer(final XmlWriter xmlWriter,
                                       final String queryName,
                                       final KettleTransFromFileProducer fileProducer)
      throws IOException
  {
    final AttributeList coreAttrs = new AttributeList();
    coreAttrs.setAttribute(KettleDataFactoryModule.NAMESPACE, "name", queryName);
    coreAttrs.setAttribute(KettleDataFactoryModule.NAMESPACE, "repository", fileProducer.getRepositoryName());
    coreAttrs.setAttribute(KettleDataFactoryModule.NAMESPACE, "filename", fileProducer.getTransformationFile());
    coreAttrs.setAttribute(KettleDataFactoryModule.NAMESPACE, "step", fileProducer.getStepName());
    coreAttrs.setAttribute(KettleDataFactoryModule.NAMESPACE, "username", fileProducer.getUsername());
    coreAttrs.setAttribute(KettleDataFactoryModule.NAMESPACE, "password",
        PasswordEncryptionService.getInstance().encrypt(fileProducer.getPassword()));

    final String[] definedArgumentNames = fileProducer.getDefinedArgumentNames();
    final ParameterMapping[] parameterMappings = fileProducer.getDefinedVariableNames();
    if (definedArgumentNames.length == 0 && parameterMappings.length == 0)
    {
      xmlWriter.writeTag(KettleDataFactoryModule.NAMESPACE, "query-file", coreAttrs, XmlWriter.CLOSE);
      return;
    }

    xmlWriter.writeTag(KettleDataFactoryModule.NAMESPACE, "query-file", coreAttrs, XmlWriter.OPEN);
    writeParameterAndArguments(xmlWriter, fileProducer);
    xmlWriter.writeCloseTag();
  }

  private void writeKettleRepositoryProducer(final XmlWriter xmlWriter,
                                             final String queryName,
                                             final KettleTransFromRepositoryProducer repositoryProducer)
      throws IOException
  {
    final AttributeList coreAttrs = new AttributeList();
    coreAttrs.setAttribute(KettleDataFactoryModule.NAMESPACE, "name", queryName);
    coreAttrs.setAttribute(KettleDataFactoryModule.NAMESPACE, "repository", repositoryProducer.getRepositoryName());
    coreAttrs.setAttribute(KettleDataFactoryModule.NAMESPACE, "directory", repositoryProducer.getDirectoryName());
    coreAttrs.setAttribute(KettleDataFactoryModule.NAMESPACE, "transformation", repositoryProducer.getTransformationName());
    coreAttrs.setAttribute(KettleDataFactoryModule.NAMESPACE, "step", repositoryProducer.getStepName());
    coreAttrs.setAttribute(KettleDataFactoryModule.NAMESPACE, "username", repositoryProducer.getUsername());
    coreAttrs.setAttribute(KettleDataFactoryModule.NAMESPACE, "password",
        PasswordEncryptionService.getInstance().encrypt(repositoryProducer.getPassword()));

    final String[] definedArgumentNames = repositoryProducer.getDefinedArgumentNames();
    final ParameterMapping[] parameterMappings = repositoryProducer.getDefinedVariableNames();
    if (definedArgumentNames.length == 0 && parameterMappings.length == 0)
    {
      xmlWriter.writeTag(KettleDataFactoryModule.NAMESPACE, "query-repository", coreAttrs, XmlWriter.CLOSE);
      return;
    }

    xmlWriter.writeTag(KettleDataFactoryModule.NAMESPACE, "query-repository", coreAttrs, XmlWriter.OPEN);
    writeParameterAndArguments(xmlWriter, repositoryProducer);
    xmlWriter.writeCloseTag();
  }

  private void writeKettleEmbeddedProducer(final WriteableDocumentBundle bundle,
                                           final String contextFileName,
                                           final XmlWriter xmlWriter,
                                           final String queryName,
                                           final EmbeddedKettleTransformationProducer fileProducer)
      throws IOException
  {
    String absoluteResourceName = writeFile(bundle, contextFileName, queryName, fileProducer);

    final AttributeList coreAttrs = new AttributeList();
    // the name is static for now
    coreAttrs.setAttribute(KettleDataFactoryModule.NAMESPACE, "name", queryName);
    coreAttrs.setAttribute(KettleDataFactoryModule.NAMESPACE, "plugin-id", fileProducer.getPluginId());

    xmlWriter.writeTag(KettleDataFactoryModule.NAMESPACE, "query-embedded", coreAttrs, XmlWriter.OPEN);

    // Now writes the name of the file that the KTR is stored in. 
    xmlWriter.writeTag(KettleDataFactoryModule.NAMESPACE, "resource", XmlWriter.OPEN);
    final String resourceName = IOUtils.getInstance().createRelativePath(absoluteResourceName, contextFileName);
    xmlWriter.writeText(resourceName);
    xmlWriter.writeCloseTag();

    writeParameterAndArguments(xmlWriter, fileProducer);
    xmlWriter.writeCloseTag();
  }

  private String writeFile(final WriteableDocumentBundle bundle,
                           final String contextFileName,
                           final String queryName,
                           final EmbeddedKettleTransformationProducer fileProducer)
      throws IOException
  {

    final String fileName = BundleUtilities.getUniqueName(bundle, contextFileName, queryName + "{0}.ktr");
    if (fileName == null)
    {
      throw new IOException("Unable to generate unique name for unified datasource. ");
    }

    OutputStream outputStream = null;
    try
    {
      outputStream = bundle.createEntry(fileName, "text/xml");
      outputStream.write(fileProducer.getTransformationRaw());
    }
    finally
    {
      if (outputStream != null)
      {
        outputStream.flush();
        outputStream.close();
      }
    }
    return fileName;
  }


  private void writeParameterAndArguments(final XmlWriter xmlWriter,
                                          final AbstractKettleTransformationProducer fileProducer) throws IOException
  {
    final FormulaArgument[] definedArgumentNames = fileProducer.getArguments();
    final FormulaParameter[] parameterMappings = fileProducer.getParameter();
    for (int i = 0; i < definedArgumentNames.length; i++)
    {
      final FormulaArgument arg = definedArgumentNames[i];
      xmlWriter.writeTag(KettleDataFactoryModule.NAMESPACE, "argument", "formula", arg.getFormula(), XmlWriter.CLOSE);
    }

    for (int i = 0; i < parameterMappings.length; i++)
    {
      final FormulaParameter parameterMapping = parameterMappings[i];
      final AttributeList paramAttr = new AttributeList();
      paramAttr.setAttribute(KettleDataFactoryModule.NAMESPACE, "variable-name", parameterMapping.getName());
      paramAttr.setAttribute(KettleDataFactoryModule.NAMESPACE, "formula", parameterMapping.getFormula());
      xmlWriter.writeTag(KettleDataFactoryModule.NAMESPACE, "variable", paramAttr, XmlWriter.CLOSE);
    }
  }

}
