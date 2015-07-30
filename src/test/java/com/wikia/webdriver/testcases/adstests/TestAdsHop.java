package com.wikia.webdriver.testcases.adstests;

import com.wikia.webdriver.common.contentpatterns.AdsContent;
import com.wikia.webdriver.common.dataprovider.mobile.MobileAdsDataProvider;
import com.wikia.webdriver.common.templates.TemplateNoFirstLoad;
import com.wikia.webdriver.pageobjectsfactory.pageobject.adsbase.AdsHopObject;

import org.testng.annotations.Test;

/**
 * @author drets
 * @ownership AdEng
 */
public class TestAdsHop extends TemplateNoFirstLoad {

  @Test(
      dataProviderClass = MobileAdsDataProvider.class,
      dataProvider = "testAdsHopPostMessage",
      groups = "TestAdsHop"
  )
  public void testAdsHopPostMessage(String wikiName, String article, String src) {
    String testPage = urlBuilder.getUrlForPath(wikiName, article);
    AdsHopObject adsHopObject = new AdsHopObject(driver, testPage);
    adsHopObject.waitForPageLoaded();
    adsHopObject.verifyClassHidden(AdsContent.MOBILETOP_LB, src);
    adsHopObject.verifyPostMessage(AdsContent.MOBILETOP_LB, src);
    adsHopObject.verifyLineItemIdsDiffer(AdsContent.MOBILETOP_LB);
  }

}
