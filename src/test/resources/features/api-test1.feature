@api @ukpoliceapi
Feature: Police API to return an police stops by a given police force per month.

  Scenario: For a given police force "cleveland" and for June 2015, get the police stops
    Given I use the police Api "/stops-no-location"
    When I search for stops by force "cleveland" in "2015-06"
    Then I should get "120" results
    And age range of result "2" should be "25-34"