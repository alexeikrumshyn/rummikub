@tag
Feature: Test Valid Joker Replacements
  I want to use this feature to test valid joker replacements

  @fromSetToSet
  Scenario Outline: Replace a Joker in a Set and Reuse in Another Set
    Given Test Server is started
    And Player 1 has played initial points
    And Player 2 has played initial points
    And Player 1 hand starts with "R2 B2 G2 *"
    And Player 2 hand starts with "R2 B2 G2 O2"
    When Player 1 plays <p1play>
    And Player 2 plays <p2play>
    Then table contains <table>
    And Player 2 hand contains <hand>
    Examples:
      | p1play                  | p2play                                  | table                                             | hand         |
      | "R2 G2 *"               | "1:R2 1:G2 O2,R2 B2 1:*"                | "{ !R2 !G2 *O2 }\n{ *R2 *B2 !* }\n"               | "G2 "        |
      | "R2 G2 *"               | "1:R2 1:G2 O2,R2 B2 G2 1:*"             | "{ !R2 !G2 *O2 }\n{ *R2 *B2 *G2 !* }\n"           | ""           |
      | "R2 B2 G2 *"            | "1:R2 1:B2 1:G2 O2,R2 B2 1:*"           | "{ !R2 !B2 !G2 *O2 }\n{ *R2 *B2 !* }\n"           | "G2 "        |
      | "R2 B2 G2 *"            | "1:R2 1:B2 1:G2 O2,R2 B2 G2 1:*"        | "{ !R2 !B2 !G2 *O2 }\n{ *R2 *B2 *G2 !* }\n"       | ""           |

  @fromSetToRun
  Scenario Outline: Replace a Joker in a Set and Reuse in Another Run
    Given Test Server is started
    And Player 1 has played initial points
    And Player 2 has played initial points
    And Player 1 hand starts with "R2 B2 G2 *"
    And Player 2 hand starts with "R2 B2 G2 O2 O7 O8 O10"
    When Player 1 plays <p1play>
    And Player 2 plays <p2play>
    Then table contains <table>
    And Player 2 hand contains <hand>
    Examples:
      | p1play                  | p2play                                  | table                                             | hand                   |
      | "R2 G2 *"               | "1:R2 1:G2 O2,O7 O8 1:*"                | "{ !R2 !G2 *O2 }\n{ *O7 *O8 !* }\n"               | "R2 B2 G2 O10 "        |
      | "R2 G2 *"               | "1:R2 1:G2 O2,O7 O8 1:* O10"            | "{ !R2 !G2 *O2 }\n{ *O7 *O8 !* *O10 }\n"          | "R2 B2 G2 "        |
      | "R2 B2 G2 *"            | "1:R2 1:B2 1:G2 O2,O7 O8 1:*"           | "{ !R2 !B2 !G2 *O2 }\n{ *O7 *O8 !* }\n"           | "R2 B2 G2 O10 "        |
      | "R2 B2 G2 *"            | "1:R2 1:B2 1:G2 O2,O7 O8 1:* O10"       | "{ !R2 !B2 !G2 *O2 }\n{ *O7 *O8 !* *O10 }\n"      | "R2 B2 G2 "            |

  @fromRunToSet
  Scenario Outline: Replace a Joker in a Run and Reuse in Another Set
    Given Test Server is started
    And Player 1 has played initial points
    And Player 2 has played initial points
    And Player 1 hand starts with "R2 R3 R4 *"
    And Player 2 hand starts with "R1 R3 B5 G5 O5"
    When Player 1 plays <p1play>
    And Player 2 plays <p2play>
    Then table contains <table>
    And Player 2 hand contains <hand>
    Examples:
      | p1play                  | p2play                                  | table                                       | hand            |
      | "R2 * R4"               | "1:R2 R3 1:R4,1:* G5 O5"                | "{ !R2 *R3 !R4 }\n{ !* *G5 *O5 }\n"         | "R1 B5 "        |
      | "R2 * R4"               | "1:R2 R3 1:R4,1:* B5 G5 O5"             | "{ !R2 *R3 !R4 }\n{ !* *B5 *G5 *O5 }\n"     | "R1 "           |
      | "* R2 R3 R4"            | "R1 1:R2 1:R3 1:R4,1:* G5 O5"           | "{ *R1 !R2 !R3 !R4 }\n{ !* *G5 *O5 }\n"     | "R3 B5 "        |
      | "* R2 R3 R4"            | "R1 1:R2 1:R3 1:R4,1:* B5 G5 O5"        | "{ *R1 !R2 !R3 !R4 }\n{ !* *B5 *G5 *O5 }\n" | "R3 "           |

  @fromRunToRun
  Scenario Outline: Replace a Joker in a Run and Reuse in Another Run
    Given Test Server is started
    And Player 1 has played initial points
    And Player 2 has played initial points
    And Player 1 hand starts with "R2 R3 R4 R5 *"
    And Player 2 hand starts with "R3 R4 G5 G7 G8"
    When Player 1 plays <p1play>
    And Player 2 plays <p2play>
    Then table contains <table>
    And Player 2 hand contains <hand>
    Examples:
      | p1play                  | p2play                                  | table                                       | hand            |
      | "R2 R3 *"               | "1:R2 1:R3 R4,G7 G8 1:*"                | "{ !R2 !R3 *R4 }\n{ *G7 *G8 !* }\n"         | "R3 G5 "        |
      | "R2 R3 *"               | "1:R2 1:R3 R4,G5 1:* G7 G8"             | "{ !R2 !R3 *R4 }\n{ *G5 !* *G7 *G8 }\n"     | "R3 "           |
      | "R2 * R4 R5"            | "1:R2 R3 1:R4 1:R5,G7 G8 1:*"           | "{ !R2 *R3 !R4 !R5 }\n{ *G7 *G8 !* }\n"     | "R4 G5 "        |
      | "R2 * R4 R5"            | "1:R2 R3 1:R4 1:R5,G5 1:* G7 G8"        | "{ !R2 *R3 !R4 !R5 }\n{ *G5 !* *G7 *G8 }\n" | "R4 "           |
