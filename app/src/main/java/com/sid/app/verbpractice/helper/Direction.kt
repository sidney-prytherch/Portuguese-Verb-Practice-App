package com.sid.app.verbpractice.helper

enum class Direction { NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;
    companion object {
        fun getDirectionalComponents(direction: Direction): Array<Direction> {
            return when (direction) {
                NORTH -> arrayOf(NORTH)
                NORTHEAST -> arrayOf(NORTH, EAST)
                EAST -> arrayOf(EAST)
                SOUTHEAST -> arrayOf(SOUTH, EAST)
                SOUTH -> arrayOf(SOUTH)
                SOUTHWEST -> arrayOf(SOUTH, WEST)
                WEST -> arrayOf(WEST)
                NORTHWEST -> arrayOf(WEST, NORTH)
            }
        }
        fun getOppositeDirection(direction: Direction): Direction {
            return when (direction) {
                NORTH -> SOUTH
                EAST -> WEST
                SOUTH -> NORTH
                WEST -> EAST
                NORTHEAST -> SOUTHWEST
                SOUTHEAST -> NORTHWEST
                SOUTHWEST -> NORTHEAST
                NORTHWEST -> SOUTHEAST
            }
        }
    }

}
