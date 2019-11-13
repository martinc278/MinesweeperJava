# Minesweeper in Java
###### Here's how, using Java, I programmed an entire, network enabled, Minesweeper

## 1. Local Minesweeper
Being able to stay fully local thanks to its own main application, the Minesweeper has the following features:
- GUI (extends JFrame, uses JPannel, onclick listener etc, ...)
- Different level, with different pannel sizes
- Timer to count the time before user wins
- New parties on the fly, with ability to change the level

## 2. Serveur
The different clients can play together with the serveur ruling them. Once launched, it accepts clients and store them.
Once the serveur decides that there is enough person, it starts, by letting the clients play. 
- Once somebody clicks on a case, it is painted a certain color for all the players, and nobody can click on it anymore. 
- The number of bombs around a case clicked is showed on the case once clicked
- The party stops once somebody has lost

## 3. Librairies
- JPannel (and the on click listener)
- Runnable (different threads for couting time and for the serveur to accept new clients)
- Collections, for ArrayList and HashMap management
