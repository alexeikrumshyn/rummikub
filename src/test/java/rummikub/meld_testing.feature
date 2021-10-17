@tag
Feature: Test validity of different melds
  I want to use this feature to test validity of different melds

  @basicSets
  Scenario Outline: Test Validity of Basic Sets
    Given Test Server is started
    When Player 1 plays <tiles>
    Then table contains <table>
    And Player 1 hand contains <hand>
    Examples:
      | tiles             | table                       | hand                          |
      | "R5 B5 G5" 		  | "{ *R5 *B5 *G5 }\n"		    | ""                            |
      | "R5 B5 G5 O5" 	  | "{ *R5 *B5 *G5 *O5 }\n"		| ""                            |
      | "R5 R5 G5" 		  |	""		                    | "R5 R5 G5 ? ? ? "             |
      | "R5 G5 G5 O5" 	  |	""		                    | "R5 G5 G5 O5 ? ? ? "          |
      | "R5 G5 B5 B5 O5"  |	""		                    | "R5 G5 B5 B5 O5 ? ? ? "       |
      | "R5 G5" 	      |	""		                    | "R5 G5 ? ? ? "                |
