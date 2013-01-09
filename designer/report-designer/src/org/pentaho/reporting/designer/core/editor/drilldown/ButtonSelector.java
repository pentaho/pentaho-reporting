package org.pentaho.reporting.designer.core.editor.drilldown;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.08.2010
 * Time: 14:06:42
 *
 * @author Thomas Morgner.
 */
public class ButtonSelector extends JPanel implements DrillDownSelector
{
  private class SelectorAction implements ActionListener
  {
    private DrillDownUiProfile profile;

    private SelectorAction(final DrillDownUiProfile profile)
    {
      this.profile = profile;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      setSelectedProfile(profile);
    }
  }

  private EventListenerList eventListeners;
  private DrillDownUiProfile selectedItem;
  private HashMap<DrillDownUiProfile, JRadioButton> profilesToButton;

  public ButtonSelector()
  {
    eventListeners = new EventListenerList();
    profilesToButton = new HashMap<DrillDownUiProfile, JRadioButton>();

    final ButtonGroup buttonGroup = new ButtonGroup();
    final DrillDownUiProfileRegistry metaData = DrillDownUiProfileRegistry.getInstance();
    final DrillDownUiProfile[] drilldownProfiles = metaData.getProfiles();
    Arrays.sort(drilldownProfiles, new DrillDownUiProfileComparator());
    for (int i = 0; i < drilldownProfiles.length; i++)
    {
      final DrillDownUiProfile profile = drilldownProfiles[i];
      final JRadioButton button = new JRadioButton(profile.getDisplayName());
      button.addActionListener(new SelectorAction(profile));
      add(button);
      buttonGroup.add(button);
      profilesToButton.put(profile, button);
    }

    final JRadioButton button = new JRadioButton("Manual Linking");
    button.addActionListener(new SelectorAction(null));
    add(button);
    buttonGroup.add(button);
    profilesToButton.put(null, button);

  }

  public DrillDownUiProfile getSelectedProfile()
  {
    return selectedItem;
  }

  public void setSelectedProfile(final DrillDownUiProfile profile)
  {
    selectedItem = profile;

    final JRadioButton button = profilesToButton.get(selectedItem);
    if (button != null)
    {
      button.setSelected(true);
    }
    fireChangeEvent();
  }

  public void addChangeListener(final ChangeListener changeListener)
  {
    if (changeListener == null)
    {
      throw new NullPointerException();
    }
    eventListeners.add(ChangeListener.class, changeListener);
  }

  public void removeChangeListener(final ChangeListener changeListener)
  {
    if (changeListener == null)
    {
      throw new NullPointerException();
    }
    eventListeners.remove(ChangeListener.class, changeListener);
  }

  private void fireChangeEvent()
  {
    final ChangeListener[] changeListeners = eventListeners.getListeners(ChangeListener.class);
    final ChangeEvent event = new ChangeEvent(this);
    for (int i = 0; i < changeListeners.length; i++)
    {
      final ChangeListener listener = changeListeners[i];
      listener.stateChanged(event);
    }
  }

  public JComponent getComponent()
  {
    return this;
  }
}
