import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Created by aefoote on 03/04/2017.
 */

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(plugin = {"pretty:output", "html:target/cucumber"}, features = "src/test/resources/features", tags = "@api")
public class TestRunner {
}
