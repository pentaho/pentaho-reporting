package org.pentaho.reporting.ui.datasources.kettle;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.io.UnsupportedEncodingException;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.EmbeddedKettleDataFactoryMetaData;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.EmbeddedKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class KettleEmbeddedQueryEntry extends KettleQueryEntry {
  
  private String pluginId;
  private byte[] raw = null;
  private EmbeddedHelper helper = null;
  
  private static final Log logger = LogFactory.getLog(KettleEmbeddedQueryEntry.class);


  public KettleEmbeddedQueryEntry(String aName, String pluginId, byte[] raw) {
    super(aName);
    this.pluginId = pluginId;
    this.raw = raw;
    this.helper = new EmbeddedHelper(pluginId, this);
  }

  @Override
  public boolean validate() {
    update();
    return helper.validate();
  }

  @Override
  public String getSelectedStep() 
  {
    return EmbeddedKettleDataFactoryMetaData.DATA_RETRIEVAL_STEP;
  }

  @Override
  public KettleTransformationProducer createProducer()
  {
    
    update();

    final String[] argumentFields = getArguments();
    final ParameterMapping[] varNames = getParameters();

    return new EmbeddedKettleTransformationProducer(argumentFields, varNames, pluginId, getSelectedStep(),raw);
  }

  public void refreshQueryUIComponents(JPanel datasourcePanel, DesignTimeContext designTimeContext, PropertyChangeListener l) 
    throws ReportDataFactoryException
  {
    datasourcePanel.removeAll();
    datasourcePanel.add(helper.getDialogPanel(createProducer(), designTimeContext, l), BorderLayout.CENTER);
    datasourcePanel.revalidate();
  }
  
  public String[] getDeclaredParameters(final ResourceManager resourceManager,
      final ResourceKey contextKey) throws KettleException, ReportDataFactoryException{
    
    return helper.getCachedDeclaredParameters();
    
  }


  public void update()
  {
    try 
    {
      byte[] potential = helper.update();
      if (potential != null)
      {
        raw = potential;
      }
    } catch (Exception e) 
    {
      logger.warn("Warning: Not able to update query entry. Results may be unpredictable.", e);
    }
  }

  public void clear() 
  {
    helper.clear();
  }
  
}
