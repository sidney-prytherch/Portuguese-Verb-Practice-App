package com.sid.app.verbpractice.helper

enum class Direction { NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;
    companion object {
        fun getDirectionalComponents(direction: Direction): Array<Direction> {
            return when (direction) {
                Direction.NORTH -> arrayOf(Direction.NORTH)
                Direction.NORTHEAST -> arrayOf(Direction.NORTH, Direction.EAST)
                Direction.EAST -> arrayOf(Direction.EAST)
                Direction.SOUTHEAST -> arrayOf(Direction.SOUTH, Direction.EAST)
                Direction.SOUTH -> arrayOf(Direction.SOUTH)
                Direction.SOUTHWEST -> arrayOf(Direction.SOUTH, Direction.WEST)
                Direction.WEST -> arrayOf(Direction.WEST)
                Direction.NORTHWEST -> arrayOf(Direction.WEST, Direction.NORTH)
            }
        }
    }

}
