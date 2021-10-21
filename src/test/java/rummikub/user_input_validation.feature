@tag
Feature: User Input Validation
  I want to use this feature to test validity of user input

  @notInHand
  Scenario: Test Playing Tiles not in Hand
    Given Test Server is started
    And Player 1 hand starts with "R1 B1 G1 O1"
    And Player 1 has played initial points
    When Player 1 plays "O8 O9 O10 O11"
    Then table contains ""
    And Player 1 hand contains "R1 B1 G1 O1"

  @notOnTable
  Scenario: Test Reusing Tiles not on Table
    Given Test Server is started
    And Player 1 hand starts with "R1 B1 G1 O1"
    And Player 1 has played initial points
    When Player 1 plays "O1 1:O2 1:O3"
    Then table contains ""
    And Player 1 hand contains "R1 B1 G1 O1"
