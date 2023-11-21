<p align="center">
  <img style="width:100px;" src="https://cdn.discordapp.com/attachments/1159034838783893567/1159035012260319242/melody-logo.png?ex=651e6af8&is=651d1978&hm=1ef9304af11367211103a9fa1cd7329b29c3408f2399cff73cdb842fe108efcf" alt="MelodyMine Logo">
</p>

<h1 align="center" style="font-size: 36px; color: #7E3FAA; background: linear-gradient(to right, #8D5E8D, #7E3FAA); padding: 16px 0; border-radius: 8px;color:white">MelodyMine (Minecraft Voice Plugin) </h1>

<p align="center">
  <a href="https://github.com/vallerian/MelodyMine/blob/main/LICENSE"><img src="https://img.shields.io/github/license/vallerian/MelodyMine?style=for-the-badge&color=blue" alt="License"></a>
  <a href="#"><img src="https://img.shields.io/badge/TypeScript-007ACC?style=for-the-badge&logo=typescript&color=3178C6" alt="TypeScript"></a>
  <a href="#"><img src="https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&color=4A90E2" alt="Kotlin"></a>
</p>

<p align="center">
  🚀 Welcome to the MelodyMine GitHub Project! Explore the world of MelodyMine and enhance your Minecraft server experience. 🌍✨
</p>

## Introduction 🌐

MelodyMine is a revolutionary system for Minecraft servers where players can simultaneously enter a web interface, communicate, voice chat, and make their in-game interactions richer. Elevate your gameplay with MelodyMine! 🎮🌐


- 💻 **Website Demo:** [MelodyMine.TAHER7.iR](https://MelodyMine.TAHER7.iR)
- 🌍 **Minecraft Server Demo:** [Play.TAHER7.iR](https://Play.TAHER7.iR)

## Key Features 🌟


<img style="margin-bottom:10px;border-radius: 5px" src="https://cdn.discordapp.com/attachments/1159034838783893567/1171346459858325554/image.png?ex=655c5866&is=6549e366&hm=ad63d33eb0047f053aebc91c0f4df56c449bc92cfa23333c082b9ad512d483f0&"/>

- 🗣️ **Admin Mode:** Admins can communicate with players across the entire server and listen to their conversations, even from a distance. Maintain control and ensure a safe environment. 🔊🔒

<img style="margin-bottom:10px;border-radius: 5px" src="https://cdn.discordapp.com/attachments/1159034838783893567/1159038696255934594/image.png?ex=651e6e67&is=651d1ce7&hm=8ad93989d50b1655494aec5d998a58ebdb6d3307b5adc148d614ed268aecbc4d&"/>

- 📼 **Toggle Logger:** Users can easily record player interactions and voice conversations. Starting and stopping voice recordings is a breeze, providing valuable insights into your server's activities. 📜📼

<img style="margin-bottom:10px;border-radius: 5px" src="https://cdn.discordapp.com/attachments/1159034838783893567/1159039652272025710/image.png?ex=651e6f4b&is=651d1dcb&hm=edc4ae3e39e0ff2afedea7b657ec0d6eeac6b72f9d788c4bd267c64c768edf8c&"/>

- 🔇 **Player Mute/Unmute:** Users can mute or unmute players directly from the web interface, ensuring that no one's voice goes unnoticed. Manage the voice communication effectively. 🧏‍♂️📢

<img style="margin-bottom:10px;border-radius: 5px" src="https://cdn.discordapp.com/attachments/1159034838783893567/1159040155890483290/image.png?ex=651e6fc3&is=651d1e43&hm=6ae140b8535c2c8f6a879eab70a939e04cde9cd946b420a7c141a06151cf8bd9&"/>

- 🌐 **Proxy-Less Plugin:** MelodyMine can change players' game modes on the website without the need for a proxy like Velocity or BungeeCord. It also captures the voices of players in the same game mode and those nearby, creating a seamless experience. 🏹🖥️

- 🔒 **AuthMe Support:** Seamlessly integrate with AuthMe, allowing players to enter the server using their username and password. Enhance security and accessibility. 🔒🔑

<img style="margin-bottom:10px;border-radius: 5px" src="https://cdn.discordapp.com/attachments/1159034838783893567/1159040820444414003/image.png?ex=651e7061&is=651d1ee1&hm=e50a9d3d80701c47f25a9ce54a6028ad5aaee1725e884d5268a30299c7eb6de1&"/>

- 🌐 **Automatic Login:** Users can obtain an automatic login link by using the `/melodymine start link` command, simplifying website login and reducing hassle. 🌐🚪

<img style="margin-bottom:10px;border-radius: 5px" src="https://cdn.discordapp.com/attachments/1159034838783893567/1159041197218746379/image.png?ex=651e70bb&is=651d1f3b&hm=dd83a23900e58a03fd6732ee1db6baf39e2b8d496f2139a1be21357d3c105b65&"/>

- 🌐 **QRCode Automatic Login:** Players can obtain a QR code by using the command `/melodymine start qrcode`, and by scanning it, they can enter the website. 🌍✨

<img style="margin-bottom:10px;border-radius: 5px" src="https://cdn.discordapp.com/attachments/1159034838783893567/1161726011247968276/melody-qrcode.png?ex=653958a8&is=6526e3a8&hm=a512f1de25c741d6da891b2254fc2e2a7ed39ffd4e519c3c4eba0ea16788cf6c&"/>

- 🛠️ **Comprehensive API:** Developers have access to a comprehensive API, including events like `PlayerJoinWebEvent` and `PlayerLeaveWebEvent`, enabling the creation of custom plugins and expanding the possibilities of your server. 📚👩‍💻

- 📝 **Simple Plugin config.yml:** Customize plugin settings and messages easily with the user-friendly config.yml.

```yaml
# ----- Database configs -----
database:
  host: "localhost"
  port: "3306"
  user: 'root'
  password: ''
  database_name: 'melody'

# ----- Basic configs -----

# game mode name.
server: "Lobby" # must be unique!

# website url
website: "http://localhost:3000"

# websocket url
websocket-url: "ws://localhost:4000"

# websocket auth security key !!! (important) change that after install plugin !!!
websocket-auth-key: "" # must be the same in web server auth key

# users must be online in website and active voice chat.
force-voice: false

# shortcut for toggle self mute in website (Shift + F)
mute-toggle-shortcut: true

# call configs
call-pending-time: 600 # must be Long (10L = 0.5s, 20L = 1s,...)

# ------ sound configs ------

3D-sound: true # player voice 3D mode.
hear-lazy: true # default player volume (when 3DSound is "false" you can use this feature).
max-distance: 30 # distance that players can hear each other.
ref-distance: 5  # distance that players can hear each other max volume.
inner-angle: 120 # angle that players can hear each other max volume.
outer-angle: 180 # angle that players can hear each other.
outer-volume: 0.3 # outer angle volume.

# update player distance time (as Long)
update-distance-time: 10 # must be Long (10L = 0.5s, 20L = 1s,...)
```

- 📞 **Call System:** You have the ability to make a call using the command `/melodymine call start <player>` to call other players.

<img style="margin-bottom:10px;border-radius: 5px" src="https://media.discordapp.net/attachments/1159034838783893567/1171354819643047977/callpng.png?ex=655c602f&is=6549eb2f&hm=310550c82085cfd69735ad77502dc30d57b94605187fbab68229c619ec435fc7&="/>

- 🎧 **3D Voice Capability:** Player sounds are played in 3D, and you can determine the player's position through sound.

<img style="margin-bottom:10px;border-radius: 5px" src="https://cdn.discordapp.com/attachments/1159034838783893567/1176601670650703933/melody-sound4.png?ex=656f76b2&is=655d01b2&hm=08004a4c29ab3370656f0b6963689fb76c27dc22128b939d75d5dad32fb73333&">

- 🐞 **Robust System:** MelodyMine is a highly reliable and secure system with minimal bugs. Any discovered bugs are promptly addressed and fixed, ensuring a smooth and hassle-free experience for all users. Be part of a community that values your experience and offers continuous improvement. Find announcements and updates on our [GitHub repository](https://github.com/vallerian/MelodyMine). 🛠️🐛

## Links 🔗

- 📖 **Wiki**: To access detailed documentation, visit the [MelodyMine Wiki](https://github.com/vallerian/MelodyMine/wiki).

- 💾 **Plugin Installation**: Get started with the installation by following the [Plugin Installation Guide](https://github.com/Vallerian/MelodyMine/wiki/installation).

- 📜 **Command List**: Explore the list of available commands in the [Command List](https://github.com/Vallerian/MelodyMine/wiki/commands).

- 🚦 **Permissions List**: Check out the permissions you need in the [Permissions List](https://github.com/Vallerian/MelodyMine/wiki/permissions).

- 📄 **Placeholder Information**: Discover placeholder details in the [Placeholder Information](https://github.com/Vallerian/MelodyMine/wiki/placeholders).

- 📚 **API Documentation**: Dive into the API for advanced customization in the [API Documentation](https://github.com/Vallerian/MelodyMine/wiki/api).

- 🧩 **Spigot**: Discover MelodyMine on [Spigot](https://www.spigotmc.org/resources/melodymine-minecraft-voice-plugin.112938/)
- 💼 **Modrinth**: Find MelodyMine on [Modrinth](https://modrinth.com/plugin/melodymine)
- 🌐 **Polymart**: Check out MelodyMine on [Polymart](https://polymart.org/resource/melodymine.4875)
- 💬 **Hangar**: Discover MelodyMine on [Hangar](https://hangar.papermc.io/TAHER7/MelodyMine)

## Get Started 🚀

Ready to harmonize your Minecraft server experience? Join the MelodyMine community and embark on a journey to enhance your Minecraft server. Explore the endless possibilities, and remember, your experience matters to us! Connect, engage, and play with MelodyMine today! 🌟🎮


🌐 Visit our [GitHub repository](https://github.com/vallerian/melodymine) for updates, bug reports, and contributions. 🛠️🐞

<img style="border-radius: 5px" src="https://bstats.org/signatures/bukkit/MelodyMine.svg"/>