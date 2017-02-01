package org.pentaho.reporting.libraries.designtime.swing.icons;

import javax.swing.*;

public class IconLoader {
  private static final IconLoader instance = new IconLoader();
  private ImageIcon addIcon;
  private ImageIcon deleteIcon;
  private ImageIcon forwardIcon;
  private ImageIcon moveDownIcon;
  private ImageIcon moveUpIcon;
  private ImageIcon removeIcon;

  public static IconLoader getInstance() {
    return instance;
  }

  private IconLoader() {
    forwardIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/libraries/designtime/swing/icons/e_forward.gif" ) ); // NON-NLS
    moveUpIcon = new ImageIcon( IconLoader.class.getResource
      ( "/org/pentaho/reporting/libraries/designtime/swing/icons/move_up.gif" ) ); // NON-NLS
    moveDownIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/libraries/designtime/swing/icons/move_down.gif" ) ); // NON-NLS
    deleteIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/libraries/designtime/swing/icons/delete.png" ) ); // NON-NLS
    addIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/libraries/designtime/swing/icons/Add.png" ) ); // NON-NLS
    removeIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/libraries/designtime/swing/icons/Remove.png" ) ); // NON-NLS
  }

  public ImageIcon getAddIcon() {
    return addIcon;
  }

  public ImageIcon getDeleteIcon() {
    return deleteIcon;
  }

  public ImageIcon getForwardIcon() {
    return forwardIcon;
  }

  public ImageIcon getMoveDownIcon() {
    return moveDownIcon;
  }

  public ImageIcon getMoveUpIcon() {
    return moveUpIcon;
  }

  public ImageIcon getRemoveIcon() {
    return removeIcon;
  }
}
