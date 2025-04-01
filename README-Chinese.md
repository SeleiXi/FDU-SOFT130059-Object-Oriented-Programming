# Lab 3

---

Github：[https://github.com/SeleiXi/FDU-SOFT130059-Object-Oriented-Programming/tree/Lab3](https://github.com/SeleiXi/FDU-SOFT130059-Object-Oriented-Programming/tree/Lab3)

---

## 运行方式

### Docker

```
docker pull seleixi/soft130059:latest
docker run -it seleixi/soft130059:latest
```

### Maven

```
mvn clean package
java -jar target/chess-game-1.0-SNAPSHOT.jar
```

### jar包

```
java -jar target/chess-game-1.0-SNAPSHOT.jar
```


## 新加的功能

- 支持同时在3个棋盘上进行游戏
- 玩家可以在游戏过程中轻松切换棋盘
- 每个棋盘独立维护其状态

## 细节

- 棋盘数量限定为3个，在Game.java开头定义了常量
```
private static final int BOARD_COUNT = 3;
```

- 现在游戏结束的条件不再是 `!board.isFull()`，因为我们有3个棋盘，所以我们定义了一个函数来判断
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

- 程序没有硬编码特定的数字，更符合软件工程规范
1. ```  for (int i = 0; i < boardSize; i++) { ```
2. ```  System.out.print("请玩家[" + currentPlayer.getName() + "]输入落子位置或棋盘号(1-" + BOARD_COUNT + ")："); ```

- 根据实验三文档（提示部分），错误信息取决于输入的长度
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



## 项目结构

```
com.chess
├── Main.java          # 游戏入口
├── entity
│   ├── Board.java     # 棋盘
│   ├── Piece.java     # 棋子
│   └── Player.java    # 玩家
└── service
    └── Game.java      # 游戏逻辑
```

### 功能

- 棋盘显示：8x8棋盘
- 玩家交互：玩家通过控制台输入他们的落子
- 游戏规则：只能在空位置落子
- 游戏结束条件：当所有棋盘都满时游戏结束

### 游戏操作

- 切换到不同的棋盘：输入棋盘号（例如1,2,3），本Lab限制为3个棋盘
- 落子格式：行号+列字母（例如，1a，2b）
    - 行号从1开始，列字母从A开始
- 系统将指示轮到哪位玩家落子 