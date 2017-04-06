package stepdefs;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.restassured.path.json.JsonPath.with;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by aefoote on 03/04/2017.
 */
public class policeApiStepDefinitions {

    private static final Logger LOGGER = LoggerFactory.getLogger(policeApiStepDefinitions.class);

    /*
     In this example class, we set up a RequestSpecification to use throughout the Given/When/Then statements.
     The Given statements should add attributes to it.
     The When statements should run it (do the get/post requests) into the Response resp object.
     And the Then statement run assertions, mostly using resp.path but there are some other examples below.
      */
    private RequestSpecification spec = RestAssured.with();
    private Response resp;

    /**
     * A "Given" statement where we set up the spec object with the given attributes.
     * @param api
     */
    @Given("^I use the police Api \"([^\"]*)\"$")
    public void i_use_the_police_api(String api)
    {
        spec.given();

        if(System.getProperty("proxy.url") != null && !System.getProperty("proxy.url").isEmpty()) {
            spec.proxy(System.getProperty("proxy.url"), Integer.parseInt(System.getProperty("proxy.port")));
        }
        spec.baseUri(System.getProperty("api.base.url") + api);

//            spec.given().
//                //proxy(System.getProperty("proxy.url"), Integer.parseInt(System.getProperty("proxy.port"))).
//                baseUri(System.getProperty("api.base.url") + api);
    }

    /**
     * The "When" statement for preparing and running the get or post request. This
     * @param location
     * @param yearMonth
     */
    @When("^I search for stops by force \"([^\"]*)\" in \"([^\"]*)\"$")
    public void i_search_for_stops_at_location(String location, String yearMonth)
    {
        resp = spec.when().
                param("force", location).
                param("date", yearMonth).
                get();
    }


    /**
     * Assert that number of results returned is as expected
     * @param numRes
     */
    /*
    NB. This is intended as an example with returning data in multiple ways. This is not realistically how a method should be!
     */
    @Then("^I should get \"([^\"]*)\" results$")
    public void i_should_get_given_number_results(Integer numRes)
    {
        /*
        Put the response into a String if we want to use that
         */
        String jsonResponseString = resp.asString();
        LOGGER.info("*** INFO: Example to show the response as a string: " + jsonResponseString);

        /*
        We can also interact with the response using r.path
         */
        LOGGER.info("*** INFO: Example of returning first result using r.path: " + resp.path("[0].")); // prints the first result in the json
        String ageRangeValue = resp.path("[1].age_range"); // Gets the value of age_range for the 2nd result into a String
        LOGGER.info("*** INFO: Example of getting a value one layer down from the top level: " + ageRangeValue);


        /*
        If we're doing the former, then using with and get we can put items from the json string into lists/strings/ints etc and assert of that
         */
        List rootItemsList = with(jsonResponseString).get(".");
        LOGGER.info("*** INFO: Example of getting number of items in list: " + rootItemsList.size());
        assertThat(rootItemsList.size(), equalTo(numRes));

        /*
        Or if we're using the r.path response then we can do the same as above like this:
        This could be the entire content of the @When if we were to use this in a real example
         */
        List rootItemsListPath = resp.path(".");
        LOGGER.info("*** INFO: Example of gettting number of items in path list: " + rootItemsListPath.size());
        assertThat(rootItemsListPath.size(), equalTo(numRes));


        /*
        And to loop through each result and get the age_range values:
         */
        List rootItemsListPathToLoop = resp.path(".");
        int i;
        for(i = 0; i <rootItemsListPathToLoop.size(); i++){
            //LOGGER.info("*** INFO: Looping through each top level result in the json response: " + r.path("[" + i + "].age_range"));
        }

        /*
        Or we can do the whole thing with a separate json parser if we really want to...
         */
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(resp.asString());
            JSONArray jsonArray = (JSONArray) obj;
            int length = jsonArray.size();
            LOGGER.info("*** INFO: Number of results: " + length);
            assertThat(length, equalTo(numRes));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Assert that the age range of a given response is as expected
     * @param resultItem
     * @param ageRange
     */
    @Then("^age range of result \"([^\"]*)\" should be \"([^\"]*)\"$")
    public void age_range_of_given_result(Integer resultItem, String ageRange)
    {
        int i = resultItem - 1;
        LOGGER.info("*** INFO: age range is: " + resp.path("[" + i + "].age_range"));
        String nthResult = resp.path("[" + i + "].age_range").toString();
        assertThat(ageRange, equalTo(nthResult));
    }
}
