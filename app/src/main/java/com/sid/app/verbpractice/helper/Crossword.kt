package com.sid.app.verbpractice.helper

import android.util.Log
import java.util.*
import kotlin.random.Random

class Crossword(
    private val words: Array<String>
) {

    val size = 12
    val cw = Array(size) { i -> Array(size) { j -> Node(i, j) } }

    fun generateCrossword() {
        val placedVerbs = mutableListOf<String>()
        val verbs =
            words.map { it.replace(" ", "").split("/")[0].toUpperCase(Locale.ROOT) }
                .filter { it.length in 3..size }.sortedByDescending { it.length }.toTypedArray()
        val directions = arrayOf(Direction.SOUTH, Direction.EAST)
        val randomWord = verbs.random()
        placedVerbs.add(randomWord)
        cw[0][0].setWord(directions.random(), randomWord)
        var canContinue = true
        loopUntilNewWordPlaced@ while (canContinue) {
            directions.shuffle()
            val allSegments = getAllLineSegments()
            for (direction in directions) {
                val segments =
                    if (direction == Direction.SOUTH) allSegments.first else allSegments.second
                for (segment in segments) {
                    for (word in verbs) {
                        if (word !in placedVerbs) {
                            val startIndex = wordFitsInSegment(word, segment.third)
                            if (startIndex > -1) {
                                placedVerbs.add(word)
                                when (direction) {
                                    Direction.SOUTH -> {
                                        cw[segment.first + startIndex][segment.second]
                                    }
                                    else -> {
                                        cw[segment.first][segment.second + startIndex]
                                    }
                                }.setWord(direction, word)
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


        // place word randomly
        // get list of all segments of lines in which something can be placed (can't be next to a letter of same orientation),
        // ordered by number of letters already in the segment
        // go through words until a word that can be placed is found
        // place word, repeat
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
        return Pair(
            downSegments.filter { it.third.length > 2 && it.third.count { letter -> letter == ' ' } < it.third.length }
                .sortedByDescending { it.third.count { letter -> letter != ' ' } + Random.nextDouble() }
                .toTypedArray(),
            acrossSegments.filter { it.third.length > 2 && it.third.count { letter -> letter == ' ' } < it.third.length }
                .sortedByDescending { it.third.count { letter -> letter != ' ' } + Random.nextDouble() }
                .toTypedArray()
        )
    }

    private fun getSubSegments(segment: Triple<Int, Int, String>, dir: Direction): Array<Triple<Int, Int, String>> {
        Log.v("segments", segment.third)
        val allSubSegments = mutableListOf(segment)
        val letterIndices =
            segment.third.mapIndexed { index, c -> if (c != ' ') index else -1 }.filter { it != -1 }
        for (letterCount in 1 until letterIndices.size) {
            for (index in 0..letterIndices.size-letterCount) {
                val startIndex = if (index == 0) 0 else letterIndices[index - 1] + 2
                val endIndex =
                    if (index + letterCount == letterIndices.size) segment.third.length else letterIndices[index + letterCount] - 2
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
        var char = ' '
        var letter: String = (col + 65).toChar().toString()
        var words = mutableListOf<String?>()

        fun getLineSegment(dir: Direction, includeCurrentNode: Boolean): Triple<Int, Int, String>? {
            val startNode =
                if (includeCurrentNode && legals[dir] == true) this else getNextLegalNode(dir)
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
                nextNode.legals[dir] == true -> {
                    nextNode
                }
                else -> {
                    nextNode.getNextLegalNode(dir)
                }
            }
        }

        fun setWord(direction: Direction, word: String) {
            val endNode = setWordHelper(direction, word, 0)
            val previousNode = getNextNode(if (direction == Direction.SOUTH) Direction.NORTH else Direction.WEST)
            arrayOf(previousNode, endNode.getNextNode(direction)).forEach {
                it?.legals?.set(Direction.SOUTH, false)
                it?.legals?.set(Direction.EAST, false)
            }
        }

        private fun setWordHelper(dir: Direction, word: String, currLetterIndex: Int): Node {
            char = word[currLetterIndex]
            when (dir) {
                Direction.SOUTH -> {
                    getNextNode(Direction.EAST)?.legals?.set(Direction.SOUTH, false)
                    getNextNode(Direction.WEST)?.legals?.set(Direction.SOUTH, false)
                }
                else -> {
                    getNextNode(Direction.SOUTH)?.legals?.set(Direction.EAST, false)
                    getNextNode(Direction.NORTH)?.legals?.set(Direction.EAST, false)
                }
            }
            this.legals[dir] = false
            val nextIndex = currLetterIndex + 1
            return if (nextIndex == word.length) {
                this
            } else {
                getNextNode(dir)?.setWordHelper(dir, word, nextIndex) ?: this
            }
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