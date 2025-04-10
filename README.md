# Lab4
---

Github：[https://github.com/SeleiXi/FDU-SOFT130059-Object-Oriented-Programming/tree/Lab3](https://github.com/SeleiXi/FDU-SOFT130059-Object-Oriented-Programming/tree/Lab4)

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

## 支持的游戏模式

### 和平棋（Peace）
- 基本规则：玩家轮流在棋盘上放置棋子，棋子只能放在空位置上
- 游戏结束：当所有棋盘都满时游戏结束

### 黑白棋（Reversi/奥赛罗）
- 基本规则：玩家轮流在棋盘上放置棋子，并翻转被夹住的对手棋子
- 合法落子：必须能够夹住对手至少一个棋子
- Pass规则：当玩家没有合法落子位置时，自动让对手行棋
- 游戏结束：当双方都无法落子或棋盘已满时结束
- 胜负判定：以棋盘上各自颜色棋子数量决定胜负

## 核心功能

- 多游戏支持：可以在和平棋与黑白棋模式间切换
- 多棋盘支持：在一个游戏会话中维护多个棋盘
- 实时游戏切换：可随时切换不同游戏或创建新游戏
- 显示当前玩家：清晰标识该哪位玩家落子
- 显示可落子位置：在黑白棋模式下显示合法落子位置（以 + 标记）
- 显示双方得分：在黑白棋模式下实时统计双方棋子数量

## 操作指南

- 落子：输入行号+列字母（例如 1a，2b）
- 切换棋盘：输入棋盘号（1-3）
- 切换/新建游戏：输入游戏类型（peace, reversi）
- 黑白棋跳过行棋：输入 pass（仅当没有合法落子位置时允许）
- 退出游戏：输入 quit

## 游戏界面

```
  A B C D E F G H
1 . . . . . . . .
2 . . . + . . . .
3 . . . ○ + . . .
4 . . . ○ ○ + . .  游戏#2 (reversi)           游戏列表
5 . . + ○ ● . . .  玩家[Player1]  得分: 3     1. peace
6 . . . + . . . .  玩家[Player2] ● 得分: 2    2. reversi (当前)
7 . . . . . . . .                    3. peace
8 . . . . . . . .
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
    ├── Game.java      # 游戏基础逻辑
    └── ReversiGame.java # 黑白棋特有逻辑
```

## 开发细节

- 继承关系：ReversiGame继承Game类，实现特有的游戏规则
- 多态应用：通过override实现不同游戏模式的差异化逻辑
- 封装：各类通过私有变量与公开方法实现良好封装
- 代码重用：核心游戏功能集中在父类中，子类只实现特殊功能
- 灵活性：通过常量控制多项游戏参数，避免硬编码 