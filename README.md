# 🔑 Escape Room

A 2D puzzle-platformer game built in **Java** using **Java Swing** and **AWT Graphics2D**. Navigate through 6 increasingly challenging levels, avoid spikes and rotating saws, collect the key, and reach the door to escape!

## 🎮 Gameplay

- Move through each level, jumping across platforms while avoiding hazards
- Collect the **key** hidden in each level
- Once you have the key, reach the **door** to clear the level and advance
- Survive rotating saws and spike traps that reset the level on contact
- Complete all 6 levels to win

## 🕹️ Controls

| Key | Action |
|---|---|
| **← / →** | Move left / right |
| **Space** | Jump |
| **Enter** | Confirm / Select level / Continue |
| **Arrow Keys** | Navigate level select grid |

## 🛠️ Tech Stack

- **Java** — core game logic
- **Java Swing (`JPanel`, `JFrame`)** — game window and UI
- **AWT Graphics2D** — custom rendering (player, platforms, spikes, rotating saw animation, blinking eyes)
- **Timer / ActionListener** — game loop running at ~60 FPS (16ms tick)
- **KeyListener** — real-time keyboard input handling

## ✨ Features

- **6 handcrafted levels** with increasing difficulty
- **Physics-based movement** — gravity, jumping, and platform collision detection
- **Animated hazards** — rotating circular saw obstacles with dynamic angle transforms
- **Level select screen** — grid-based navigation to choose any unlocked level
- **Subtle character animation** — idle blinking eyes for a bit of personality
- **Game state machine** — clean transitions between Title, Level Select, Game, Clear, and End screens
- **Level timer** — tracks and displays completion time for each level

## 🚀 How to Run

Make sure you have Java (JDK 8+) installed, then:

```bash
javac CjGame.java
java CjGame
```

The game window will open at 800x450 resolution. Press **Enter** at the title screen to begin.

## 📂 Project Structure

```
CjGame.java   → Single-file implementation containing game logic, 
                rendering, input handling, and level data
```

## 🔮 Future Improvements

- Add sound effects and background music
- Add more levels beyond the current 6
- Add a scoring/leaderboard system based on completion time
- Externalize level data into a config file instead of hardcoding

---

*Built as a personal project to practice Java game development fundamentals — collision detection, state management, and 2D rendering with Graphics2D.*
