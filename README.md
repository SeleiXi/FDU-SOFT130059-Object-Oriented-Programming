# 棋类游戏

这是一个简单的控制台棋类游戏，两名玩家轮流在棋盘上落子。

## 项目结构

```
com.chess
├── Main.java           # 应用程序入口点
├── entity              # 实体类包
│   ├── Board.java      # 棋盘实体
│   ├── Piece.java      # 棋子枚举
│   └── Player.java     # 玩家实体
└── service             # 服务层包
    └── Game.java       # 游戏逻辑服务
```

## 功能特点

- 棋盘显示：8x8 的棋盘
- 玩家交互：通过控制台输入落子位置
- 游戏规则：只能在空白位置落子
- 游戏结束条件：棋盘填满时游戏结束

## 如何运行

### 使用 Maven

```
mvn clean package
java -jar target/chess-game-1.0-SNAPSHOT.jar
```

### 使用 Docker

```
docker build -t chess-game .
docker run -it chess-game
```

## 游戏操作

- 落子位置输入格式：行号+列字母（例如：1a，2b）
- 行号从 1 开始，列字母从 A 开始
- 系统会显示当前轮到哪位玩家落子

Github：[https://github.com/SeleiXi/FDU-SOFT130059-Object-Oriented-Programming/](https://github.com/SeleiXi/FDU-SOFT130059-Object-Oriented-Programming/)

### LAB1

#### About the project

Prof. Zhang mentioned in class that this Lab requires the concept of object-oriented, which is also a key focus of this course (SOFT130058). Therefore, the project emphasizes the use of features such as encapsulation.

e.g.

```Game.Java

    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Scanner scanner;


```

####

#### Getting Started

You have two ways to run the project.

- Method 1 (recommended)

  1. Clone the repo

  ```sh
  git clone https://github.com/project-kxkg/ViDove.git
  cd ViDove
  ```

  2. Install Requirments

  ```sh
  conda create -n ViDove python=3.10 -y
  conda activate ViDove
  pip install --upgrade pip
  pip install -r requirements.txt
  ```

#### Details

The project has uploaded some files that should be in .gitignore, because TA may run the project in his/her local java environment.
