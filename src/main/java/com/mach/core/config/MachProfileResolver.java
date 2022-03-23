package com.mach.core.config;

import io.appium.java_client.remote.MobilePlatform;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfilesResolver;

@Configuration
public class MachProfileResolver implements ActiveProfilesResolver {

	private static final String TEST_PLATFORM = "TEST_PLATFORM";

	@Override
	public String[] resolve(final Class<?> aClass) {
		return new String[] { getPlatform() };
	}

	public static String getActiveProfile() {
		return getPlatform();
	}

	private static String getPlatform() {
		String platform = MachProperties.getInstance().getString(TEST_PLATFORM);
		switch (platform.toLowerCase()) {
			case "android":
				return MobilePlatform.ANDROID;

			case "ios":
			default:
				return MobilePlatform.IOS;
		}
	}

}
