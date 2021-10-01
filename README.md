# rummikub
A textually interfaced game of Rummikub using TDD principles for COMP 4004

How to import the project into IntelliJ:
1. Download and extract ZIP from GitHub.
2. In the ZIP file, there will be a *rummikub-master* directory inside another *rummikub-master* directory. Use the inner directory to import into IntelliJ.

How to run automated JUnit tests:
1. Run *src/test/java/rummikub/UnitTests.java*. Note that this will take a couple minutes, since all tests are networked.

How to run game for manual/random play:
1. Run *src/main/java/rummikub/GameServer.java*.
2. Run three instances of *src/main/java/rummikub/Player.java*. You may have to edit the Run Configuration of the Player class to allow multiple instances.
3. Once all three Player instances are connected, you can start playing as Player 1. Once p1's turn is over, it will be p2's turn, then p3's, then back to p1, etc...

A few notes:
- In testing, I did not make each player draw all initial 14 tiles. This was to ensure I could test what exactly is in the player's hand after playing a turn. However, during manual play, all 14 random tiles are properly dealt.
- I have some commits (that I believe still follow TDD principles) before the start of the given testplan, since I started before the testplan was released. This also explains why many of the later tests ran without any corresponding code commits.
- In the final test case (complex video), p1 plays {JH JS JC}, even though it is not a part of the table reuse, not in the video, and not part of the expected output of the test in Excel. I included this meld anyway, and so it is left untouched on the table after the reuse and part of the expected output.
- I prefixed all networking code commits with "Networking: " so that it would not be confused with other "CODE FOR" commits that are part of the TDD process.