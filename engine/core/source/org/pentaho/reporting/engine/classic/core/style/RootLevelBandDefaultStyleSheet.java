package org.pentaho.reporting.engine.classic.core.style;

public class RootLevelBandDefaultStyleSheet extends BandDefaultStyleSheet
{
  /**
   * A shared default style-sheet.
   */
  private static RootLevelBandDefaultStyleSheet defaultStyle;

  /**
   * Creates a new default style sheet.
   */
  protected RootLevelBandDefaultStyleSheet()
  {
    setLocked(false);
    setStyleProperty(ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT, Boolean.FALSE);
    setStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, Boolean.TRUE);
    setLocked(true);
  }

  /**
   * Returns the default band style sheet.
   *
   * @return the style-sheet.
   */
  public static synchronized RootLevelBandDefaultStyleSheet getRootLevelBandDefaultStyle()
  {
    if (defaultStyle == null)
    {
      defaultStyle = new RootLevelBandDefaultStyleSheet();
    }
    return defaultStyle;
  }
}
