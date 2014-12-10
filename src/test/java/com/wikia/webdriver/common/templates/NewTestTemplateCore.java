package com.wikia.webdriver.common.templates;

import java.io.File;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.wikia.webdriver.common.contentpatterns.URLsContent;
import com.wikia.webdriver.common.core.annotations.NetworkTrafficDump;
import com.wikia.webdriver.common.core.CommonUtils;
import com.wikia.webdriver.common.core.configuration.AbstractConfiguration;
import com.wikia.webdriver.common.core.configuration.ConfigurationFactory;
import com.wikia.webdriver.common.core.geoedge.GeoEdgeProxy;
import com.wikia.webdriver.common.core.geoedge.GeoEdgeUtils;
import com.wikia.webdriver.common.core.networktrafficinterceptor.NetworkTrafficInterceptor;
import com.wikia.webdriver.common.core.urlbuilder.UrlBuilder;
import com.wikia.webdriver.common.driverprovider.NewDriverProvider;
import com.wikia.webdriver.common.logging.PageObjectLogging;
import com.wikia.webdriver.common.properties.Properties;
import org.browsermob.proxy.ProxyServer;
import java.lang.reflect.Method;

@Listeners({ com.wikia.webdriver.common.logging.PageObjectLogging.class })
public class NewTestTemplateCore {

	protected WebDriver driver;
	protected UrlBuilder urlBuilder;
	protected AbstractConfiguration config;
	protected String wikiURL;
	protected String wikiCorporateURL;
	protected String wikiCorpSetupURL;
	private DesiredCapabilities capabilities;
	protected NetworkTrafficInterceptor networkTrafficIntereceptor;
	protected boolean isProxyServerRunning = false;

	public NewTestTemplateCore() {
		config = ConfigurationFactory.getConfig();
	}

	@BeforeSuite(alwaysRun = true)
	public void beforeSuite() {
		prepareDirectories();
	}

	protected void prepareDirectories() {
		Properties.setProperties();
		CommonUtils.deleteDirectory("." + File.separator + "logs");
		CommonUtils.createDirectory("." + File.separator + "logs");
	}

	private void printProperties() {
		System.out.println("Wiki url: " + wikiURL);
		System.out.println("Wiki corporate url: " + wikiCorporateURL);
	}

	protected void prepareURLs() {
		urlBuilder = new UrlBuilder(config.getEnv());
		wikiURL = urlBuilder.getUrlForWiki(config.getWikiName());
		wikiCorporateURL = urlBuilder.getUrlForWiki("wikia");
		wikiCorpSetupURL = urlBuilder.getUrlForWiki("corp");
		printProperties();
	}

	protected void startBrowser() {
		driver = registerDriverListener(
			NewDriverProvider.getDriverInstanceForBrowser (config.getBrowser())
		);
	}

	protected WebDriver startCustomBrowser(String browserName) {
		driver = registerDriverListener(
			NewDriverProvider.getDriverInstanceForBrowser(browserName)
		);
		return driver;
	}

	protected WebDriver registerDriverListener(EventFiringWebDriver driver) {
		driver.register(new PageObjectLogging());
		return driver;
	}

	protected void logOut() {
		driver.get(wikiURL + URLsContent.LOGOUT);
	}

	protected void logOutCustomDriver(WebDriver customDriver) {
		customDriver.get(wikiURL + URLsContent.LOGOUT);
	}

	protected void stopBrowser() {
		if (driver != null) {
			driver.quit();
		}
	}

	protected void stopCustomBrowser(WebDriver customDriver) {
		if (customDriver != null) {
			customDriver.quit();
		}
	}

	protected DesiredCapabilities getCapsWithProxyServerSet(ProxyServer server) {
		capabilities = new DesiredCapabilities();
		try {
			capabilities.setCapability(
				CapabilityType.PROXY, server.seleniumProxy()
			);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return capabilities;
	}

	protected void setDriverCapabilities(DesiredCapabilities caps) {
		NewDriverProvider.setDriverCapabilities(caps);
	}

	protected void setWindowSize(int width, int height, WebDriver desiredDriver) {
		Dimension dimension = new Dimension(width, height);
		desiredDriver.manage().window().setSize(dimension);
	}

	protected void setBrowserUserAgent(String userAgent) {
		NewDriverProvider.setBrowserUserAgent(config.getBrowser(), userAgent);
	}

	protected void runProxyServerIfNeeded(Method method) {
		boolean isGeoEdgeSet = false;
		boolean isNetworkTrafficDumpSet = false;
		String countryCode = null;

		if (method.getAnnotation(GeoEdgeProxy.class) != null) {
			isGeoEdgeSet = true;
			countryCode = method.getAnnotation(GeoEdgeProxy.class).country();
		}

		if (method.getAnnotation(NetworkTrafficDump.class) != null) {
			isNetworkTrafficDumpSet = true;
		}

		if (isGeoEdgeSet || isNetworkTrafficDumpSet) {
			isProxyServerRunning = true;
			networkTrafficIntereceptor = new NetworkTrafficInterceptor();
			networkTrafficIntereceptor.startSeleniumProxyServer();
		} else {
			return;
		}

		if (isGeoEdgeSet) {
			GeoEdgeUtils geoEdgeUtils = new GeoEdgeUtils(config.getCredentialsFilePath());
			String credentialsBase64 = "Basic " + geoEdgeUtils.createBaseFromCredentials();
			String IP = geoEdgeUtils.getIPForCountry(countryCode);
			networkTrafficIntereceptor.setProxyServer(IP);
			networkTrafficIntereceptor.changeHeader("Proxy-Authorization", credentialsBase64);
		}

		capabilities = getCapsWithProxyServerSet(networkTrafficIntereceptor);
		setDriverCapabilities(capabilities);
	}
}