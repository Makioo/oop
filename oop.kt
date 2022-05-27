class ChessGame {

    sealed class Piece {
        object King : Piece()
        object Queen : Piece()
        object Bishop : Piece()
        object Knight : Piece()
        object Rook : Piece()
        data class Pawn(var hasStartingPosition: Boolean = true) : Piece()
    }

    enum class Player {
        Black, White
    }
data class Vector(val x: Int, val y: Int) {
    companion object {
        val perpendicular = listOf(Vector(-1, 0), Vector(+1, 0), Vector(0, -1), Vector(0, +1))
        val diagonal = listOf(Vector(-1, -1), Vector(+1, +1), Vector(-1, +1), Vector(+1, -1))
      
    }
}
data class Point(val x: Int, val y: Int) {
    operator fun plus(vector: Vector) = Point(x + vector.x, y + vector.y)

    fun isValid() = x in 0..7 && y in 0..7
}

    data class PlayerPiece(val piece: Piece? = null, val player: Player? = null)

    var currentPlayer: Player = Player.White

    private var playingFieldArray = getStartingPositions()
    val playingField: Array<Array<PlayerPiece>> = playingFieldArray

    private fun getStartingPositions() = arrayOf(
        arrayOf(
            PlayerPiece(Piece.Rook, Player.Black),
            PlayerPiece(Piece.Knight, Player.Black),
            PlayerPiece(Piece.Bishop, Player.Black),
            PlayerPiece(Piece.Queen, Player.Black),
            PlayerPiece(Piece.King, Player.Black),
            PlayerPiece(Piece.Bishop, Player.Black),
            PlayerPiece(Piece.Knight, Player.Black),
            PlayerPiece(Piece.Rook, Player.Black),
        ),
        arrayOf(
            PlayerPiece(Piece.Pawn(), Player.Black),
            PlayerPiece(Piece.Pawn(), Player.Black),
            PlayerPiece(Piece.Pawn(), Player.Black),
            PlayerPiece(Piece.Pawn(), Player.Black),
            PlayerPiece(Piece.Pawn(), Player.Black),
            PlayerPiece(Piece.Pawn(), Player.Black),
            PlayerPiece(Piece.Pawn(), Player.Black),
            PlayerPiece(Piece.Pawn(), Player.Black)
        ),
        Array(8) { PlayerPiece() },
        Array(8) { PlayerPiece() },
        Array(8) { PlayerPiece() },
        Array(8) { PlayerPiece() },
        arrayOf(
            PlayerPiece(Piece.Pawn(), Player.White),
            PlayerPiece(Piece.Pawn(), Player.White),
            PlayerPiece(Piece.Pawn(), Player.White),
            PlayerPiece(Piece.Pawn(), Player.White),
            PlayerPiece(Piece.Pawn(), Player.White),
            PlayerPiece(Piece.Pawn(), Player.White),
            PlayerPiece(Piece.Pawn(), Player.White),
            PlayerPiece(Piece.Pawn(), Player.White)
        ),
        arrayOf(
            PlayerPiece(Piece.Rook, Player.White),
            PlayerPiece(Piece.Knight, Player.White),
            PlayerPiece(Piece.Bishop, Player.White),
            PlayerPiece(Piece.Queen, Player.White),
            PlayerPiece(Piece.King, Player.White),
            PlayerPiece(Piece.Bishop, Player.White),
            PlayerPiece(Piece.Knight, Player.White),
            PlayerPiece(Piece.Rook, Player.White)
        ),
    )

    private var removedPiecesList = mutableListOf<PlayerPiece>()
    val removedPieces: List<PlayerPiece> = removedPiecesList

    fun getAvailableMoves(x: Int, y: Int): List<Point> {
        val field = playingFieldArray[x][y]
        if (field.player != currentPlayer || isGameOver()) {
            return emptyList()
        }

        val availableMoves = mutableListOf<Point>()

        fun isValidPosition(x: Int, y: Int) = x in 0..7 && y in 0..7 && !tileHasPieceOfCurrentPlayer(x, y)

        if (field.piece == Piece.Rook || field.piece == Piece.Queen) {
            var toXUp = x - 1
            val toYUp = y
            while (isValidPosition(toXUp, toYUp)
                && !tileHasPieceOfCurrentPlayer(toXUp, toYUp)
            ) {
                availableMoves.add(Point(toXUp, toYUp))
                if (tileHasPieceOfOpponent(toXUp, toYUp)) break
                toXUp--
            }
            var toXDown = x + 1
            val toYDown = y
            while (isValidPosition(toXDown, toYDown)
                && !tileHasPieceOfCurrentPlayer(toXDown, toYDown)
            ) {
                availableMoves.add(Point(toXDown, toYDown))
                if (tileHasPieceOfOpponent(toXDown, toYDown)) break
                toXDown++
            }
            val toXLeft = x
            var toYLeft = y - 1
            while (isValidPosition(toXLeft, toYLeft)
                && !tileHasPieceOfCurrentPlayer(toXLeft, toYLeft)
            ) {
                availableMoves.add(Point(toXLeft, toYLeft))
                if (tileHasPieceOfOpponent(toXLeft, toYLeft)) break
                toYLeft--
            }
            val toXRight = x
            var toYRight = y + 1
            while (isValidPosition(toXRight, toYRight)
                && !tileHasPieceOfCurrentPlayer(toXRight, toYRight)
            ) {
                availableMoves.add(Point(toXRight, toYRight))
                if (tileHasPieceOfOpponent(toXRight, toYRight)) break
                toYRight++
            }
        }

        if (field.piece == Piece.Knight) {
            listOf(
                Point(x - 2, y - 1), Point(x - 2, y + 1), Point(x + 2, y - 1), Point(x + 2, y + 1),
                Point(x - 1, y - 2), Point(x - 1, y + 2), Point(x + 1, y - 2), Point(x + 1, y + 2)
            ).forEach { point ->
                if (isValidPosition(point.x, point.y)) {
                    availableMoves.add(point)
                }
            }
        }

        if (field.piece == Piece.King) {
            listOf(
                Point(x - 1, y), Point(x + 1, y), Point(x, y - 1), Point(x, y + 1), Point(x - 1, y - 1),
                Point(x - 1, y + 1), Point(x + 1, y - 1), Point(x + 1, y + 1)
            ).forEach { point ->
                if (isValidPosition(point.x, point.y)) {
                    availableMoves.add(point)
                }
            }
        }

        if (field.piece is Piece.Pawn) {
            if (field.player == Player.Black) {
                val toXDown = x + 1
                val toYDown = y
                if (isValidPosition(toXDown, toYDown) && !tileHasPieceOfOpponent(toXDown, toYDown)) {
                    availableMoves.add(Point(toXDown, toYDown))
                }

                if (field.piece.hasStartingPosition) {
                    val toXDown2 = x + 2
                    val toYDown2 = y
                    if (isValidPosition(toXDown2, toYDown2) && !tileHasPieceOfOpponent(toXDown2, toYDown2)) {
                        availableMoves.add(Point(toXDown2, toYDown2))
                    }
                }

                listOf(
                    Point(x + 1, y + 1), Point(x + 1, y - 1)
                ).forEach { point ->
                    if (isValidPosition(point.x, point.y)
                        && tileHasPieceOfOpponent(point.x, point.y)
                    ) {
                        availableMoves.add(point)
                    }
                }
            } else {
                val toXUp = x - 1
                val toYUp = y
                if (isValidPosition(toXUp, toYUp) && !tileHasPieceOfOpponent(toXUp, toYUp)) {
                    availableMoves.add(Point(toXUp, toYUp))
                }

                if (field.piece.hasStartingPosition) {
                    val toXDown2 = x - 2
                    val toYDown2 = y
                    if (isValidPosition(toXDown2, toYDown2) && !tileHasPieceOfOpponent(toXDown2, toYDown2)) {
                        availableMoves.add(Point(toXDown2, toYDown2))
                    }
                }

                listOf(
                    Point(x - 1, y + 1), Point(x - 1, y - 1)
                ).forEach { point ->
                    if (isValidPosition(point.x, point.y)
                        && tileHasPieceOfOpponent(point.x, point.y)
                    ) {
                        availableMoves.add(point)
                    }
                }
            }
        }

        if (field.piece == Piece.Bishop || field.piece == Piece.Queen) {
            var toXUpLeft = x - 1
            var toYUpLeft = y - 1
            while (isValidPosition(toXUpLeft, toYUpLeft)
                && !tileHasPieceOfCurrentPlayer(toXUpLeft, toYUpLeft)
            ) {
                availableMoves.add(Point(toXUpLeft, toYUpLeft))
                if (tileHasPieceOfOpponent(toXUpLeft, toYUpLeft)) break
                toXUpLeft--
                toYUpLeft--
            }
            var toXUpRight = x - 1
            var toYUpRight = y + 1
            while (isValidPosition(toXUpRight, toYUpRight)
                && !tileHasPieceOfCurrentPlayer(toXUpRight, toYUpRight)
            ) {
                availableMoves.add(Point(toXUpRight, toYUpRight))
                if (tileHasPieceOfOpponent(toXUpRight, toYUpRight)) break
                toXUpRight--
                toYUpRight++
            }
            var toXDownLeft = x + 1
            var toYDownLeft = y - 1
            while (isValidPosition(toXDownLeft, toYDownLeft)
                && !tileHasPieceOfCurrentPlayer(toXDownLeft, toYDownLeft)
            ) {
                availableMoves.add(Point(toXDownLeft, toYDownLeft))
                if (tileHasPieceOfOpponent(toXDownLeft, toYDownLeft)) break
                toXDownLeft++
                toYDownLeft--
            }
            var toXDownRight = x + 1
            var toYDownRight = y + 1
            while (isValidPosition(toXDownRight, toYDownRight)
                && !tileHasPieceOfCurrentPlayer(toXDownRight, toYDownRight)
            ) {
                availableMoves.add(Point(toXDownRight, toYDownRight))
                if (tileHasPieceOfOpponent(toXDownRight, toYDownRight)) break
                toXDownRight++
                toYDownRight++
            }
        }

        return availableMoves
    }

    fun movePiece(fromX: Int, fromY: Int, toX: Int, toY: Int) {
        if (getAvailableMoves(fromX, fromY).contains(Point(toX, toY))) {
            if (tileHasPieceOfOpponent(toX, toY)) {
                removedPiecesList.add(playingField[toX][toY])
            }
            playingFieldArray[toX][toY] = playingFieldArray[fromX][fromY]
            playingFieldArray[fromX][fromY] = PlayerPiece()
            (playingFieldArray[toX][toY].piece as? Piece.Pawn)?.hasStartingPosition = false
        } else {
            throw IllegalArgumentException("Invalid move coordinates")
        }
        currentPlayer = if (currentPlayer == Player.White) Player.Black else Player.White
    }

    fun tileHasPieceOfCurrentPlayer(x: Int, y: Int) = when (currentPlayer) {
        Player.Black -> {
            playingField[x][y].player == Player.Black
        }
        Player.White -> {
            playingField[x][y].player == Player.White
        }
    }

    private fun tileHasPieceOfOpponent(x: Int, y: Int) = when (currentPlayer) {
        Player.Black -> {
            playingField[x][y].player == Player.White
        }
        Player.White -> {
            playingField[x][y].player == Player.Black
        }
    }

    fun isGameOver() = removedPieces.any { it.piece == Piece.King }
}
