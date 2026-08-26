package org.everyvoice.aac.engine

/**
 * The starter vocabulary.
 *
 * Built from core-vocabulary research: a few hundred high-frequency words
 * cover the large majority of everyday communication. The selection favors
 * words that let a person direct their own life — "stop", "no", "help",
 * "I don't understand" — over nouns that name things around them.
 *
 * Every entry here is a starting point, not a cage: caregivers edit, add,
 * and delete freely. The categories use stable string IDs so backups and
 * seeds stay compatible across versions.
 */
object Vocabulary {

    fun seed(): List<SeedCategory> = listOf(

        SeedCategory(
            id = "core",
            name = "Core",
            icon = "🏠",
            tiles = tiles(
                "I", "you", "we", "they", "he", "she", "it",
                "want", "need", "have", "like", "don't like",
                "go", "come", "stop", "help", "more", "all done",
                "yes", "no", "maybe",
                "please", "thank you", "sorry",
                "not", "this", "that", "here", "there",
                "now", "later", "again",
                "what", "where", "when", "who", "why", "how",
                "my", "your"
            )
        ),

        SeedCategory(
            id = "phrases",
            name = "Phrases",
            icon = "💬",
            tiles = listOf(
                Tile("I need help", "I need help."),
                Tile("I need the bathroom", "I need the bathroom."),
                Tile("I'm in pain", "I am in pain."),
                Tile("I don't understand", "I don't understand."),
                Tile("Please wait", "Please wait."),
                Tile("Call my caregiver", "Please call my caregiver."),
                Tile("I need a break", "I need a break."),
                Tile("Not what I meant", "That's not what I meant."),
                Tile("Say it again", "Can you say that again?"),
                Tile("I'm hungry", "I'm hungry."),
                Tile("I'm thirsty", "I'm thirsty."),
                Tile("Too hot", "I'm too hot."),
                Tile("Too cold", "I'm too cold."),
                Tile("Leave me alone", "Leave me alone, please."),
                Tile("I want to go home", "I want to go home."),
                Tile("Thanks for helping", "Thank you for helping me."),
                Tile("Where is my phone", "Where is my phone?"),
                Tile("I love you", "I love you."),
                Tile("Stop please", "Stop, please."),
                Tile("That's right", "Yes, that's right."),
                Tile("I need my medicine", "I need my medicine."),
                Tile("Something is wrong", "Something is wrong."),
                Tile("I'm scared", "I'm scared."),
                Tile("I want to be alone", "I want to be alone."),
                Tile("Can I have some water", "Can I have some water?")
            )
        ),

        SeedCategory(
            id = "people",
            name = "People",
            icon = "👥",
            tiles = tiles(
                "mom", "dad", "grandma", "grandpa", "brother", "sister",
                "friend", "teacher", "doctor", "nurse", "caregiver",
                "baby", "man", "woman", "child", "family", "neighbor",
                "bus driver", "therapist", "aide"
            )
        ),

        SeedCategory(
            id = "actions",
            name = "Actions",
            icon = "🏃",
            tiles = tiles(
                "go", "come", "run", "walk", "sit", "stand",
                "sleep", "wake up", "eat", "drink", "play",
                "read", "write", "listen", "watch", "look",
                "talk", "call", "text", "give", "take", "put",
                "open", "close", "push", "pull", "wash", "brush",
                "cook", "clean", "work", "drive", "ride", "swim",
                "dance", "sing", "draw", "hug", "cry", "laugh",
                "wait", "my turn", "help"
            )
        ),

        SeedCategory(
            id = "food",
            name = "Food & Drink",
            icon = "🍎",
            tiles = tiles(
                "water", "milk", "juice", "coffee", "tea", "soda",
                "bread", "rice", "pasta", "cereal", "egg", "cheese",
                "meat", "chicken", "fish", "fruit", "apple", "banana",
                "orange", "vegetables", "salad", "soup", "sandwich",
                "pizza", "snack", "cookie", "candy", "ice cream",
                "breakfast", "lunch", "dinner", "hot", "cold"
            )
        ),

        SeedCategory(
            id = "feelings",
            name = "Feelings",
            icon = "😊",
            tiles = tiles(
                "happy", "sad", "angry", "scared", "tired", "sick",
                "hurt", "excited", "bored", "worried", "calm",
                "frustrated", "proud", "lonely", "loved", "confused",
                "embarrassed", "safe", "okay"
            )
        ),

        SeedCategory(
            id = "body",
            name = "Body",
            icon = "🖐",
            tiles = tiles(
                "head", "eyes", "ears", "nose", "mouth", "teeth",
                "hair", "hand", "arm", "leg", "foot", "stomach",
                "back", "heart", "skin", "throat", "ear hurts",
                "head hurts", "stomach hurts"
            )
        ),

        SeedCategory(
            id = "places",
            name = "Places",
            icon = "📍",
            tiles = tiles(
                "home", "school", "work", "hospital", "doctor",
                "store", "park", "bathroom", "bedroom", "kitchen",
                "living room", "car", "bus", "outside", "inside",
                "restaurant", "library", "church", "playground"
            )
        ),

        SeedCategory(
            id = "questions",
            name = "Questions",
            icon = "❓",
            tiles = listOf(
                Tile("what", "What?"),
                Tile("what time", "What time is it?"),
                Tile("where", "Where?"),
                Tile("when", "When?"),
                Tile("who", "Who?"),
                Tile("why", "Why?"),
                Tile("how", "How?"),
                Tile("how much", "How much?"),
                Tile("can I", "Can I?"),
                Tile("do you", "Do you?"),
                Tile("are we there yet", "Are we there yet?"),
                Tile("what happened", "What happened?"),
                Tile("what's wrong", "What's wrong?")
            )
        ),

        SeedCategory(
            id = "social",
            name = "Social",
            icon = "👋",
            tiles = listOf(
                Tile("hello", "Hello."),
                Tile("goodbye", "Goodbye."),
                Tile("how are you", "How are you?"),
                Tile("I'm fine", "I'm fine."),
                Tile("see you later", "See you later."),
                Tile("good morning", "Good morning."),
                Tile("good night", "Good night."),
                Tile("excuse me", "Excuse me."),
                Tile("you're welcome", "You're welcome."),
                Tile("bless you", "Bless you."),
                Tile("nice to meet you", "Nice to meet you."),
                Tile("I missed you", "I missed you.")
            )
        ),

        SeedCategory(
            id = "time",
            name = "Time",
            icon = "⏰",
            tiles = tiles(
                "now", "later", "today", "tomorrow", "yesterday",
                "morning", "afternoon", "evening", "night",
                "soon", "never", "always", "sometimes",
                "minute", "hour", "day", "week", "weekend",
                "month", "year", "birthday", "holiday"
            )
        ),

        SeedCategory(
            id = "describing",
            name = "Describing",
            icon = "🎨",
            tiles = tiles(
                "big", "small", "hot", "cold", "fast", "slow",
                "loud", "quiet", "hard", "soft", "clean", "dirty",
                "new", "old", "good", "bad", "pretty",
                "same", "different", "more", "less", "all", "none",
                "some", "red", "blue", "green", "yellow",
                "black", "white", "orange", "purple", "pink", "brown"
            )
        ),

        SeedCategory(
            id = "things",
            name = "Things",
            icon = "📦",
            tiles = tiles(
                "phone", "tablet", "TV", "book", "toy", "ball",
                "game", "music", "money", "keys", "clothes", "shoes",
                "jacket", "glasses", "medicine", "wheelchair",
                "blanket", "pillow", "chair", "table", "door", "light"
            )
        )
    )

    /** Total tiles across all categories. Used by tests and the About line. */
    fun tileCount(): Int = seed().sumOf { it.tiles.size }

    private fun tiles(vararg labels: String): List<Tile> = labels.map { Tile(it) }
}
