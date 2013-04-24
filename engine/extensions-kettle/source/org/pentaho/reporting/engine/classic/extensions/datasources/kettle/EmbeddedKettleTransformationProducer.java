package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleMissingPluginsException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class EmbeddedKettleTransformationProducer extends AbstractKettleTransformationProducer {
  private static final long serialVersionUID = 1900310938438244134L;

  private static final Log logger = LogFactory.getLog(EmbeddedKettleTransformationProducer.class);

  private String pluginId;

  private byte[] rawBytes;

  public EmbeddedKettleTransformationProducer(final String[] definedArgumentNames,
      final ParameterMapping[] definedVariableNames, final String pluginId, final String stepName,
      final byte[] transformationRaw) {
    super("", stepName, null, null, definedArgumentNames, definedVariableNames);

    this.pluginId = pluginId;
    if (transformationRaw == null) {
      EmbeddedKettleDataFactoryMetaData md = (EmbeddedKettleDataFactoryMetaData) DataFactoryRegistry.getInstance()
          .getMetaData(pluginId);
      rawBytes = md.getBytes().clone();
    } else {
      rawBytes = transformationRaw.clone();
    }
  }

  public String getPluginId() {
    return pluginId;
  }

  public byte[] getTransformationRaw() {
    return rawBytes.clone();
  }
  
  public void setTransformationRaw(byte[] raw)
  {
    rawBytes = raw;
  }

  /**
   * Designtime support
   *
   * @return
   */
  public TransMeta getTransMeta() throws KettlePluginException, KettleMissingPluginsException, KettleXMLException {
    return loadTransformation(null);
  }

  protected TransMeta loadTransformation(final Repository repository, final ResourceManager resourceManager,
      final ResourceKey contextKey) throws ReportDataFactoryException, KettleException {
    return loadTransformation(contextKey);
  }

  public TransMeta loadTransformation(final ResourceKey contextKey) throws KettleMissingPluginsException,
      KettlePluginException, KettleXMLException {
    final Document document = DocumentHelper.loadDocumentFromBytes(getTransformationRaw());
    final Node node = XMLHandler.getSubNode(document, TransMeta.XML_TAG);
    final TransMeta meta = new TransMeta();
    meta.loadXML(node, null, true, null, null);
    final String filename = computeFullFilename(contextKey);
    if (filename != null) {
      logger.debug("Computed Transformation Location: " + filename);
      meta.setFilename(filename);
    }
    return meta;
  }

  protected String computeFullFilename(ResourceKey key) {
    while (key != null) {
      final Object identifier = key.getIdentifier();
      if (identifier instanceof File) {
        final File file = (File) identifier;
        return file.getAbsolutePath();
      }
      key = key.getParent();
    }
    return null;
  }

  public Object getQueryHash(final ResourceManager resourceManager, final ResourceKey resourceKey) {
    final ArrayList<Object> retval = internalGetQueryHash();
    retval.add(pluginId);
    try {
      final TransMeta meta = loadTransformation(null, resourceManager, resourceKey);
      retval.add(meta.hashCode());
    } catch (Exception e) {
      // if it fails, move along with original query hash ...
    }
    return retval;
  }

  @Override
  public String getTransformationFile() {
    return null;
  }

}
