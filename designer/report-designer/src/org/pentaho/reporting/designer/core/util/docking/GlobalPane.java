package org.pentaho.reporting.designer.core.util.docking;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * The GlobalPane is a component that offers side-windows in addition to a desktop or content area. The pane offers
 * four area to add content. Content is provided via Category-objects, which carry the actual JComponent as well
 * as some metadata (icons, text and the minimized-state).
 *
 * @author Thomas Morgner.
 */
public class GlobalPane extends JComponent
{
  public static enum Alignment
  {
    TOP(JSplitPane.HORIZONTAL_SPLIT),
    BOTTOM(JSplitPane.HORIZONTAL_SPLIT),
    LEFT(JSplitPane.VERTICAL_SPLIT),
    RIGHT(JSplitPane.VERTICAL_SPLIT);

    private int direction;

    private Alignment(final int direction)
    {
      this.direction = direction;
    }

    public int getDirection()
    {
      return direction;
    }
  }

  private SidePanel topPanel;
  private SidePanel leftPanel;
  private SidePanel bottomPanel;
  private SidePanel rightPanel;
  private JPanel contentPane;

  public GlobalPane(final boolean buttonsVisible)
  {
    leftPanel = new SidePanel(Alignment.LEFT);
    leftPanel.setButtonsVisible(buttonsVisible);
    rightPanel = new SidePanel(Alignment.RIGHT);
    rightPanel.setButtonsVisible(buttonsVisible);
    topPanel = new SidePanel(Alignment.TOP);
    topPanel.setButtonsVisible(buttonsVisible);
    bottomPanel = new SidePanel(Alignment.BOTTOM);
    bottomPanel.setButtonsVisible(buttonsVisible);

    contentPane = new JPanel(new BorderLayout());
    setLayout(new BorderLayout());
    add(leftPanel, BorderLayout.WEST);
    add(rightPanel, BorderLayout.EAST);
    add(topPanel, BorderLayout.NORTH);
    add(bottomPanel, BorderLayout.SOUTH);
    add(contentPane, BorderLayout.CENTER);
  }

  public void setMainComponent(final Component component)
  {
    contentPane.removeAll();
    if (component != null)
    {
      contentPane.add(component);
    }
  }

  public Component getMainComponent()
  {
    if (contentPane.getComponentCount() == 0)
    {
      return null;
    }
    return contentPane.getComponent(0);
  }

  public void add(final Alignment position, final Category category)
  {
    if (position == null)
    {
      throw new NullPointerException();
    }
    switch (position)
    {
      case TOP:
        topPanel.add(category);
        break;
      case LEFT:
        leftPanel.add(category);
        break;
      case BOTTOM:
        bottomPanel.add(category);
        break;
      case RIGHT:
        rightPanel.add(category);
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  public boolean isButtonsVisible(final Alignment position)
  {
    if (position == null)
    {
      throw new NullPointerException();
    }
    switch (position)
    {
      case TOP:
        return topPanel.isButtonsVisible();
      case LEFT:
        return leftPanel.isButtonsVisible();
      case BOTTOM:
        return bottomPanel.isButtonsVisible();
      case RIGHT:
        return rightPanel.isButtonsVisible();
      default:
        throw new IllegalArgumentException();
    }
  }

  public void setButtonsVisible(final Alignment position, final boolean visible)
  {
    if (position == null)
    {
      throw new NullPointerException();
    }
    switch (position)
    {
      case TOP:
        topPanel.setButtonsVisible(visible);
        break;
      case LEFT:
        leftPanel.setButtonsVisible(visible);
        break;
      case BOTTOM:
        bottomPanel.setButtonsVisible(visible);
        break;
      case RIGHT:
        rightPanel.setButtonsVisible(visible);
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  public void setPreferredContentSize(final Alignment position, final Integer value)
  {
    if (position == null)
    {
      throw new NullPointerException();
    }
    switch (position)
    {
      case TOP:
        topPanel.setPreferredContentSize(value);
        break;
      case LEFT:
        leftPanel.setPreferredContentSize(value);
        break;
      case BOTTOM:
        bottomPanel.setPreferredContentSize(value);
        break;
      case RIGHT:
        rightPanel.setPreferredContentSize(value);
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  public Integer getPreferredContentSize(final Alignment position)
  {
    if (position == null)
    {
      throw new NullPointerException();
    }
    switch (position)
    {
      case TOP:
        return topPanel.getPreferredContentSize();
      case LEFT:
        return leftPanel.getPreferredContentSize();
      case BOTTOM:
        return bottomPanel.getPreferredContentSize();
      case RIGHT:
        return rightPanel.getPreferredContentSize();
      default:
        throw new IllegalArgumentException();
    }
  }
}
