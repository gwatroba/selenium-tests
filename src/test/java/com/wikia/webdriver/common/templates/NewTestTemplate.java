package com.wikia.webdriver.common.templates;

import static com.wikia.webdriver.common.core.configuration.Configuration.DEFAULT_LANGUAGE;

import com.wikia.webdriver.common.contentpatterns.URLsContent;
import com.wikia.webdriver.common.core.url.FandomUrlBuilder;
import com.wikia.webdriver.common.core.url.UrlBuilder;
import com.wikia.webdriver.common.templates.core.CoreTestTemplate;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

public class NewTestTemplate extends CoreTestTemplate {

  protected UrlBuilder urlBuilder;
  protected FandomUrlBuilder fandomUrlBuilder;
  protected String wikiURL;
  protected String wikiCorporateURL;
  protected String wikiCorpSetupURL;

  @BeforeMethod(alwaysRun = true)
  public void start(Method method, Object[] data) {
  }

  protected void loadFirstPage() {
    driver.get(wikiURL + URLsContent.SPECIAL_VERSION);
  }

  protected void prepareURLs() {
    urlBuilder = UrlBuilder.createUrlBuilder();
    fandomUrlBuilder = new FandomUrlBuilder();
    wikiURL = urlBuilder.getUrl();
    wikiCorporateURL = urlBuilder.getWikiGlobalURL();
    wikiCorpSetupURL = UrlBuilder.createUrlBuilderForWikiAndLang("corp", DEFAULT_LANGUAGE).getUrl();
  }
}
