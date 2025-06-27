package content.global.skill.magic.spells.modern

import content.global.skill.magic.spells.SpellProjectile
import core.game.node.entity.combat.spell.Runes
import core.game.node.entity.combat.spell.SpellType
import core.game.node.entity.impl.Projectile
import core.game.node.item.Item
import core.game.world.update.flag.context.Graphics
import org.rs.consts.Sounds
import org.rs.consts.Graphics as Graphic

/**
 * Represents configuration of fire spells.
 */
enum class FireSpellDefinition(val type: SpellType, val buttonId: Int, val level: Int, val xp: Double, val sound: Int, val start: Graphics, val projectile: Projectile, val end: Graphics, val runes: Array<Item>) {
    STRIKE(type = SpellType.STRIKE, buttonId = 8, level = 13, xp = 11.5, sound = Sounds.FIRESTRIKE_CAST_AND_FIRE_160, start = Graphics(Graphic.FIRE_STRIKE_CAST_99, 96), projectile = SpellProjectile.create(Graphic.FIRE_STRIKE_PROJECTILE_100), end = Graphics(Graphic.FIRE_STRIKE_IMPACT_101, 96), runes = arrayOf(Runes.MIND_RUNE.getItem(1), Runes.FIRE_RUNE.getItem(3), Runes.AIR_RUNE.getItem(2))),
    BOLT(type = SpellType.BOLT, buttonId = 20, level = 35, xp = 22.5, sound = Sounds.FIREBOLT_CAST_AND_FIRE_157, start = Graphics(Graphic.FIRE_BOLT_CAST_126, 96), projectile = SpellProjectile.create(Graphic.FIRE_BOLT_PROJECTILE_127), end = Graphics(Graphic.FIRE_BOLT_IMPACT_128, 96), runes = arrayOf(Runes.CHAOS_RUNE.getItem(1), Runes.FIRE_RUNE.getItem(4), Runes.AIR_RUNE.getItem(3))),
    BLAST(type = SpellType.BLAST, buttonId = 38, level = 59, xp = 34.5, sound = Sounds.FIREBLAST_CAST_AND_FIRE_155, start = Graphics(Graphic.FIRE_BLAST_CAST_129, 96), projectile = SpellProjectile.create(Graphic.FIRE_BLAST_PROJECTILE_130), end = Graphics(Graphic.FIRE_BLAST_IMPACT_131, 96), runes = arrayOf(Runes.DEATH_RUNE.getItem(1), Runes.FIRE_RUNE.getItem(5), Runes.AIR_RUNE.getItem(4))),
    WAVE(type = SpellType.WAVE, buttonId = 55, level = 75, xp = 42.5, sound = Sounds.FIREWAVE_CAST_AND_FIRE_162, start = Graphics(Graphic.FIRE_WAVE_CAST_155, 96), projectile = SpellProjectile.create(Graphic.FIRE_WAVE_PROJECTILE_156), end = Graphics(Graphic.FIRE_WAVE_IMPACT_157, 96), runes = arrayOf(Runes.BLOOD_RUNE.getItem(1), Runes.FIRE_RUNE.getItem(7), Runes.AIR_RUNE.getItem(5)));
}