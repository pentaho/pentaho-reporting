package org.pentaho.reporting.ui.datasources.kettle;

import java.beans.PropertyChangeListener;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleMissingPluginsException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.trans.step.BaseStepGenericXulDialog;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.DocumentHelper;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.EmbeddedKettleDataFactoryMetaData;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.EmbeddedKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.swing.tags.SwingDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class EmbeddedHelper
{
  private static final Log logger = LogFactory.getLog(EmbeddedHelper.class);
  private String id;
  private TransMeta cachedMeta;
  private String[] cachedDeclaredParameters;
  private String[] reportFields;
  private List<String> unmatchedVariables;
  List<String> mappedVariables;
  
  private StepMeta step;
  private BaseStepGenericXulDialog dialog;
  private KettleQueryEntry parent;
  
  private static final String AUTO_GENERATED_PARAMETER = "AUTO.GENERATED.PARAMETER";

  public EmbeddedHelper(String id, KettleQueryEntry kqe)
  {
    this.id = id;
    parent = kqe;
  }

  public JComponent getDialogPanel(KettleTransformationProducer query,
                                   final DesignTimeContext context,
                                   PropertyChangeListener l)
      throws ReportDataFactoryException
  {
    findConfigurationStep(query);

    dialog = (BaseStepGenericXulDialog) createDialog(step, context);

    if (l != null)
    {
      dialog.addPropertyChangeListener(l);
    }

    dialog.validate();
    
    // May need these for later, in order to automatically map variables on behalf of the user
    reportFields = context.getDataSchemaModel().getColumnNames();

    XulComponent root = dialog.getXulDomContainer().getDocumentRoot().getElementById("root");
    
    // Without the following two lines of code, message boxes and prompts will freeze the dialog... 
    // There must be a better way.
    
    JComponent panel = ((SwingDialog)root.getParent()).getContainer();
    dialog.setModalParent(panel);

    return panel;
  }

  private StepDialogInterface createDialog(final StepMeta step, final DesignTimeContext context)
      throws ReportDataFactoryException
  {
    // Render datasource specific dialog for editing step details...
    try
    {
      final String dlgClassName = step.getStepMetaInterface().getDialogClassName().replace("Dialog", "XulDialog");

      final Class<StepDialogInterface> dialog =
          (Class<StepDialogInterface>) Class.forName(dlgClassName, true, step.getStepMetaInterface().getClass().getClassLoader());

      final Constructor<StepDialogInterface> constructor =
          dialog.getDeclaredConstructor(Object.class, BaseStepMeta.class, TransMeta.class, String.class);

      return constructor.newInstance(null, step.getStepMetaInterface(),
          step.getParentTransMeta(), EmbeddedKettleDataFactoryMetaData.DATA_CONFIGURATION_STEP);

    }
    catch (Exception e)
    {

      logger.error("Critical error attempting to dynamically create dialog. This datasource will not be available.", e);
      throw new ReportDataFactoryException("Error attempting to dynamically create dialog. Abort dialog rendering.");

    }
  }

  private StepMeta findConfigurationStep(KettleTransformationProducer query) throws ReportDataFactoryException
  {

    try
    {
      TransMeta transMeta = (query == null) ? loadTemplate() : loadQuery(query);

      step = transMeta.findStep(EmbeddedKettleDataFactoryMetaData.DATA_CONFIGURATION_STEP);
      step.setParentTransMeta(transMeta);

      cachedMeta = transMeta;
      cachedDeclaredParameters = transMeta.listParameters();

    }
    catch (Exception e)
    {

      step = null;
      cachedMeta = null;
      cachedDeclaredParameters = null;

      logger.error("Unable to find configuration step. Attempt to fall back to standard Kettle datasource...", e);
      throw new ReportDataFactoryException("Unable to find configuration step. Abort dialog rendering.");

    }

    return step;
  }

  /**
   * If this method returns null, it means the helper has not initialized the dialog or other member variables yet. .
   *
   * @return updated raw bytes of transformation to use.
   * @throws UnsupportedEncodingException
   * @throws KettleException
   */
  public byte[] update() throws UnsupportedEncodingException, KettleException  {

    if (dialog == null)
    {
      return null;
    }

    dialog.onAccept();
    
    dynamicallyMapVariables();

    ParameterMapping[] params = new ParameterMapping[mappedVariables.size()];

    for (int i = 0; i < mappedVariables.size(); i++) {
      params[i] = new ParameterMapping(mappedVariables.get(i),mappedVariables.get(i)); 
    }
    
    parent.addParameters(params);
      
    final byte[] rawData = cachedMeta.getXML().getBytes("UTF8");
    return rawData;

  }

  public boolean validate()
  {
    if (dialog != null)
    {
      dynamicallyMapVariables();
      if(!Const.isEmpty(unmatchedVariables)){
        
        String vars = "";
        for (String variable: unmatchedVariables) {
          vars = vars.concat("\n\r").concat(variable);
        }
        int val = dialog.showPromptMessage("Match (es) cannot be found for the following parameter(s). \n\r"
            + "This can cause query failure. \n\r"
            + vars + "\n\r \n\r"
            + "Select OK to continue, or Cancel to return and correct or remove the parameter.", "MongoDb Datasource Warning");
        return (val == 0);
        
      }
      
      if (!dialog.validate())
      {
        int val = dialog.showPromptMessage("One or more queries are missing required information. \n\r"
            + "This can cause these queries to fail when executing. \n\r"
            + "Select OK to continue, or Cancel to return and correct the query.", "MongoDb Datasource Warning");
        return (val == 0);
      }
    }
    return true;
  }
  
  private void dynamicallyMapVariables(){
    
    mappedVariables = new ArrayList<String>();
    unmatchedVariables = new ArrayList<String>();

    for (String parameter : cachedMeta.listParameters()) {
      
      String desc;
      
      try{
        desc = cachedMeta.getParameterDescription(parameter);
      
      }catch(Exception e){
        desc = "";
      }

      // Only manage managed parameters... those we auto-add...
      if (desc.equalsIgnoreCase(AUTO_GENERATED_PARAMETER)){
      
        // Is the parameter already part of a mapping?
        boolean found = false;
        for (int i = 0; i < parent.getParameters().length; i++)
        {
          final ParameterMapping mapping = parent.getParameters()[i];
          if (parameter.equalsIgnoreCase(mapping.getAlias())){
            found = true;
            break;
          }
        }

        // If not, do we have a matching report field to map to? 
        if(!found){
          for (String field : reportFields){
            if (parameter.equals(field)){
              mappedVariables.add(parameter);
              break;
            }
          }
        }

        if ((!found) && (!mappedVariables.contains(parameter))){
            unmatchedVariables.add(parameter);
        }
        
      }
    }
  }

  public void clear()
  {
    if (dialog != null)
    {
      dialog.clear();
    }
  }

  private TransMeta loadTemplate() throws KettlePluginException, KettleMissingPluginsException, KettleXMLException
  {
    final Document document = DocumentHelper.loadDocumentFromPlugin(id);
    final Node node = XMLHandler.getSubNode(document, TransMeta.XML_TAG);
    final TransMeta meta = new TransMeta();
    meta.loadXML(node, null, true, null, null);
    return meta;
  }

  private TransMeta loadQuery(KettleTransformationProducer query) throws KettlePluginException, KettleMissingPluginsException, KettleXMLException
  {
    final Document document = DocumentHelper.loadDocumentFromBytes(((EmbeddedKettleTransformationProducer) query).getTransformationRaw());
    final Node node = XMLHandler.getSubNode(document, TransMeta.XML_TAG);
    final TransMeta meta = new TransMeta();
    meta.loadXML(node, null, true, null, null);
    return meta;
  }

  public String[] getCachedDeclaredParameters() {
    return cachedDeclaredParameters;
  }

  public void setCachedDeclaredParameters(String[] cachedDeclaredParameters) {
    this.cachedDeclaredParameters = cachedDeclaredParameters;
  }

}
