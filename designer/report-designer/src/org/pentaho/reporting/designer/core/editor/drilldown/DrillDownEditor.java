package org.pentaho.reporting.designer.core.editor.drilldown;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class DrillDownEditor extends JPanel
{
  public static final String DRILL_DOWN_UI_PROFILE_PROPERTY = "drillDownUiProfile";

  private class DrillDownFormulaUpdateListener implements PropertyChangeListener
  {
    private String lastFormulaSeen;

    private DrillDownFormulaUpdateListener()
    {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    public void propertyChange(final PropertyChangeEvent evt)
    {
      final String oldFormula = lastFormulaSeen;
      lastFormulaSeen = model.getDrillDownFormula();
      if (ObjectUtilities.equal(oldFormula, lastFormulaSeen) == false)
      {
        firePropertyChange("drillDownFormula", oldFormula, lastFormulaSeen);
      }
    }
  }

  private JPanel editorContainer;
  private DrillDownUi drillDownEditor;
  private final DrillDownModel model;
  private ReportDesignerContext designerContext;
  private DrillDownUiProfile selectedProfile;
  private boolean editFormulaFragment;

  public DrillDownEditor()
  {
    // this.drillDownSelector = drillDownSelector;
    editorContainer = new JPanel();
    editorContainer.setLayout(new BorderLayout());

    final JPanel cardHolder = new JPanel();
    cardHolder.setLayout(new CardLayout());
    cardHolder.add("2", editorContainer);
    cardHolder.add("1", Box.createRigidArea(new Dimension(450, 500)));

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    add(cardHolder, BorderLayout.CENTER);

    // initialize it to a sane default ..
    this.model = new DrillDownModel();
    this.model.addPropertyChangeListener(new DrillDownFormulaUpdateListener());
  }

  public boolean isEditFormulaFragment()
  {
    return editFormulaFragment;
  }

  public void setEditFormulaFragment(final boolean editFormulaFragment)
  {
    this.editFormulaFragment = editFormulaFragment;
  }

  public boolean isLimitedEditor()
  {
    return model.isLimitedEditor();
  }

  public void setLimitedEditor(final boolean limitedEditor)
  {
    model.setLimitedEditor(limitedEditor);
  }

  public DrillDownUiProfile getDrillDownUiProfile()
  {
    return this.selectedProfile;
  }

  public void setDrillDownUiProfile(final DrillDownUiProfile uiProfile)
  {
    final DrillDownUiProfile oldProfile = this.selectedProfile;
    this.selectedProfile = uiProfile;

    if (selectedProfile == oldProfile)
    {
      return;
    }

    if (drillDownEditor != null)
    {
      drillDownEditor.deactivate();
    }

    editorContainer.removeAll();

    if (uiProfile == null)
    {
      drillDownEditor = null;
      editorContainer.revalidate();
      return;
    }

    try
    {
      drillDownEditor = uiProfile.createUI();
      drillDownEditor.init(DrillDownEditor.this, designerContext, model);
      editorContainer.add(drillDownEditor.getEditorPanel());
      editorContainer.revalidate();
    }
    catch (DrillDownUiException ex)
    {
      UncaughtExceptionsModel.getInstance().addException(ex);
      drillDownEditor = null;
      editorContainer.revalidate();
    }

    firePropertyChange(DRILL_DOWN_UI_PROFILE_PROPERTY, oldProfile, selectedProfile);
  }

  public String getDrillDownFormula()
  {
    return model.getResultDrillDownFormula(isEditFormulaFragment());
  }

  public String getTargetFormula()
  {
    return model.getTargetFormula();
  }

  public String getTooltipFormula()
  {
    return model.getTooltipFormula();
  }

  public ReportDesignerContext getDesignerContext()
  {
    return designerContext;
  }

  private void setDrillDownProfile(final String drillDownProfileName)
  {
    final DrillDownUiProfile[] profiles = DrillDownUiProfileRegistry.getInstance().getProfiles();
    for (int i = 0; i < profiles.length; i++)
    {
      final DrillDownUiProfile profile = profiles[i];
      if (profile.canHandle(drillDownProfileName))
      {
        DebugLog.log("Profile " + drillDownProfileName + " can be handled by " + profile);//NON-NLS
        setDrillDownUiProfile(profile);
        return;
      }
    }

    DebugLog.log("Profile " + drillDownProfileName + " can NOT be handled by anyone.");//NON-NLS
    setDrillDownUiProfile(null);
  }

  public boolean initialize(final ReportDesignerContext designerContext,
                            final String linkFormula,
                            final String tooltipFormula,
                            final String targetFormula)
  {
    this.model.setTargetFormula(targetFormula);
    this.model.setTooltipFormula(tooltipFormula);
    if (designerContext != null)
    {
      this.designerContext = designerContext;
      if (this.model.initializeFromFormula(linkFormula, isEditFormulaFragment()) == false)
      {
        setDefaultProfile();
        DebugLog.log("Failed to init from formula, selecting generic URL");//NON-NLS
        return false;
      }
      else
      {
        setDrillDownProfile(this.model.getDrillDownConfig());
        DebugLog.log("Success on init from formula, selecting " + this.model.getDrillDownConfig());//NON-NLS
        return true;
      }
    }

    DebugLog.log("There is no designer context, clearing selections");//NON-NLS
    this.designerContext = null;
    this.model.clear();
    setDrillDownProfile(null);// NON-NLS
    return false;
  }

  public void setDefaultProfile()
  {
    final String defaultProfile = ReportDesignerBoot.getInstance().getGlobalConfig().getConfigProperty
        ("org.pentaho.reporting.designer.core.editor.drilldown.default-profile");
    final DrillDownUiProfile profile = DrillDownUiProfileRegistry.getInstance().getProfile(defaultProfile);
    setDrillDownUiProfile(profile);
  }
}
