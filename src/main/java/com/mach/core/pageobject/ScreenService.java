package com.mach.core.pageobject;

import java.util.List;

public interface ScreenService {

	boolean isAnyTextPresent(String... texts);
	boolean isAnyTextContainedPresent(String... texts);
	boolean isAnyTextContainedPresentSpecial(String... texts);
	Integer parseInt(String originalString);
	List<String> getAllVisibleTexts();

}
