package earth.terrarium.tempad.client.widgets.map

import net.minecraft.Optionull
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BiomeColors
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Direction
import net.minecraft.core.SectionPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.material.MapColor
import kotlin.math.max
import kotlin.math.min

object ClaimMapTopologyAlgorithm {
    const val BRIGHTER_COLOR: Int = -0xfcfcfd

    fun setColors(minX: Int, minZ: Int, maxX: Int, maxZ: Int, level: Level, player: Player): Array<IntArray> {
        val colors = Array(maxX - minX) { IntArray(maxZ - minZ) }
        val pos1 = MutableBlockPos()
        val pos2 = MutableBlockPos()

        for (x in minX until maxX) {
            pos1.setX(x)
            var depth = 0.0
            for (z in minZ - 1 until maxZ) {
                var fluidDepth = 0.0
                pos1.setZ(z)
                val chunk = level.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z))
                if (!chunk.isEmpty) {
                    var color = MapColor.NONE
                    var yDiff = 0.0
                    var y = if (level.dimensionType().hasCeiling()) findBlockWithAirAbove(
                        level,
                        BlockPos(x, player.blockY, z)
                    ) else chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1
                    var state: BlockState? = null
                    if (y > level.minBuildHeight + 1) {
                        do {
                            y--
                            pos1.setY(y)
                            state = chunk.getBlockState(pos1)
                        } while (!shouldRender(state, level, pos1) && y > level.minBuildHeight)

                        if (y > level.minBuildHeight && !state!!.fluidState.isEmpty) {
                            var y2 = y - 1
                            pos2.set(pos1)

                            var blockState2: BlockState
                            do {
                                pos2.setY(y2--)
                                blockState2 = chunk.getBlockState(pos2)
                                ++fluidDepth
                            } while (y2 > level.minBuildHeight && !blockState2.fluidState.isEmpty)

                            state = getCorrectStateForFluidBlock(level, state, pos1)
                        }

                        yDiff += y.toDouble()
                        color = Optionull.mapOrDefault(
                            state,
                            { b: BlockState ->
                                b.getMapColor(
                                    level,
                                    pos1
                                )
                            }, MapColor.NONE
                        )
                    }

                    fluidDepth /= 2.5

                    var brightness: MapColor.Brightness
                    if (color === MapColor.WATER) {
                        val darkness = fluidDepth * 0.1 + (x + z and 1).toDouble() * 0.2
                        brightness = if (darkness < 0.5) {
                            MapColor.Brightness.LOW
                        } else if (darkness > 0.9) {
                            MapColor.Brightness.HIGH
                        } else {
                            MapColor.Brightness.NORMAL
                        }
                    } else {
                        val darkness = (yDiff - depth) * 4.0 / 5.0 + ((x + z and 1).toDouble() - 0.5) * 0.4
                        brightness = if (darkness > 0.6) {
                            MapColor.Brightness.HIGH
                        } else if (darkness < -0.6) {
                            MapColor.Brightness.LOW
                        } else {
                            MapColor.Brightness.NORMAL
                        }
                    }

                    depth = yDiff
                    if (z != minZ - 1) {
                        colors[x - minX][z - minZ] = getTintShade(color, state, level, pos1, brightness)
                    }
                }
            }
        }
        return colors
    }

    private fun getCorrectStateForFluidBlock(level: Level, blockState: BlockState?, blockPos: BlockPos): BlockState {
        val fluidState = blockState!!.fluidState
        return if (!fluidState.isEmpty && !blockState.isFaceSturdy(
                level,
                blockPos,
                Direction.UP
            )
        ) fluidState.createLegacyBlock() else blockState
    }

    fun shouldRender(state: BlockState?, level: Level?, pos: BlockPos?): Boolean {
        if (state!!.getMapColor(level, pos) === MapColor.NONE) return false
        if (state!!.fluidState.isEmpty) return state.`is`(Blocks.SNOW) || !state.canBeReplaced()
        return true
    }

    fun getTintShade(
        color: MapColor,
        state: BlockState?,
        level: Level,
        pos: BlockPos,
        brightness: MapColor.Brightness
    ): Int {
        if (color === MapColor.WATER || color === MapColor.GRASS || color === MapColor.PLANT) {
            var tintColor = BiomeColors.getAverageGrassColor(level, pos)
            if (color === MapColor.WATER) tintColor = BiomeColors.getAverageWaterColor(level, pos)
            if (color === MapColor.PLANT) tintColor = BiomeColors.getAverageFoliageColor(level, pos)
            var intColor = rgb2abgr(tintColor)
            if (color === MapColor.WATER) {
                intColor = brighter(intColor)
            }
            return when (brightness) {
                MapColor.Brightness.LOWEST -> intColor
                MapColor.Brightness.LOW -> darker(intColor)
                MapColor.Brightness.NORMAL -> darker(darker(intColor))
                MapColor.Brightness.HIGH -> darker(darker(darker(intColor)))
            }
        } else if (state != null) {
            val tintColor = Minecraft.getInstance().blockColors.getColor(state, level, pos, 0)
            if (tintColor == -1) {
                return ClaimMapPalette.getColor(color.id, brightness)
            }
            val intColor = rgb2abgr(Minecraft.getInstance().blockColors.getColor(state, level, pos))
            return when (brightness) {
                MapColor.Brightness.LOWEST -> intColor
                MapColor.Brightness.LOW -> darker(intColor)
                MapColor.Brightness.NORMAL -> darker(darker(intColor))
                MapColor.Brightness.HIGH -> darker(darker(darker(intColor)))
            }
        }
        return 0x00000000
    }

    fun rgb2abgr(rgb: Int): Int {
        return (rgb and -0xff0100) or ((rgb and 0xFF) shl 16) or ((rgb shr 16) and 0xFF) or -0x1000000
    }

    private fun darker(abgr: Int): Int {
        val red = (abgr shr 16) and 0xFF
        val green = (abgr shr 8) and 0xFF
        val blue = abgr and 0xFF
        return (max((red * 0.7f).toInt(), 0) shl 16) or (max((green * 0.7f).toInt(), 0) shl 8) or max((blue * 0.7f).toInt(), 0) or -0x1000000
    }

    private fun brighter(abgr: Int): Int {
        var red = (abgr shr 16) and 0xFF
        var green = (abgr shr 8) and 0xFF
        var blue = abgr and 0xFF
        if (red == 0 && green == 0 && blue == 0) {
            return BRIGHTER_COLOR
        }
        if (red > 0 && red < 3) {
            red = 3
        }
        if (green > 0 && green < 3) {
            green = 3
        }
        if (blue > 0 && blue < 3) {
            blue = 3
        }
        return (min((red * 1.1f).toInt(), 255) shl 16) or (min(
            (green * 1.1f).toInt(),
            255
        ) shl 8) or min((blue * 1.1f).toInt(), 255) or -0x1000000
    }

    private fun findBlockWithAirAbove(level: Level, pos: BlockPos): Int {
        var offset = 0
        val y = pos.y
        val mutablePos = pos.mutable()

        while (!(!level.getBlockState(mutablePos).isAir && (level.getBlockState(mutablePos.above()).isAir || level.getBlockState(
                mutablePos.below()
            ).isAir))
        ) {
            offset = if (offset <= 0) {
                -offset + 1
            } else {
                -offset
            }
            mutablePos.setY(offset + y)
        }
        return mutablePos.y
    }
}