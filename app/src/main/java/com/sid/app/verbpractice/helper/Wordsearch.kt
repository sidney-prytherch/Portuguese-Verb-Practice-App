package com.sid.app.verbpractice.helper

import android.util.Log
import com.sid.app.verbpractice.enums.Person

class Wordsearch(val size: Int, private val conjugationArrayParcel: ConjugationArrayParcel) {

    private val allPersons = arrayOf(Person.FIRST_SING, Person.SECOND_SING, Person.THIRD_SING, Person.FIRST_PLUR, Person.SECOND_PLUR, Person.THIRD_PLUR)
    private val conjugations = conjugationArrayParcel.conjugations.map { conjugationParcel ->
        Conjugation(conjugationParcel)
    }.map {
        conjugation -> arrayOf(conjugation.personMap[allPersons[conjugation.person]] ?: "", conjugation.enVerb)
    }.toTypedArray()


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

    fun convertToWordsearchParcel(): WordsearchParcel {

        conjugations.forEach { Log.v("potWord", it[0] + " ~ " + it[1]) }
        return WordsearchParcel(ws.map { row ->
            row.map { cell ->
                WordsearchCellParcel(
                    cell.letter.toCharArray()[0],
                    cell.orientations.toTypedArray(),
                    cell.getRelatedNodeArray(),
                    cell.hints.toTypedArray()
                )
            }.toTypedArray()
        }.toTypedArray())
    }

    inner class Node(private val row: Int, private val col: Int) {
        var letter: String = (col + 65).toChar().toString()
        private var relatedNodes = mutableListOf<Node>()
        var hints = mutableListOf<String?>()
        var orientations = mutableListOf<Direction>()

        fun getNodeLine(direction: Direction): Array<Node> {
            return getNodeLineHelper(mutableListOf(), direction)
        }

        private fun getNodeLineHelper(nodeList: MutableList<Node>, direction: Direction): Array<Node> {
            nodeList.add(this)
            return getNextNode(direction)?.getNodeLineHelper(nodeList, direction) ?: nodeList.toTypedArray()
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