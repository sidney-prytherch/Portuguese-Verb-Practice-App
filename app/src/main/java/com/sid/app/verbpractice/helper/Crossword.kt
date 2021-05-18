package com.sid.app.verbpractice.helper

import android.content.res.Resources
import android.util.Log
import com.sid.app.verbpractice.enums.Person
import com.sid.app.verbpractice.enums.VerbForm
import java.util.*
import kotlin.random.Random

class Crossword(
    val size: Int,
    private val conjugationArrayParcel: ConjugationArrayParcel,
    private val enabledThirdPersons: BooleanArray,
    private val resources: Resources
) {
    private lateinit var wordStartCoords: Array<Triple<Int, Int, Boolean>>
    private lateinit var crosswordHints: Array<String>
    private lateinit var crosswordWords: Array<String>
    private lateinit var crosswordPtInfinitives: Array<String>
    private lateinit var crosswordEnTranslations: Array<String>
    val cw = Array(size) { i -> Array(size) { j -> Node(i, j) } }

    fun convertToCrosswordParcel(): CrosswordParcel {
        generateCrossword()
        val crosswordLetters = CharArray(size * size) { i -> cw[i / size][i % size].char }
        val acrossRoots = IntArray(size * size) { i -> cw[i / size][i % size].firstCoordInLine[Direction.EAST]!! }
        val downRoots = IntArray(size * size) { i -> cw[i / size][i % size].firstCoordInLine[Direction.SOUTH]!! }
        return CrosswordParcel(crosswordLetters, acrossRoots, downRoots, crosswordHints, crosswordWords, crosswordPtInfinitives, crosswordEnTranslations, wordStartCoords)
    }

    inner class SharedConjugationGroup(
        val ptInfinitive: String,
        val enVerb: String,
        val tense: VerbForm
    )

    inner class IndividualConjugation(
        val sharedConjugationGroup: SharedConjugationGroup,
        val ptVerb: String,
        val person: Person
    )

    private fun getEnglishHint(conjugation: IndividualConjugation): Array<String> {
        val enVerb = conjugation.sharedConjugationGroup.enVerb
        val tense = conjugation.sharedConjugationGroup.tense
        val person = conjugation.person
        val englishParts = enVerb.split("|")
        val englishConjugations = ConjugatorEnglish.conjugate(
            englishParts[6],
            tense,
            arrayOf(ConjugatorPortuguese.getSubject(person, enabledThirdPersons)),
            englishParts[5],
            englishParts[4],
            englishParts[1],
            englishParts[2],
            englishParts[3],
            true
        )
        return arrayOf(
            "Verb: ${englishParts[0]}\nTense: ${
                ConjugatorPortuguese.getVerbFormString(
                    tense,
                    resources
                )
            }", englishConjugations[0]
        )
    }



    private fun generateCrossword() {
        val coords = mutableListOf<Triple<Int, Int, Boolean>>()
        val placedVerbs = mutableListOf<String>()
        val hintList = mutableListOf<String>()
        val ptInfinitives = mutableListOf<String>()
        val enTranslations = mutableListOf<String>()

        fun addWord(
            cell: Node,
            conjugation: IndividualConjugation,
            direction: Direction
        ) {
            placedVerbs.add(conjugation.ptVerb)
            cell.setWord(direction, conjugation.ptVerb)
            val (englishHint1, englishTranslation1) = getEnglishHint(conjugation)
            coords.add(Triple(cell.row, cell.col, direction == Direction.EAST))
            hintList.add(englishHint1)
            enTranslations.add(englishTranslation1)
            ptInfinitives.add(conjugation.sharedConjugationGroup.ptInfinitive)
        }


        val conjugations = conjugationArrayParcel.conjugations.map {
            val sharedConjugationGroup = SharedConjugationGroup(it.verb, it.enVerb, it.tense)
            it.persons.mapIndexed { index, person ->
                IndividualConjugation(
                    sharedConjugationGroup,
                    (it.verbConjugations[index] ?: "").replace(" ", "").split("/")[0].toUpperCase(
                        Locale.ROOT
                    ),
                    person
                )
            }.filter { conj -> conj.ptVerb.length in 3..size }
        }.flatten().shuffled().toTypedArray()

//        words = conjugationArrayParcel.conjugations.map { it.verbConjugations.toSet() }.flatten()
//            .filterNotNull().toTypedArray()

//        words = arrayOf("AAB", "BCD", "CEE", "FGD", "GHH")

//        val verbs =
//            words.map { it.replace(" ", "").split("/")[0].toUpperCase(Locale.ROOT) }
//                .filter { it.length in 3..size }.shuffled()
//                .toTypedArray()
        val directions = arrayOf(Direction.SOUTH, Direction.EAST)
        val randomDirection = directions.random()

        addWord(cw[0][0], conjugations[0], randomDirection)

        addWord(
            if (randomDirection == Direction.SOUTH) {
                cw[size - conjugations[1].ptVerb.length][size - 1]
            } else {
                cw[size - 1][size - conjugations[1].ptVerb.length]
            },
            conjugations[1],
            randomDirection
        )

        var canContinue = true
        loopUntilNewWordPlaced@ while (canContinue) {
            directions.shuffle()
            val allSegments = getAllLineSegments()
            for (direction in directions) {
                val segments =
                    if (direction == Direction.SOUTH) allSegments.first else allSegments.second
//                val downSegments = mutableListOf<Triple<Int, Int, String>>()
//                val acrossSegments = mutableListOf<Triple<Int, Int, String>>()
//                Array(size) { i ->
//                    downSegments.add(Triple(0, i, cw[0][i].getNodeLineString(Direction.SOUTH)))
//                    acrossSegments.add(Triple(i, 0, cw[i][0].getNodeLineString(Direction.EAST)))
//                }
//                val segments =
//                    if (direction == Direction.SOUTH) downSegments.toTypedArray() else acrossSegments.toTypedArray()

                for (segment in segments) {
                    for (conjugation in conjugations) {
                        if (conjugation.ptVerb !in placedVerbs) {
                            val startIndex = wordFitsInSegment(conjugation.ptVerb, segment.third)
                            if (startIndex > -1) {
                                addWord(
                                    when (direction) {
                                        Direction.SOUTH -> {
                                            cw[segment.first + startIndex][segment.second]
                                        }
                                        else -> {
                                            cw[segment.first][segment.second + startIndex]
                                        }
                                    },
                                    conjugation,
                                    direction
                                )
                                continue@loopUntilNewWordPlaced
                            }
                        }
                    }
                }
            }
            canContinue = false
        }
        for (row in cw) {
            Log.v("crosswordTest", row.joinToString { it.char.toString() }.replace(",", ""))
        }

        crosswordHints = hintList.toTypedArray()
        crosswordWords = placedVerbs.toTypedArray()
        crosswordPtInfinitives = ptInfinitives.toTypedArray()
        crosswordEnTranslations = enTranslations.toTypedArray()
        wordStartCoords = coords.toTypedArray()

    }

    private fun wordFitsInSegment(word: String, segment: String): Int {
        val firstLetterIndex = segment.indexOfFirst { it != ' ' }
        val lastLetterIndex = segment.indexOfLast { it != ' ' }
        startPlaceLoop@ for (startIndex in segment.indices) {
            // if the word at this placement doesn't include the last letter, continue
            if (startIndex + word.length - 1 < lastLetterIndex) {
                continue
            }
            // if the word is too long for placement or if the placement doesn't include the first letter, end
            if (startIndex + word.length > segment.length || startIndex > firstLetterIndex) {
                return -1
            }
            for (index in word.indices) {
                val segmentLetter = segment[startIndex + index]
                if (segmentLetter != ' ' && segmentLetter != word[index]) {
                    continue@startPlaceLoop
                }
            }
            Log.v("wordPlaceFound", segment.replace(" ", " _ ") + "    - " + word)
            return startIndex

        }
        return -1
    }

    // _ _ L _ L _ _
    // F A L O _ _ _
    // _ F A L O _ _
    // _ _ F A L O _
    // _ _ _ F A L O

    private fun getAllLineSegments(): Pair<Array<Triple<Int, Int, String>>, Array<Triple<Int, Int, String>>> {
        val downSegments = mutableListOf<Triple<Int, Int, String>>()
        val acrossSegments = mutableListOf<Triple<Int, Int, String>>()
        Array(size) { i ->
            downSegments.addAll(getLineSegments(cw[0][i], Direction.SOUTH))
            acrossSegments.addAll(getLineSegments(cw[i][0], Direction.EAST))
        }
//        downSegments.forEach {
//            Log.v("wordPlacement", it.third.replace(" ", " _ "))
//        }
//        acrossSegments.forEach {
//            Log.v("wordPlacement", it.third.replace(" ", " _ "))
//        }

        //potential error here, with sortedByDescending: "Comparison method violates its general contract!"
        return Pair(
            downSegments.filter { it.third.length > 2 && it.third.count { letter -> letter == ' ' } < it.third.length }
                .sortedByDescending { it.third.count { letter -> letter != ' ' } + Random.nextDouble() }
                .toTypedArray(),
            acrossSegments.filter { it.third.length > 2 && it.third.count { letter -> letter == ' ' } < it.third.length }
                .sortedByDescending { it.third.count { letter -> letter != ' ' } + Random.nextDouble() }
                .toTypedArray()
        )
    }

    private fun getSubSegments(
        segment: Triple<Int, Int, String>,
        dir: Direction
    ): Array<Triple<Int, Int, String>> {
        Log.v("segments", segment.third)
        val allSubSegments = mutableListOf(segment)
        val letterIndices =
            segment.third.mapIndexed { index, c -> if (c != ' ') index else -1 }.filter { it != -1 }
        for (letterCount in 1 until letterIndices.size) {
            for (index in 0..letterIndices.size - letterCount) {
                val startIndex = if (index == 0) 0 else letterIndices[index - 1] + 2
                val endIndex =
                    if (index + letterCount == letterIndices.size) segment.third.length else letterIndices[index + letterCount] - 1
                when (dir) {
                    Direction.SOUTH -> allSubSegments.add(
                        Triple(
                            startIndex + segment.first,
                            segment.second,
                            segment.third.substring(startIndex, endIndex)
                        )
                    )
                    else -> allSubSegments.add(
                        Triple(
                            segment.first,
                            startIndex + segment.second,
                            segment.third.substring(startIndex, endIndex)
                        )
                    )
                }
            }
        }


        return allSubSegments.toTypedArray()
    }

    private fun getLineSegments(node: Node, dir: Direction): Array<Triple<Int, Int, String>> {
        val segments = mutableListOf<Triple<Int, Int, String>>()
        var segment = node.getLineSegment(dir, true)
        while (segment != null) {
            segments.addAll(getSubSegments(segment, dir))
            segment = when (dir) {
                Direction.SOUTH -> {
                    cw[segment.first + segment.third.length - 1][node.col].getLineSegment(
                        dir,
                        false
                    )
                }
                // A _ _ _ _
                else -> {
                    cw[node.row][segment.second + segment.third.length - 1].getLineSegment(
                        dir,
                        false
                    )
                }
            }
        }
        return segments.toTypedArray()
    }

    inner class Node(val row: Int, val col: Int) {
        private val legals = mutableMapOf(Direction.SOUTH to true, Direction.EAST to true)

        //        private val isPartOfWord = mutableMapOf(Direction.SOUTH to false, Direction.EAST to false)
        var char = ' '
        var letter: String = (col + 65).toChar().toString()
        var words = mutableListOf<String?>()
        val firstCoordInLine = mutableMapOf(Direction.SOUTH to -1, Direction.EAST to -1)

        fun getLineSegment(dir: Direction, includeCurrentNode: Boolean): Triple<Int, Int, String>? {
            val previousNode = getNextNode(Direction.getOppositeDirection(dir))
            val previousNodeIsEmpty = previousNode == null || previousNode.char == ' '
            val startNode =
                if (includeCurrentNode && legals[dir] == true && previousNodeIsEmpty) this else getNextLegalNode(
                    dir
                )
            return if (startNode == null) {
                null
            } else {
                Triple(startNode.row, startNode.col, startNode.getLastLegalNodeString(dir, ""))
            }
        }

        private fun getLastLegalNodeString(dir: Direction, string: String): String {
            if (legals[dir] == false) return string
            val currentString = string + char
            val nextNode = getNextNode(dir)
            return nextNode?.getLastLegalNodeString(dir, currentString) ?: currentString
        }

        private fun getNextLegalNode(dir: Direction): Node? {
            val nextNode = getNextNode(dir)
            return when {
                nextNode == null -> {
                    null
                }
                nextNode.legals[dir] == true && char == ' ' -> {
                    nextNode
                }
                else -> {
                    nextNode.getNextLegalNode(dir)
                }
            }
        }

        fun setWord(direction: Direction, word: String) {
            val endNode =
                setWordHelper(direction, word, 0, if (direction == Direction.EAST) col else row)
            val previousNode =
                getNextNode(if (direction == Direction.SOUTH) Direction.NORTH else Direction.WEST)
            arrayOf(previousNode, endNode.getNextNode(direction)).forEach {
                it?.legals?.set(Direction.SOUTH, false)
                it?.legals?.set(Direction.EAST, false)
            }
        }

        private fun setWordHelper(
            dir: Direction,
            word: String,
            currLetterIndex: Int,
            indexOfFirst: Int
        ): Node {
            char = word[currLetterIndex]
//            isPartOfWord[dir] = true
            firstCoordInLine[dir] = indexOfFirst
            when (dir) {
                Direction.SOUTH -> {
                    val sideNodes =
                        arrayOf(getNextNode(Direction.EAST), getNextNode(Direction.WEST))
                    sideNodes.forEach {
//                        if (it != null && it.isPartOfWord[Direction.EAST] == false) {
                        if (it != null && it.firstCoordInLine[Direction.EAST]!! == -1) {
                            it.legals[Direction.SOUTH] = false
                        }
                    }
//                    if (isPartOfWord[Direction.EAST] == false) {
                    if (firstCoordInLine[Direction.EAST] == -1) {
                        legals[Direction.EAST] = true
                    }
                }
                else -> {
                    val sideNodes =
                        arrayOf(getNextNode(Direction.NORTH), getNextNode(Direction.SOUTH))
                    sideNodes.forEach {
//                        if (it != null && it.isPartOfWord[Direction.SOUTH] == false) {
                        if (it != null && it.firstCoordInLine[Direction.SOUTH] == -1) {
                            it.legals[Direction.EAST] = false
                        }
                    }
//                    if (isPartOfWord[Direction.SOUTH] == false) {
                    if (firstCoordInLine[Direction.SOUTH] == -1) {
                        legals[Direction.SOUTH] = true
                    }
                }
            }
            this.legals[dir] = false
            val nextIndex = currLetterIndex + 1
            return if (nextIndex == word.length) {
                Log.v("endNode", word + " - " + nextIndex + " - " + this.row + ", " + this.col)
                this
            } else {
                getNextNode(dir)?.setWordHelper(dir, word, nextIndex, indexOfFirst) ?: this
            }
        }

        private fun getNodeLineStringHelper(line: String, direction: Direction): String {
            return getNextNode(direction)?.getNodeLineStringHelper(line + char, direction)
                ?: line + char
        }

        private fun getNextNode(direction: Direction): Node? {
            val coord = when (direction) {
                Direction.NORTH -> Coord(col, row - 1)
                Direction.NORTHEAST -> Coord(col + 1, row - 1)
                Direction.EAST -> Coord(col + 1, row)
                Direction.SOUTHEAST -> Coord(col + 1, row + 1)
                Direction.SOUTH -> Coord(col, row + 1)
                Direction.SOUTHWEST -> Coord(col - 1, row + 1)
                Direction.WEST -> Coord(col - 1, row)
                Direction.NORTHWEST -> Coord(col - 1, row - 1)
            }
            return if (coord.isLegal()) cw[coord.r][coord.c] else null
        }
    }

    inner class Coord(val c: Int, val r: Int) {
        fun isLegal(): Boolean {
            return !(c > size - 1 || c < 0 || r > size - 1 || r < 0)
        }
    }

}