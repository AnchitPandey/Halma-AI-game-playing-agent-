# Halma-AI-game-playing-agent-

This repo contains my code for AI agent for playing Halma (Chinese checkers)<br>
1. halma.java  - Main Code<br>
2. MasterAgent.java - Master Agent for simulating a two player game. This file creates 2 instances of the halma.java agent and starts a game between them<br>

* The agent takes on average 170 s (at max depth 4 in the search tree) to finish the game when played against itself.<br>
* The agent finishes in 96 moves on average when played against itself.<br>
* For attaining faster speeds, the code incorporates concepts of Alpha Beta Pruning, Transposition table, Move Ordering and Move Sorting. 

