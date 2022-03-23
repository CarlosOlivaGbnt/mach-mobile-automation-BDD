package com.mach.runners;

import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.TestNGCucumberRunner;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@CucumberOptions(
        tags = "@CrearCuentaMACH",
        features = "src/test/resources/features",
        glue = "com.mach.tests.definitions"
)

public class CreateAccountRunner {

    private TestNGCucumberRunner testNGCucumberRunner;

    @BeforeClass(alwaysRun = true)
    @Parameters({"user", "name", "skip-challenge", "address", "target-Challenge", "replacement-Challenge", "fail-challenge", "check-challenge"})
    private void setupData(@Optional("random-user-default") String userIdentifier, @Optional("") String name,
                           @Optional("") String skipChallenge, @Optional("default-create-account") String address,
                           @Optional("") String targetChallenge, @Optional("") String replacementChallenge,
                           @Optional("") String failChallenge, @Optional("") String checkChallenge) {
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
    }

    @Test(groups = "cucumber", description = "Run Cucumber Features.", dataProvider = "scenarios")
    public void scenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper) {
        testNGCucumberRunner.runScenario(pickleWrapper.getPickle());
    }

    @DataProvider
    public Object[][] scenarios() {
        return testNGCucumberRunner.provideScenarios();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        testNGCucumberRunner.finish();
    }

}
