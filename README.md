# LAB1

---

Github：[https://github.com/SeleiXi/FDU-SOFT130059-Object-Oriented-Programming/](https://github.com/SeleiXi/FDU-SOFT130059-Object-Oriented-Programming/)

---

## About the project

Prof. Zhang mentioned in class that this Lab requires the concept of object-oriented, which is also a key focus of this course (SOFT130058). Therefore, the project emphasizes the use of features such as encapsulation and abstraction.

1. Entity

- Board
- Piece
- Player

2. Encapsulation

- Game.java

```Java

    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Scanner scanner;

```

3. Abstraction

The Piece class is an enum, which abstracts the concept of a chess piece into a set of predefined constants (BLACK, WHITE, EMPTY). This abstraction allows the rest of the code to work with these high-level concepts without worrying about their underlying representation.

```java
   public enum Piece {
       BLACK("○"),
       WHITE("●"),
       EMPTY(".")
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
cd FDU-SOFT130059-Object-Oriented-Programming
java -jar target/chess-game-1.0-SNAPSHOT.jar
```

## Details

The project has not excluded ./target in .gitignore, to fulfill the requirements that a jar package should be included.

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

- Move Input Format: Row number + column letter (e.g., 1a, 2b)
- Row numbers start from 1, and column letters start from A
- The system will indicate which player’s turn it is to make a move
