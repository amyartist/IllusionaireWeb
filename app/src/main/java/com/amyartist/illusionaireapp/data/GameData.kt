package com.amyartist.illusionaireapp.data

import com.amyartist.illusionaireapp.R

class Monster(
    val description: String,
    val strength: Int,
    val image: Any
)

object Monsters {
    val LIZARD = Monster(
        description = "Scary lizard monster", strength = 5, image = R.drawable.lizard_monster
    )
}

enum class ActionType {
    LOOK, OPEN, GO
}

data class Action(
    val id: String,
    val type: ActionType,
    val monster: Monster? = null,
    val appeaseMessage: String? = null,
    val appeasePrompt: String? = null,
    val message: String? = null,
    val item: String? = null,
    val contents: List<Item>? = null,
    val opened: Boolean = false,
    val direction: String? = null,
    val destinationRoomId: String? = null,
    val avatar: Avatar? = Avatars.NEUTRAL
)

data class Room(
    val id: String,
    val name: String,
    val prompt: String,
    val exits: Map<String, String>,
    val actions: List<Action>
)

val gameRooms = mapOf(
    "starting_room" to Room(
        id = "starting_room",
        name = "Starting Room",
        prompt = "Generate an image of a dimly lit entryway of a old mansion. The decor is generally dark wood with dusty bronze fixtures and details. The year is 1850. There is a door to the north and a chest decorated with jewels in the corner. There is only one door visible in the room. Use an anime cartoon background style of art.",
        exits = mapOf("north" to "hallway"),
        actions = listOf(
            Action(
                id = "starting_room_look",
                type = ActionType.LOOK,
                message = "You are in the dimly lit entryway of a old mansion. There is a door to the north and a bejeweled chest in the corner.",
                avatar = Avatars.NEUTRAL
            ),
            Action(
                id = "starting_room_open_chest",
                type = ActionType.OPEN,
                item = "chest",
                contents = listOf(Weapons.RUSTY_DAGGER),
                avatar = Avatars.HAPPY
            ),
            Action(
                id = "starting_room_go_north",
                type = ActionType.GO,
                direction = "north",
                destinationRoomId = "hallway",
                avatar = Avatars.NEUTRAL
            )
        )
    ),
    "hallway" to Room(
        id = "hallway",
        name = "Hallway",
        prompt = "Generate an image of a dimly lit, long hallway. There are paintings on the walls. There are gothic style windows but it is very dark outside. The year is 1850. There is an old door at the far end of the hallway. it is the only door visible in the hallway. The decor is generally dark wood with dusty bronze fixtures and details. Use an anime cartoon background style of art.",
        exits = mapOf("south" to "starting_room", "north" to "library"),
        actions = listOf(
            Action(
                id = "hallway_look",
                type = ActionType.LOOK,
                message = "You are in a long, dark hallway. There is a door at the north end of the hallway and another leading back to the south. One painting is suspicious.",
                avatar = Avatars.NEUTRAL
            ),
            Action(
                id = "hallway_open_painting",
                type = ActionType.OPEN,
                item = "painting",
                monster = Monsters.LIZARD,
                appeaseMessage = "Monster says: Show me a kitty in a room!",
                appeasePrompt = "Is this a picture of a room with cat in it? Only answer yes or no.",
                avatar = Avatars.SURPRISED
            ),
            Action(
                id = "hallway_go_south",
                type = ActionType.GO,
                direction = "south",
                destinationRoomId = "starting_room",
                avatar = Avatars.NEUTRAL
            ),
            Action(
                id = "hallway_go_north",
                type = ActionType.GO,
                direction = "north",
                destinationRoomId = "library",
                avatar = Avatars.NEUTRAL
            )
        )
    ),
    "library" to Room(
        id = "library",
        name = "Library",
        prompt = "Generate an image of an old library with a high ceiling. There are tall shelves full of old books. The room is lit with candle fixtures on the wall. There is a creepy painting of an old man dressed in a smoker's jacket on the left side of the room. It is nighttime. The decor is generally dark wood with dusty bronze fixtures and details. Use an anime cartoon background style of art.",
        exits = mapOf("south" to "hallway"),
        actions = listOf(
            Action(
                id = "library_look",
                type = ActionType.LOOK,
                message = "You see an old library, full of books and mystique. One book in particular piques your interest.",
                avatar = Avatars.NEUTRAL
            ),
            Action(
                id = "library_go_south",
                type = ActionType.GO,
                direction = "south",
                destinationRoomId = "hallway",
                avatar = Avatars.NEUTRAL
            ),
            Action(
                id = "library_open_book",
                type = ActionType.OPEN,
                item = "book",
                monster = Monsters.LIZARD,
                appeaseMessage = "Monster says: Show me a kitty in a room!",
                appeasePrompt = "Is this a picture of a room with cat in it? Only answer yes or no.",
                avatar = Avatars.SURPRISED
            )
        )
    )
)
