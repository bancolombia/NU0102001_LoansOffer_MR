package co.com.bancolombia.acceptancetest;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.intuit.karate.core.ScenarioResult;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ParallelTest {
    @Test
    public void testParallel(){
        Results results = Runner.path("classpath:co/com/bancolombia/acceptancetest/features").outputCucumberJson(true).parallel(4);
        retryFailedTests(results);
        generateReport(results.getReportDir());
    }
    private static void generateReport(String karateOutputPath){
        Collection<File> jsonFiles = FileUtils.listFiles(new File(karateOutputPath),new String[]{"json"},true);
        ArrayList<String> jsonPaths = new ArrayList<>(jsonFiles.size());
        jsonFiles.forEach(file -> jsonPaths.add(file.getAbsolutePath()));
        Configuration config = new Configuration(new File("target"), "AcceptanceTest");
        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }
    Results retryFailedTests(Results results) {
        System.out.println("======== Retrying failed tests ========");
        Results initialResults = results;
        List<ScenarioResult> retryResult = results.getScenarioResults().filter(ScenarioResult::isFailed)
                .parallel()
                .map(scenarioResult -> initialResults.getSuite().retryScenario(scenarioResult.getScenario()))
                .collect(Collectors.toList());
        for (ScenarioResult scenarioResult : retryResult) {
            results = results.getSuite().updateResults(scenarioResult);
        }
        return results;
    }
}