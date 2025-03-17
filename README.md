# LAB3

---

Github：[https://github.com/SeleiXi/FDU-SOFT130059-Object-Oriented-Programming/tree/Lab3](https://github.com/SeleiXi/FDU-SOFT130059-Object-Oriented-Programming/tree/Lab3)

---

## Features

- Support for 3 separate chess boards that can be played concurrently
- Players can easily switch between boards during gameplay
- Each board maintains its state independently

## Details

- The board count is limited to 3, a constand is defined at the start of Game.java
```
private static final int BOARD_COUNT = 3;
```

- The condition to end the game is not `!board.isFull()` now, because we have 3 boards, so we define a function to judge
```
    private void checkGameEnd() {
         isGameEnded = true;
         for (int i = 0; i < BOARD_COUNT; i++) {
             if (!boards[i].isFull()) {
                 isGameEnded = false;
                 break;
             }
         }
     }
```

- I won't hardcode a specific number to my program
```  for (int i = 0; i < boardSize; i++) { ```
```  System.out.print("请玩家[" + currentPlayer.getName() + "]输入落子位置或棋盘号(1-" + BOARD_COUNT + ")："); ```

- according to the lab3 document (the tips part), error message depends on the length of the input
``` java
 if (input.length() == 1) {
                 processBoardSelection(input);
             } else if (input.length() >= 2) {
                 validMove = processMoveInput(input);
             } else {
                 System.out.println("输入格式有误，请使用1-" + BOARD_COUNT + "的数字或数字+字母（如：1a）");
             }
         }
     }
 ```


## Getting Started

### Docker

```
docker pull seleixi/soft130059:latest
docker run -it seleixi/soft130059:latest
```

### Maven

```
git clone https://github.com/SeleiXi/FDU-SOFT130059-Object-Oriented-Programming.git
cd FDU-SOFT130059-Object-Oriented-Programming
mvn clean package
java -jar target/chess-game-1.0-SNAPSHOT.jar
```

### jar package

```
git clone https://github.com/SeleiXi/FDU-SOFT130059-Object-Oriented-Programming.git
checkout to the corresponding branch (e.g. lab2, lab3)
cd FDU-SOFT130059-Object-Oriented-Programming
java -jar target/chess-game-1.0-SNAPSHOT.jar

## Project Structure

```
com.chess
├── Main.java          # Game Entry
├── entity
│   ├── Board.java
│   ├── Piece.java
│   └── Player.java
└── service
    └── Game.java
```

### Features

- Board Display: An 8x8 board
- Player Interaction: Players input their moves via the console
- Game Rules: Moves can only be made in empty positions
- Game End Condition: The game ends when the board is full

### Game Operation

- Change to a different board: board number(e.g. 1,2,3), this lab has limited us for 3 boards
- Move Input Format: Row number + column letter (e.g., 1a, 2b)
    - Row numbers start from 1, and column letters start from A
- The system will indicate which player's turn it is to make a move
