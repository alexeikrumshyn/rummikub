@tag
Feature: Test validity of different melds
  I want to use this feature to test validity of different melds

  @basicSets
  Scenario Outline: Test Validity of Basic Sets
    Given Test Server is started
    And Player 1 hand starts with <initialHand>
    When Player 1 plays <tiles>
    Then table contains <table>
    And Player 1 hand contains <hand>
    Examples:
      | initialHand       | tiles             | table                       | hand                          |
      | "R5 B5 G5" 		  |  "R5 B5 G5"       | "{ *R5 *B5 *G5 }\n"		    | ""                            |
      | "R5 B5 G5 O5" 	  | "R5 B5 G5 O5" 	  | "{ *R5 *B5 *G5 *O5 }\n"		| ""                            |
      | "R5 R5 G5" 		  | "R5 R5 G5" 		  |	""		                    | "R5 R5 G5 ? ? ? "             |
      | "R5 G5 G5 O5" 	  | "R5 G5 G5 O5" 	  |	""		                    | "R5 G5 G5 O5 ? ? ? "          |
      | "R5 G5 B5 B5 O5"  | "R5 G5 B5 B5 O5"  |	""		                    | "R5 G5 B5 B5 O5 ? ? ? "       |
      | "R5 G5" 	      | "R5 G5" 	      |	""		                    | "R5 G5 ? ? ? "                |

  @setsWithJokers
  Scenario Outline: Test Validity of Sets with Jokers
    Given Test Server is started
    And Player 1 hand starts with <initialHand>
    When Player 1 plays <tiles>
    Then table contains <table>
    And Player 1 hand contains <hand>
    Examples:
      | initialHand       | tiles             | table                       | hand                          |
      | "R5 G5 *" 	      | "R5 G5 *" 	      | "{ *R5 ** *G5 }\n"		    | ""                            |
      | "R5 * O5" 	      | "R5 * O5" 	      | "{ *R5 ** *O5 }\n"		    | ""                            |
      | "* G5 O5" 	      | "* G5 O5" 	      | "{ ** *G5 *O5 }\n"	        | ""                            |
      | "R5 B5 G5 *" 	  | "R5 B5 G5 *" 	  |	"{ *R5 *B5 *G5 ** }\n"		| ""                            |
      | "R5 B5 * O5" 	  | "R5 B5 * O5" 	  |	"{ *R5 *B5 ** *O5 }\n"		| ""                            |
      | "* B5 G5 O5" 	  | "* B5 G5 O5" 	  |	"{ ** *B5 *G5 *O5 }\n"		| ""                            |
      | "* * G5"          | "* * G5"          |	"{ ** ** *G5 }\n"		    | ""                            |
      | "* * G5 O5"       | "* * G5 O5"       |	"{ ** ** *G5 *O5 }\n"		| ""                            |
      | "*"               | "*"               |	""		                    | "? ? ? * "                    |

  @basicRuns
  Scenario Outline: Test Validity of Basic Runs
    Given Test Server is started
    And Player 1 hand starts with <initialHand>
    When Player 1 plays <tiles>
    Then table contains <table>
    And Player 1 hand contains <hand>
    Examples:
      | initialHand                                   | tiles                                         | table                                                           | hand              |
      | "R5 R6 R7"   	                              | "R5 R6 R7"   	                              |	"{ *R5 *R6 *R7 }\n"		                                        | ""                |
      | "R1 R2 R3 R4 R5 R6 R7 R8 R9 R10 R11 R12 R13"  | "R1 R2 R3 R4 R5 R6 R7 R8 R9 R10 R11 R12 R13"  |	"{ *R1 *R2 *R3 *R4 *R5 *R6 *R7 *R8 *R9 *R10 *R11 *R12 *R13 }\n" | ""                |
      | "R12 R13 R1" 		                          | "R12 R13 R1" 		                          |	"{ *R12 *R13 *R1 }\n"		                                    | ""                |
      | "R13 R1 R2" 		                          | "R13 R1 R2" 		                          |	"{ *R13 *R1 *R2 }\n"		                                    | ""                |
      | "R5"         	                              | "R5"         	                              | ""		                                                        | "R5 ? ? ? "       |
      | "R5 R6" 		                              | "R5 R6" 		                              |	""		                                                        | "R5 R6 ? ? ? "    |
      | "R5 O6 B7" 	                                  | "R5 O6 B7" 	                                  |	""		                                                        | "R5 O6 B7 ? ? ? " |

  @runsWithJokers
  Scenario Outline: Test Validity of Runs with Jokers
    Given Test Server is started
    And Player 1 hand starts with <initialHand>
    When Player 1 plays <tiles>
    Then table contains <table>
    And Player 1 hand contains <hand>
    Examples:
      | initialHand           | tiles                 | table                               | hand                |
      | "R5 R6 *" 		      | "R5 R6 *" 		      | "{ *R5 *R6 ** }\n"		            | ""                  |
      | "R5 * R7" 		      | "R5 * R7" 		      | "{ *R5 ** *R7 }\n"		            | ""                  |
      | "* R6 R7 R8" 		  | "* R6 R7 R8" 		  | "{ ** *R6 *R7 *R8 }\n"		        | ""                  |
      | "R5 R6 * R8" 		  | "R5 R6 * R8" 		  | "{ *R5 *R6 ** *R8 }\n"		        | ""                  |
      | "R5 R6 R7 R8 *" 	  | "R5 R6 R7 R8 *" 	  | "{ *R5 *R6 *R7 *R8 ** }\n"		    | ""                  |
      | "R12 R13 *" 	      | "R12 R13 *" 	      | "{ *R12 *R13 ** }\n"		        | ""                  |
      | "R13 * R2" 	          | "R13 * R2" 	          | "{ *R13 ** *R2 }\n"		            | ""                  |
      | "R11 R12 R13 * R2 R3" | "R11 R12 R13 * R2 R3" | "{ *R11 *R12 *R13 ** *R2 *R3 }\n"   | ""                  |
      | "R2 * * R5"           | "R2 * * R5"           | "{ *R2 ** ** *R5 }\n"		        | ""                  |
      | "R2 * R4 *"           | "R2 * R4 *"           | "{ *R2 ** *R4 ** }\n"		        | ""                  |
      | "R2 * R5" 	          | "R2 * R5" 	          | ""		                            | "R2 * R5 ? ? ? "    |
