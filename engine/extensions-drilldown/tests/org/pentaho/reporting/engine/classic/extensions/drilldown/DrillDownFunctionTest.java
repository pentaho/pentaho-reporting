package org.pentaho.reporting.engine.classic.extensions.drilldown;

/**
 *
 * @author Thomas Morgner.
 */
public class DrillDownFunctionTest extends FormulaTestBase
{
  public DrillDownFunctionTest()
  {
  }

  public DrillDownFunctionTest(final String s)
  {
    super(s);
  }

  protected Object[][] createDataTest()
  {
    return new Object[][]{
        {"DRILLDOWN(\"local-prpt\"; 0; {\"test\" ; \"value\" | \"mtest\" ; {\"v1\"; \"v2\"; \"v3\" }})",
            "http://localhost:8080/pentaho/content/reporting/reportviewer/report.html?test=value&mtest=v1&mtest=v2&mtest=v3"},
        {"DRILLDOWN(\"remote-prpt\"; \"ssh://domain.example\"; {\"test\" ; \"value\" | \"mtest\" ; {\"v1\"; \"v2\"; \"v3\" }})",
            "ssh://domain.example/content/reporting/reportviewer/report.html?test=value&mtest=v1&mtest=v2&mtest=v3"},
        {"DRILLDOWN(\"generic-url\"; \"yieks://domain.example/test.php\"; {\"test\" ; \"value\" | \"mtest\" ; {\"v1\"; \"v2\"; \"v3\" }})",
            "yieks://domain.example/test.php?test=value&mtest=v1&mtest=v2&mtest=v3"},
        {"DRILLDOWN(\"generic-url\"; \"http://localhost:8080/pentaho\"; {\"line\"; NA() | \"HideBarSection\"; NA() | " +
            "\"output-target\"; NA() | \"renderMode\"; NA() | \"subscription-name\"; NA() | \"destination\"; NA() | " +
            "\"schedule-id\"; NA() | \"subscribe\"; NA() | \"solution\"; \"steel-wheels\" | \"yield-rate\"; NA() | " +
            "\"accepted-page\"; NA() | \"path\"; \"reports\" | \"name\"; \"Inventory.prpt\" | \"action\"; NA() | " +
            "\"output-type\"; NA() | \"layout\"; NA() | \"content-handler-pattern\"; NA() | \"autoSubmit\"; NA() | " +
            "\"autoSubmitUI\"; NA() | \"dashboard-mode\"; NA() | \"showParameters\"; NA() | \"paginate\"; NA() | " +
            "\"ignoreDefaultDates\"; NA() | \"print\"; NA() | \"printer-name\"; NA()})",
            "http://localhost:8080/pentaho?line=&HideBarSection=&output-target=&renderMode=&subscription-name=&" +
                "destination=&schedule-id=&subscribe=&solution=steel-wheels&yield-rate=&accepted-page=&path=reports&" +
                "name=Inventory.prpt&action=&output-type=&layout=&content-handler-pattern=&autoSubmit=&autoSubmitUI=&" +
                "dashboard-mode=&showParameters=&paginate=&ignoreDefaultDates=&print=&printer-name="},
        {"DRILLDOWN(\"local-xaction\"; NA(); {\"name\"; \"test.xaction\" | \"solution\"; \"steelwheels\" | \"path\"; \"analyzer\"})",
          "http://localhost:8080/pentaho/ViewAction?action=test.xaction&solution=steelwheels&path=analyzer"},
        {"DRILLDOWN(\"remote-xaction\"; \"ssh://domain.example\"; {\"name\"; \"test.xaction\" | \"solution\"; \"steelwheels\" | \"path\"; \"analyzer\"})",
          "ssh://domain.example/ViewAction?action=test.xaction&solution=steelwheels&path=analyzer"},
    };
  }

  public void testDefault() throws Exception
  {
    runDefaultTest();
  }

}
