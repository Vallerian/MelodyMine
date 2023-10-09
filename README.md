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

<img style="margin-bottom:10px;border-radius: 5px" src="https://cdn.discordapp.com/attachments/1159034838783893567/1159037980346949713/image.png?ex=651e6dbc&is=651d1c3c&hm=3f2b28bd78ea6cec536e96a13dd18b680a3d03f6da9eb7084f575505b2af2940&"/>

- 🗣️ **Admin Mode:** Admins can communicate with players across the entire server and listen to their conversations, even from a distance. Maintain control and ensure a safe environment. 🔊🔒

<img style="margin-bottom:10px;border-radius: 5px" src="https://cdn.discordapp.com/attachments/1159034838783893567/1159038696255934594/image.png?ex=651e6e67&is=651d1ce7&hm=8ad93989d50b1655494aec5d998a58ebdb6d3307b5adc148d614ed268aecbc4d&"/>

- 📼 **Toggle Logger:** Users can easily record player interactions and voice conversations. Starting and stopping voice recordings is a breeze, providing valuable insights into your server's activities. 📜📼

<img style="margin-bottom:10px;border-radius: 5px" src="https://cdn.discordapp.com/attachments/1159034838783893567/1159039652272025710/image.png?ex=651e6f4b&is=651d1dcb&hm=edc4ae3e39e0ff2afedea7b657ec0d6eeac6b72f9d788c4bd267c64c768edf8c&"/>

- 🔇 **Player Mute/Unmute:** Users can mute or unmute players directly from the web interface, ensuring that no one's voice goes unnoticed. Manage the voice communication effectively. 🧏‍♂️📢

<img style="margin-bottom:10px;border-radius: 5px" src="https://cdn.discordapp.com/attachments/1159034838783893567/1159040155890483290/image.png?ex=651e6fc3&is=651d1e43&hm=6ae140b8535c2c8f6a879eab70a939e04cde9cd946b420a7c141a06151cf8bd9&"/>

- 🌐 **Proxy-Less Plugin:** MelodyMine can change players' game modes on the website without the need for a proxy like Velocity or BungeeCord. It also captures the voices of players in the same game mode and those nearby, creating a seamless experience. 🏹🖥️

- 🔒 **AuthMe Support:** Seamlessly integrate with AuthMe, allowing players to enter the server using their username and password. Enhance security and accessibility. 🔒🔑

<img style="margin-bottom:10px;border-radius: 5px" src="https://cdn.discordapp.com/attachments/1159034838783893567/1159040820444414003/image.png?ex=651e7061&is=651d1ee1&hm=e50a9d3d80701c47f25a9ce54a6028ad5aaee1725e884d5268a30299c7eb6de1&"/>

- 🌐 **Automatic Login:** Users can obtain an automatic login link by using the `/melodymine start` command, simplifying website login and reducing hassle. 🌐🚪

<img style="margin-bottom:10px;border-radius: 5px" src="https://cdn.discordapp.com/attachments/1159034838783893567/1159041197218746379/image.png?ex=651e70bb&is=651d1f3b&hm=dd83a23900e58a03fd6732ee1db6baf39e2b8d496f2139a1be21357d3c105b65&"/>

- 🛠️ **Comprehensive API:** Developers have access to a comprehensive API, including events like `PlayerJoinWebEvent` and `PlayerLeaveWebEvent`, enabling the creation of custom plugins and expanding the possibilities of your server. 📚👩‍💻

- 📝 **Simple Plugin config.yml:** Customize plugin settings and messages easily with the user-friendly config.yml. 📝✉️

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

# distance that players can hear each other.
hear-distance: 35

# update player distance time (as Long)
update-distance-time: 10 # must be Long (10L = 0.5s, 20L = 1s,...)

# increase & decrease sound in hear-distance.
hear-lazy: true

# users must be online in website and active voice chat.
force-voice: false



# ----- Message configs -----

# main plugin prefix.
prefix: "<gradient:#FFF4E4:#DDB216>MelodyMine</gradient> <#FFF4E4><bold>|</bold>"

# open website link message.
website-message: "Click to open website and start voice chat."

# Logger messages | placeholder {PLAYER}
website-join-logger: "{PLAYER} has join the website."
website-leave-logger: "{PLAYER} has left the website."
website-start-voice-logger: "{PLAYER} has start voice."
website-end-voice-logger: "{PLAYER} has end voice."

# Commands description message
reload-description: "Reload all configuration file."
adminmode-description: "Conversation with players in voice."
start-description: "Get link to start voice chat."
mute-description: "Mute player in voice chat."
unmute-description: "Unmute player in voice chat."

# Placeholder Messages
placeholder-web-online-true: "Online"
placeholder-web-online-false: "Offline"
placeholder-voice-active-true: "Active"
placeholder-voice-active-false: "Inactive"
placeholder-adminmode-true: "On"
placeholder-adminmode-false: "Off"
placeholder-mute-true: "Mute"
placeholder-mute-false: "Unmute"

# website status messages, placeholders {PLAYER}

# join website
join-website-message: "You have connected to the website."
join-website-message-type: "message" # message or actionbar

# leave website
leave-website-message: "You have disconnected from the website."
leave-website-message-type: "message" # message or actionbar

# start voice
start-voice-message: "You have start the voice chat."
start-voice-message-type: "message" # message or actionbar

# end voice
end-voice-message: "You have end the voice chat."
end-voice-message-type: "message" # message or actionbar

```

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

<img style="mix-blend-mode: overlay;border-radius: 5px" src="https://bstats.org/signatures/bukkit/MelodyMine.svg"/>