package com.sid.app.verbpractice.helper

import android.content.res.Resources
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import com.sid.app.verbpractice.enums.Person

class Wordsearch(val size: Int, conjugationArrayParcel: ConjugationArrayParcel, private val enabledThirdPersons: BooleanArray, private val resources: Resources) {

    private lateinit var wordsearchHints: Array<String>
    private lateinit var wordsearchWords: Array<String>
    private lateinit var wordsearchPtInfinitives: Array<String>
    private lateinit var wordsearchEnTranslations: Array<String>
    private lateinit var wordsearchCoordinates: IntArray
    private lateinit var wordsearchLetters: CharArray
    private val maxWordLength = 3
    private val maxFailedWordLimit = 10
    private val allPersons = arrayOf(Person.FIRST_SING, Person.SECOND_SING, Person.THIRD_SING, Person.FIRST_PLUR, Person.SECOND_PLUR, Person.THIRD_PLUR)
    private val conjugations = conjugationArrayParcel.conjugations.map { conjugationParcel ->
        Conjugation(conjugationParcel)
    }
//        .map {
//        conjugation -> arrayOf((conjugation.personMap[allPersons[conjugation.person]] ?: "").replace(" ", "").split("/")[0].toUpperCase(), conjugation.enVerb)
//    }.filter { c -> c[0].length in maxWordLength..size }.toTypedArray()


    val ws = Array(size) { r -> Array(size) { c -> Node(r, c) } } // row, column

    val lines: Map<Direction, Array<Array<Node>>> = mapOf(
        Direction.NORTH to Array(size) {x -> ws[size - 1][x]},
        Direction.NORTHEAST to Array(size * 2 - 3) {n -> if (n < size - 2) ws[n + 1][0] else ws[size - 1][n - (size - 2)]},
        Direction.EAST to Array(size) {y -> ws[y][0]},
        Direction.SOUTHEAST to Array(size * 2 - 3) {n -> if (n < size - 2) ws[0][size - 2 - n] else ws[n - (size - 2)][0]},
        Direction.SOUTH to Array(size) {x -> ws[0][x]},
        Direction.SOUTHWEST to Array(size * 2 - 3) {n -> if (n < size - 2) ws[size - 2 - n][size - 1] else ws[0][size * 2 - 3 - n]},
        Direction.WEST to Array(size) {y -> ws[y][size - 1]},
        Direction.NORTHWEST to Array(size * 2 - 3) {n -> if (n < size - 2) ws[size - 1][n + 1] else ws[size * 2 - 3 - n][size - 1]}
    ).mapValues { (direction, startNodesArray) ->
        startNodesArray.map { startNode -> startNode.getNodeLine(direction) }.toTypedArray()
    }

    private val wordCountOfDirection: MutableMap<Direction, Int> = mutableMapOf(
        Direction.NORTH to 0,
        Direction.NORTHEAST to 0,
        Direction.EAST to 0,
        Direction.SOUTHEAST to 0,
        Direction.SOUTH to 0,
        Direction.SOUTHWEST to 0,
        Direction.WEST to 0,
        Direction.NORTHWEST to 0
    )

    fun nodesFormWord(col1: Int, row1: Int, row2: Int, col2: Int): Boolean {
        return ws[row1][col1].isRelatedTo(ws[row2][col2])
    }

    private fun addWord(direction: Direction) {
        wordCountOfDirection[direction] = wordCountOfDirection[direction]?.plus(1) ?: 1
    }

    fun getLineString(nodes: Array<Node>): String {
        return nodes.joinToString { node -> node.letter }
    }

    inner class Coord(val c: Int, val r: Int) {
        fun isLegal(): Boolean {
            return !(c > size - 1 || c < 0 || r > size - 1 || r < 0)
        }
    }

    private fun getEnglishHint(conjugation: Conjugation): Array<String> {
        val englishParts = conjugation.enVerb.split("|")
        val englishConjugations = ConjugatorEnglish.conjugate(
            englishParts[6],
            conjugation.tense,
            arrayOf(ConjugatorPortuguese.getSubject(conjugation.person, enabledThirdPersons)),
            englishParts[5],
            englishParts[4],
            englishParts[1],
            englishParts[2],
            englishParts[3],
            true
        )
        return arrayOf("Verb: ${englishParts[0]}\nTense: ${ConjugatorPortuguese.getVerbFormString(conjugation.tense, resources)}", englishConjugations[0])
    }

    private fun generateWordsearch() {
        //Log.v("wordsearch", "???????????????????????" )
        //conjugations.forEach { conjugation -> Log.v("wordsearch", conjugation[0]) }
        var errorCount = 0
        val wordList = mutableListOf<String>()
        val hintList = mutableListOf<String>()
        val ptInfinitives = mutableListOf<String>()
        val enTranslations = mutableListOf<String>()
        val wordCoordinates = mutableListOf<Int>()
        val orderedDirections = mapOf(
            Direction.NORTH to 0,
            Direction.NORTHEAST to 1,
            Direction.EAST to 2,
            Direction.SOUTHEAST to 3,
            Direction.SOUTH to 4,
            Direction.SOUTHWEST to 5,
            Direction.WEST to 6,
            Direction.NORTHWEST to 7
        )
        val commonDirections = arrayOf(Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH).map { orderedDirections[it]!! }.toTypedArray()
        val uncommonDirections = arrayOf(Direction.NORTHEAST, Direction.SOUTHWEST).map { orderedDirections[it]!! }.toTypedArray()
        val rareDirections = arrayOf(Direction.NORTH, Direction.WEST).map { orderedDirections[it]!! }.toTypedArray()
        // val mythicRareDirections = arrayOf(Direction.NORTHWEST).map { orderedDirections[it]!! }.toTypedArray()
        val directionsCount = IntArray(8) {0}
        val directions = arrayOf(Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST)

        wordLoop@ for (conjugation in conjugations) {
            val ptWord = (conjugation.personMap[allPersons[conjugation.person]] ?: "").replace(" ", "").split("/")[0].toUpperCase()
            if (ptWord.length !in maxWordLength..size) continue
        //Log.v("wordsearch", "new word:" + conjugation[0]  )
            // if the word is invalid, continue (if it contains or is contained by an already used word)
            for (word in wordList) {
                if (word.contains(ptWord) || ptWord.contains(word) || word.contains(ptWord.reversed()) || ptWord.contains(word.reversed()) ) {
                    continue@wordLoop
                }
            }
            val sortedDirections = directionsCount.mapIndexed { i, count ->
                count * when (i) {
                    in commonDirections -> .7
                    in uncommonDirections -> .9
                    in rareDirections -> 1.1
                    else -> 1.3
                } + (Math.random() / 100) to i
            }.sortedBy { it.first }.map { directions[it.second] }
            // directions.shuffle()
            // this makes it slightly more likely that the word will be placed in a natural reading direction
//            if (directions[0] != Direction.SOUTHEAST && directions[0] != Direction.EAST && directions[0] != Direction.SOUTH) {
//                directions.shuffle()
//            }
            var wordPlaced = false
            for (direction in sortedDirections) {
                //Log.v("wordsearch", "new direction: $direction word: ${conjugation[0]}")
                val dirElems = Direction.getDirectionalComponents(direction)
                //calculate array of start ranges in x and y coords
                val validRangeLength = size - ptWord.length
                val (rowMin, rowMax) = when {
                    dirElems.contains(Direction.NORTH) -> {
                        arrayOf(size - validRangeLength - 1, size)
                    }
                    dirElems.contains(Direction.SOUTH) -> {
                        arrayOf(0, validRangeLength + 1)
                    }
                    else -> {
                        arrayOf(0, size)
                    }
                }
                val (colMin, colMax) = when {
                    dirElems.contains(Direction.WEST) -> {
                        arrayOf(size - validRangeLength - 1, size)
                    }
                    dirElems.contains(Direction.EAST) -> {
                        arrayOf(0, validRangeLength + 1)
                    }
                    else -> {
                        arrayOf(0, size)
                    }
                }
                val validStartLocations = Array(rowMax - rowMin) { row ->
                    Array(colMax - colMin) { col ->
                        intArrayOf(row + rowMin, col + colMin)
                    }
                }.flatten().toTypedArray()
                //randomize locations array
                validStartLocations.shuffle()
                locationLoop@ for ((row, col) in validStartLocations) {
                    //Log.v("wordsearch", "new location: ($row, $col) \t word: ${conjugation[0]} \t direction: $direction")
                    //if word doesn't fit with existing letters, goto new location
                    if (!ws[row][col].lineFitsWord(direction, ptWord)) {
                        //Log.v("wordsearch", "NEXT LOCATION (1)")
                        continue@locationLoop
                    }
                    //change the nodes' letters
                    val changedNodes = mutableListOf<Node>()
                    //ws[row][col].getNodeLineUntil(direction, conjugation[0].length).forEach { Log.v("wordsearch debug", it.letter + "," + it.row) }
                    val endNode = ws[row][col].getNodeAfterDistance(direction, ptWord.length)
                    ws[row][col].getNodeLineUntil(direction, ptWord.length).forEachIndexed { nodeIndex, node ->
                        if (node.char == ' ') {
                            changedNodes.add(node)
                        }
                        node.char = ptWord[nodeIndex]
                    }
                    //for each row/column/diagonal or wordsearch as String, ensure each word appears only once
                    wordList.add(ptWord)
                    val wordsFoundInWordsearch = IntArray(wordList.size) {0}
                    for (line in getLinesOfWordsearch()) {
                        for (wordIndex in 0 until wordList.size) {
                            val word = wordList[wordIndex]
                            val wordIsPalindrome = word == word.reversed()
                            val wordStartIndex = line.indexOf(word)
                            if (wordStartIndex > -1) {
                                wordsFoundInWordsearch[wordIndex]++
                            }
                            // if the word is found in the same line twice, or if it has been now found more than once in the whole wordsearch, find a new location
                            if ((wordStartIndex > -1 && line.indexOf(word, wordStartIndex + 1) > -1) || wordsFoundInWordsearch[wordIndex] > (if (wordIsPalindrome) 2 else 1)) {
//                                Log.v(
//                                    "wordsearch",
//                                    "This word was found in 2 places: $word including in the line $line"
//                                )
                                //before continuing, change the letters back
                                changedNodes.forEach { node -> node.char = ' ' }
                                //Log.v("wordsearch", "NEXT LOCATION (2)")
                                wordList.removeLast()
                                continue@locationLoop
                            }
                        }
                    }
                    // if here has been reached, a valid location has been found - add the word, and stop the loop from finding more locations
                    wordPlaced = true
                    val (englishHint, englishTranslation) = getEnglishHint(conjugation)
                    hintList.add(englishHint)
                    enTranslations.add(englishTranslation)
                    ptInfinitives.add("Verb: ${conjugation.verb}")
                    directionsCount[orderedDirections[direction]!!]++
                    wordCoordinates.add(row)
                    wordCoordinates.add(col)
                    wordCoordinates.add(endNode.row)
                    wordCoordinates.add(endNode.col)
                    break
                }
                if (wordPlaced) {
                    continue@wordLoop
                }
                    //if placed, break, goto new word
                //if not placed, errorCount++
                //if errorCount > 4?, break
            }
            if (!wordPlaced) {
                errorCount++
//                if (errorCount >= maxFailedWordLimit) {
//                    break@wordLoop
//                }
            }
        }
        for (row in ws) {
            for (node in row) {
                node.letter = node.char.toString()
                ws[0][0].hints = hintList.toMutableList()
                ws[0][0].words = wordList.toMutableList()
                ws[0][0].ptInfinitives = ptInfinitives.toMutableList()
                ws[0][0].enTranslations = enTranslations.toMutableList()
                wordsearchHints = hintList.toTypedArray()
                wordsearchWords = wordList.toTypedArray()
                wordsearchPtInfinitives = ptInfinitives.toTypedArray()
                wordsearchEnTranslations = enTranslations.toTypedArray()
                wordsearchCoordinates = wordCoordinates.toIntArray()
                wordsearchLetters = CharArray(size * size) {i -> ws[i / size][i % size].letter[0]}
            }
        }
        Log.v("wordsearch word", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        for (word in wordList) {
            Log.v("wordsearch word", word)
        }
    }

    private fun getLinesOfWordsearch(): Array<String> {
        Log.v("lineTest", "hello?")
        val a = arrayOf(
            Array(size) { i ->
                arrayOf(
                    ws[0][i].getNodeLineString(Direction.SOUTH),
                    ws[i][size - 1].getNodeLineString(Direction.WEST),
                    ws[size - 1][i].getNodeLineString(Direction.NORTH),
                    ws[i][0].getNodeLineString(Direction.EAST)
                )
            },
            Array(size - 1) { i ->
                arrayOf(
                    ws[size - 2 - i][0].getNodeLineString(Direction.SOUTHEAST),
                    ws[0][i + 1].getNodeLineString(Direction.SOUTHWEST),
                    ws[i + 1][size - 1].getNodeLineString(Direction.NORTHWEST),
                    ws[size - 1][size - 2 - i].getNodeLineString(Direction.NORTHEAST)
                )
            },
            Array(size - 2) { i ->
                arrayOf(
                    ws[0][i + 1].getNodeLineString(Direction.SOUTHEAST),
                    ws[i + 1][size - 1].getNodeLineString(Direction.SOUTHWEST),
                    ws[size - 1][size - 2 - i].getNodeLineString(Direction.NORTHWEST),
                    ws[size - 2 - i][0].getNodeLineString(Direction.NORTHEAST)
                )
            }
        ).map { it.flatten() }.flatten().filter { line -> line.length >= maxWordLength }.toTypedArray()
        for (thing in a) {
            Log.v("lineTest", thing)
        }

        return a

//        return Array(size - 1) { i ->
//            arrayOf(ws[0][i].getNodeLineString(Direction.SOUTH),
//            ws[i][size - 1].getNodeLineString(Direction.WEST),
//            ws[size - 1][i].getNodeLineString(Direction.NORTH),
//            ws[i][0].getNodeLineString(Direction.EAST),
//            ws[0][i].getNodeLineString(Direction.SOUTHWEST),
//            ws[i][size - 1].getNodeLineString(Direction.NORTHWEST),
//            ws[size - 1][i].getNodeLineString(Direction.NORTHEAST),
//            ws[i][0].getNodeLineString(Direction.SOUTHEAST),
//            ws[0][i].getNodeLineString(Direction.SOUTHEAST),
//            ws[i][size - 1].getNodeLineString(Direction.SOUTHWEST),
//            ws[size - 1][i].getNodeLineString(Direction.NORTHWEST),
//            ws[i][0].getNodeLineString(Direction.NORTHEAST))
//        }.flatten().filter { line -> line.length >= maxWordLength }.toTypedArray()
    }

    fun convertToWordsearchParcel(): WordsearchParcel {
        generateWordsearch()

        return WordsearchParcel(
            wordsearchHints,
            wordsearchWords,
            wordsearchPtInfinitives,
            wordsearchEnTranslations,
            wordsearchCoordinates,
            wordsearchLetters
        )

//        conjugations.forEach { Log.v("potWord", it[0] + " ~ " + it[1]) }
//        return WordsearchParcel(ws.map { row ->
//            row.map { cell ->
//                WordsearchCellParcel(
//                    cell.letter.toCharArray()[0],
//                    cell.orientations.toTypedArray(),
//                    cell.getRelatedNodeArray(),
//                    cell.hints.toTypedArray(),
//                    cell.words.toTypedArray(),
//                    cell.ptInfinitives.toTypedArray(),
//                    cell.enTranslations.toTypedArray()
//                )
//            }.toTypedArray()
//        }.toTypedArray())
    }

    inner class Node(val row: Int, val col: Int) {
        var char = ' '
        var letter: String = (col + 65).toChar().toString()
        private var relatedNodes = mutableListOf<Node>()
        var hints = mutableListOf<String?>()
        var words = mutableListOf<String?>()
        var ptInfinitives = mutableListOf<String?>()
        var enTranslations = mutableListOf<String?>()
        var orientations = mutableListOf<Direction>()

        fun lineFitsWord(direction: Direction, word: String): Boolean {
            return lineFitsWordHelper(direction, word, 0)
        }

        private fun lineFitsWordHelper(direction: Direction, word: String, position: Int): Boolean {
            return (char == ' ' || word[position] == char) &&
                    (position == word.length - 1 || getNextNode(direction)?.lineFitsWordHelper(direction, word, position + 1) ?: true)
        }

        fun getNodeLineString(direction: Direction): String {
            return getNodeLineStringHelper("", direction)
        }

        private fun getNodeLineStringHelper(line: String, direction: Direction): String {
            return getNextNode(direction)?.getNodeLineStringHelper(line + char, direction) ?: line + char
        }

        fun getNodeLine(direction: Direction): Array<Node> {
            return getNodeLineHelper(mutableListOf(), direction)
        }

        private fun getNodeLineHelper(nodeList: MutableList<Node>, direction: Direction): Array<Node> {
            nodeList.add(this)
            return getNextNode(direction)?.getNodeLineHelper(nodeList, direction) ?: nodeList.toTypedArray()
        }

        fun getNodeAfterDistance(direction: Direction, length: Int): Node {
            if (length == 1) {
                return this
            }
            return getNextNode(direction)?.getNodeAfterDistance(direction, length - 1) ?: this
        }

        fun getNodeLineUntil(direction: Direction, length: Int): Array<Node> {
            return getNodeLineUntilHelper(mutableListOf(), direction, length)
        }

        private fun getNodeLineUntilHelper(nodeList: MutableList<Node>, direction: Direction, length: Int): Array<Node> {
            nodeList.add(this)
            if (length == 1) {
                return nodeList.toTypedArray()
            }
            return getNextNode(direction)?.getNodeLineUntilHelper(nodeList, direction, length - 1) ?: nodeList.toTypedArray()
        }

        fun getRelatedNodeArray(): Array<Array<Int>> {
            return relatedNodes.map { node ->
                arrayOf(node.row, node.col)
            }.toTypedArray()
        }

        fun isRelatedTo(node: Node): Boolean {
            return relatedNodes.contains(node)
        }

        private fun getNextNode(direction: Direction): Node? {
            val coord = when (direction) {
                Direction.NORTH -> Coord(col, row - 1)
                Direction.NORTHEAST -> Coord(col + 1, row - 1)
                Direction.EAST -> Coord(col + 1, row)
                Direction.SOUTHEAST -> Coord(col + 1, row + 1)
                Direction.SOUTH ->Coord(col, row + 1)
                Direction.SOUTHWEST -> Coord(col - 1, row + 1)
                Direction.WEST -> Coord(col - 1, row)
                Direction.NORTHWEST -> Coord(col - 1, row - 1)
            }
            return if (coord.isLegal()) ws[coord.r][coord.c] else null
        }
    }
}