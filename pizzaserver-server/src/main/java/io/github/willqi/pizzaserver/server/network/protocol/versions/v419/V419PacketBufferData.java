package io.github.willqi.pizzaserver.server.network.protocol.versions.v419;

import io.github.willqi.pizzaserver.api.entity.meta.flags.EntityMetaFlag;
import io.github.willqi.pizzaserver.api.entity.meta.flags.EntityMetaFlagCategory;
import io.github.willqi.pizzaserver.api.entity.meta.properties.EntityMetaPropertyName;
import io.github.willqi.pizzaserver.server.network.protocol.data.Experiment;
import io.github.willqi.pizzaserver.server.network.protocol.versions.BasePacketBufferData;

public class V419PacketBufferData extends BasePacketBufferData {

    public static final BasePacketBufferData INSTANCE = new V419PacketBufferData();


    protected V419PacketBufferData() {
        this.registerExperiment(Experiment.DATA_DRIVEN_ITEMS);

        this.registerEntityFlagCategory(EntityMetaFlagCategory.DATA_FLAG, 0)
            .registerEntityFlagCategory(EntityMetaFlagCategory.PLAYER_FLAG, 26);

        this.registerEntityFlag(EntityMetaFlag.IS_ON_FIRE, 0)
            .registerEntityFlag(EntityMetaFlag.IS_RIDING, 2)
            .registerEntityFlag(EntityMetaFlag.IS_SPRINTING, 3)
            .registerEntityFlag(EntityMetaFlag.IS_SNEAKING, 1)
            .registerEntityFlag(EntityMetaFlag.IS_USING_ITEM, 4)
            .registerEntityFlag(EntityMetaFlag.IS_INVISIBLE, 5)
            .registerEntityFlag(EntityMetaFlag.IS_TEMPTED, 6)
            .registerEntityFlag(EntityMetaFlag.IS_IN_LOVE, 7)
            .registerEntityFlag(EntityMetaFlag.IS_SADDLED, 8)
            .registerEntityFlag(EntityMetaFlag.IS_POWERED, 9)
            .registerEntityFlag(EntityMetaFlag.IS_IGNITED, 10)
            .registerEntityFlag(EntityMetaFlag.IS_BABY, 11)
            .registerEntityFlag(EntityMetaFlag.IS_CONVERTING, 12)
            .registerEntityFlag(EntityMetaFlag.CRITICAL, 13)
            .registerEntityFlag(EntityMetaFlag.CAN_SHOW_NAMETAG, 14)
            .registerEntityFlag(EntityMetaFlag.ALWAYS_SHOW_NAMETAG, 15)
            .registerEntityFlag(EntityMetaFlag.HAS_NO_AI, 16)
            .registerEntityFlag(EntityMetaFlag.IS_SILENT, 17)
            .registerEntityFlag(EntityMetaFlag.IS_WALL_CLIMBING, 18)
            .registerEntityFlag(EntityMetaFlag.CAN_WALL_CLIMB, 19)
            .registerEntityFlag(EntityMetaFlag.CAN_SWIM, 20)
            .registerEntityFlag(EntityMetaFlag.CAN_FLY, 21)
            .registerEntityFlag(EntityMetaFlag.CAN_WALK, 22)
            .registerEntityFlag(EntityMetaFlag.IS_RESTING, 23)
            .registerEntityFlag(EntityMetaFlag.IS_SITTING, 24)
            .registerEntityFlag(EntityMetaFlag.IS_ANGRY, 25)
            .registerEntityFlag(EntityMetaFlag.IS_INTERESTED, 26)
            .registerEntityFlag(EntityMetaFlag.IS_CHARGED, 27)
            .registerEntityFlag(EntityMetaFlag.IS_TAMED, 28)
            .registerEntityFlag(EntityMetaFlag.IS_ORPHANED, 29)
            .registerEntityFlag(EntityMetaFlag.IS_LEASHED, 30)
            .registerEntityFlag(EntityMetaFlag.IS_SHEARED, 31)
            .registerEntityFlag(EntityMetaFlag.IS_GLIDING, 32)
            .registerEntityFlag(EntityMetaFlag.ELDER, 33)
            .registerEntityFlag(EntityMetaFlag.IS_MOVING, 34)
            .registerEntityFlag(EntityMetaFlag.IS_BREATHING, 35)
            .registerEntityFlag(EntityMetaFlag.CHESTED, 36)
            .registerEntityFlag(EntityMetaFlag.STACKABLE, 37)
            .registerEntityFlag(EntityMetaFlag.SHOW_BASE, 38)
            .registerEntityFlag(EntityMetaFlag.IS_STANDING, 39)
            .registerEntityFlag(EntityMetaFlag.IS_SHAKING, 40)
            .registerEntityFlag(EntityMetaFlag.IS_IDLING, 41)
            .registerEntityFlag(EntityMetaFlag.IS_CASTING, 42)
            .registerEntityFlag(EntityMetaFlag.IS_CHARGING, 43)
            .registerEntityFlag(EntityMetaFlag.IS_WASD_CONTROLLED, 44)
            .registerEntityFlag(EntityMetaFlag.CAN_POWER_JUMP, 45)
            .registerEntityFlag(EntityMetaFlag.LINGER, 46)
            .registerEntityFlag(EntityMetaFlag.HAS_COLLISION, 47)
            .registerEntityFlag(EntityMetaFlag.HAS_GRAVITY, 48)
            .registerEntityFlag(EntityMetaFlag.IS_FIRE_IMMUNE, 49)
            .registerEntityFlag(EntityMetaFlag.IS_DANCING, 50)
            .registerEntityFlag(EntityMetaFlag.ENCHANTED, 51)
            .registerEntityFlag(EntityMetaFlag.SHOWING_TRIDENT_ROPE, 52)
            .registerEntityFlag(EntityMetaFlag.HAS_PRIVATE_CONTAINER, 53)
            .registerEntityFlag(EntityMetaFlag.IS_TRANSFORMING, 54)
            .registerEntityFlag(EntityMetaFlag.SPIN_ATTACK, 55)
            .registerEntityFlag(EntityMetaFlag.IS_SWIMMING, 56)
            .registerEntityFlag(EntityMetaFlag.IS_BRIBED, 57)
            .registerEntityFlag(EntityMetaFlag.IS_PREGNANT, 58)
            .registerEntityFlag(EntityMetaFlag.IS_LAYING_EGG, 59)
            .registerEntityFlag(EntityMetaFlag.RIDER_CAN_PICK, 60)
            .registerEntityFlag(EntityMetaFlag.TRANSITION_SITTING, 61)
            .registerEntityFlag(EntityMetaFlag.IS_EATING, 62)
            .registerEntityFlag(EntityMetaFlag.IS_LAYING_DOWN, 63)
            .registerEntityFlag(EntityMetaFlag.IS_SNEEZING, 64)
            .registerEntityFlag(EntityMetaFlag.TRUSTING, 65)
            .registerEntityFlag(EntityMetaFlag.IS_ROLLING, 66)
            .registerEntityFlag(EntityMetaFlag.IS_SCARED, 67)
            .registerEntityFlag(EntityMetaFlag.IN_SCAFFOLDING, 68)
            .registerEntityFlag(EntityMetaFlag.OVER_SCAFFOLDING, 69)
            .registerEntityFlag(EntityMetaFlag.FALLING_THROUGH_SCAFFOLDING, 70)
            .registerEntityFlag(EntityMetaFlag.IS_BLOCKING, 71)
            .registerEntityFlag(EntityMetaFlag.TRANSITION_BLOCKING, 72)
            .registerEntityFlag(EntityMetaFlag.BLOCKED_USING_SHIELD, 73)
            .registerEntityFlag(EntityMetaFlag.BLOCKED_USING_DAMAGED_SHIELD, 74)
            .registerEntityFlag(EntityMetaFlag.IS_SLEEPING, 75)
            .registerEntityFlag(EntityMetaFlag.WANTS_TO_AWAKE, 76)
            .registerEntityFlag(EntityMetaFlag.HAS_TRADE_INTEREST, 77)
            .registerEntityFlag(EntityMetaFlag.IS_DOOR_BREAKER, 78)
            .registerEntityFlag(EntityMetaFlag.IS_BREAKING_OBSTRUCTION, 79)
            .registerEntityFlag(EntityMetaFlag.IS_DOOR_OPENER, 80)
            .registerEntityFlag(EntityMetaFlag.IS_ILLAGER_CAPTAIN, 81)
            .registerEntityFlag(EntityMetaFlag.IS_STUNNED, 82)
            .registerEntityFlag(EntityMetaFlag.IS_ROARING, 83)
            .registerEntityFlag(EntityMetaFlag.HAS_DELAYED_ATTACK, 84)
            .registerEntityFlag(EntityMetaFlag.IS_AVOIDING_MOBS, 85)
            .registerEntityFlag(EntityMetaFlag.IS_AVOIDING_BLOCK, 86)
            .registerEntityFlag(EntityMetaFlag.IS_FACING_TARGET_TO_RANGE_ATTACK, 87)
            .registerEntityFlag(EntityMetaFlag.IS_HIDDEN_WHEN_INVISIBLE, 88)
            .registerEntityFlag(EntityMetaFlag.IS_IN_UI, 89)
            .registerEntityFlag(EntityMetaFlag.IS_STALKING, 90)
            .registerEntityFlag(EntityMetaFlag.IS_EMOTING, 91)
            .registerEntityFlag(EntityMetaFlag.IS_CELEBRATING, 92)
            .registerEntityFlag(EntityMetaFlag.IS_ADMIRING, 93)
            .registerEntityFlag(EntityMetaFlag.IS_CELEBRATING_SPECIAL, 94);


        // Commented lines represent properties with unknown types
        this.registerEntityProperty(EntityMetaPropertyName.VARIANT, 2)
            .registerEntityProperty(EntityMetaPropertyName.HEALTH, 1)
            .registerEntityProperty(EntityMetaPropertyName.NAMETAG, 4)
            .registerEntityProperty(EntityMetaPropertyName.COLOR, 3)
            .registerEntityProperty(EntityMetaPropertyName.OWNER_EID, 5)
            .registerEntityProperty(EntityMetaPropertyName.TARGET_EID, 6)
            .registerEntityProperty(EntityMetaPropertyName.AIR, 7)
            .registerEntityProperty(EntityMetaPropertyName.POTION_COLOR, 8)
            .registerEntityProperty(EntityMetaPropertyName.POTION_AMBIENT, 9)
            .registerEntityProperty(EntityMetaPropertyName.JUMP_DURATION, 10)
            .registerEntityProperty(EntityMetaPropertyName.HURT_TIME, 11)
            .registerEntityProperty(EntityMetaPropertyName.HURT_DIRECTION, 12)
            .registerEntityProperty(EntityMetaPropertyName.PADDLE_TIME_LEFT, 13)
            .registerEntityProperty(EntityMetaPropertyName.PADDLE_TIME_RIGHT, 14)
            .registerEntityProperty(EntityMetaPropertyName.EXPERIENCE_VALUE, 15)
            .registerEntityProperty(EntityMetaPropertyName.MINECART_DISPLAY_BLOCK, 16)
            .registerEntityProperty(EntityMetaPropertyName.MINECART_DISPLAY_OFFSET, 17)
            .registerEntityProperty(EntityMetaPropertyName.MINECART_HAS_DISPLAY, 18)
            // swell
            // old swell
            // swell dir
            .registerEntityProperty(EntityMetaPropertyName.CHARGE_AMOUNT, 22)
            .registerEntityProperty(EntityMetaPropertyName.ENDERMAN_HELD_ITEM_ID, 23)
            .registerEntityProperty(EntityMetaPropertyName.ENTITY_AGE, 24)
            // ???
            // player flags (used for flags)
            .registerEntityProperty(EntityMetaPropertyName.PLAYER_INDEX, 27)
            .registerEntityProperty(EntityMetaPropertyName.PLAYER_BED_POSITION, 28)
            .registerEntityProperty(EntityMetaPropertyName.FIREBALL_POWER_X, 29)
            .registerEntityProperty(EntityMetaPropertyName.FIREBALL_POWER_Y, 30)
            .registerEntityProperty(EntityMetaPropertyName.FIREBALL_POWER_Z, 31)
            // aux power
            // fish x
            // fish z
            // fish angle
            .registerEntityProperty(EntityMetaPropertyName.POTION_AUX_VALUE, 36)
            .registerEntityProperty(EntityMetaPropertyName.LEAD_HOLDER_EID, 37)
            .registerEntityProperty(EntityMetaPropertyName.SCALE, 38)
            .registerEntityProperty(EntityMetaPropertyName.INTERACTIVE_TAG, 39)
            .registerEntityProperty(EntityMetaPropertyName.NPC_SKIN_INDEX, 40)
            // url tag
            .registerEntityProperty(EntityMetaPropertyName.MAX_AIR_SUPPLY, 42)
            .registerEntityProperty(EntityMetaPropertyName.MARK_VARIANT, 43)
            .registerEntityProperty(EntityMetaPropertyName.CONTAINER_TYPE, 44)
            .registerEntityProperty(EntityMetaPropertyName.CONTAINER_BASE_SIZE, 45)
            .registerEntityProperty(EntityMetaPropertyName.CONTAINER_EXTRA_SLOTS_PER_STRENGTH, 46)
            .registerEntityProperty(EntityMetaPropertyName.BLOCK_TARGET, 47)
            .registerEntityProperty(EntityMetaPropertyName.WITHER_INVULNERABLE_TICKS, 48)
            .registerEntityProperty(EntityMetaPropertyName.WITHER_TARGET_1, 49)
            .registerEntityProperty(EntityMetaPropertyName.WITHER_TARGET_2, 50)
            .registerEntityProperty(EntityMetaPropertyName.WITHER_TARGET_3, 51)
            .registerEntityProperty(EntityMetaPropertyName.WITHER_AERIAL_ATTACK, 52)
            .registerEntityProperty(EntityMetaPropertyName.BOUNDING_BOX_WIDTH, 53)
            .registerEntityProperty(EntityMetaPropertyName.BOUNDING_BOX_HEIGHT, 54)
            .registerEntityProperty(EntityMetaPropertyName.FUSE_LENGTH, 55)
            .registerEntityProperty(EntityMetaPropertyName.RIDER_SEAT_POSITION, 56)
            .registerEntityProperty(EntityMetaPropertyName.RIDER_ROTATION_LOCKED, 57)
            .registerEntityProperty(EntityMetaPropertyName.RIDER_MAX_ROTATION, 58)
            .registerEntityProperty(EntityMetaPropertyName.RIDER_MIN_ROTATION, 59)
            .registerEntityProperty(EntityMetaPropertyName.AREA_EFFECT_CLOUD_RADIUS, 60)
            .registerEntityProperty(EntityMetaPropertyName.AREA_EFFECT_CLOUD_WAITING, 61)
            .registerEntityProperty(EntityMetaPropertyName.AREA_EFFECT_CLOUD_PARTICLE_ID, 62)
            .registerEntityProperty(EntityMetaPropertyName.SHULKER_ATTACH_FACE, 64)
            .registerEntityProperty(EntityMetaPropertyName.SHULKER_ATTACH_POSITION, 66)
            .registerEntityProperty(EntityMetaPropertyName.TRADING_TARGET_EID, 67)
            // trading career
            .registerEntityProperty(EntityMetaPropertyName.COMMAND_BLOCK_ENABLED, 69)
            .registerEntityProperty(EntityMetaPropertyName.COMMAND_BLOCK_COMMAND, 70)
            .registerEntityProperty(EntityMetaPropertyName.COMMAND_BLOCK_LAST_OUTPUT, 71)
            .registerEntityProperty(EntityMetaPropertyName.COMMAND_BLOCK_TRACK_OUTPUT, 72)
            .registerEntityProperty(EntityMetaPropertyName.CONTROLLING_RIDER_SEAT_NUMBER, 73)
            .registerEntityProperty(EntityMetaPropertyName.STRENGTH, 74)
            .registerEntityProperty(EntityMetaPropertyName.MAX_STRENGTH, 75)
            .registerEntityProperty(EntityMetaPropertyName.EVOKER_SPELL_COLOR, 76)
            .registerEntityProperty(EntityMetaPropertyName.LIMITED_LIFE, 77)
            .registerEntityProperty(EntityMetaPropertyName.ARMOR_STAND_POSE_INDEX, 78)
            .registerEntityProperty(EntityMetaPropertyName.ENDER_CRYSTAL_TIME_OFFSET, 79)
            .registerEntityProperty(EntityMetaPropertyName.ALWAYS_SHOW_NAMETAG, 80)
            .registerEntityProperty(EntityMetaPropertyName.COLOR_2, 81)
            // name author
            .registerEntityProperty(EntityMetaPropertyName.SCORE_TAG, 83)
            .registerEntityProperty(EntityMetaPropertyName.BALLOON_ATTACHED_ENTITY, 84)
            .registerEntityProperty(EntityMetaPropertyName.PUFFERFISH_SIZE, 85)
            .registerEntityProperty(EntityMetaPropertyName.BOAT_BUBBLE_TIME, 86)
            .registerEntityProperty(EntityMetaPropertyName.PLAYER_AGENT_EID, 87)
            // sitting amount
            // sitting amount previous
            .registerEntityProperty(EntityMetaPropertyName.EATING_COUNTER, 90)
            // flags extended (probably used for other flags?)
            // laying amount
            // laying amount previous
            .registerEntityProperty(EntityMetaPropertyName.AREA_EFFECT_CLOUD_DURATION, 94)
            .registerEntityProperty(EntityMetaPropertyName.AREA_EFFECT_CLOUD_SPAWN_TIME, 95)
            .registerEntityProperty(EntityMetaPropertyName.AREA_EFFECT_CLOUD_CHANGE_RATE, 96)
            .registerEntityProperty(EntityMetaPropertyName.AREA_EFFECT_CLOUD_CHANGE_ON_PICKUP, 97)
            // pickup count
            // interact text
            .registerEntityProperty(EntityMetaPropertyName.TRADE_TIER, 100)
            .registerEntityProperty(EntityMetaPropertyName.MAX_TRADE_TIER, 101)
            .registerEntityProperty(EntityMetaPropertyName.TRADE_XP, 102)
            .registerEntityProperty(EntityMetaPropertyName.SKIN_ID, 103)
            // spawning frames
            .registerEntityProperty(EntityMetaPropertyName.COMMAND_BLOCK_TICK_DELAY, 105)
            .registerEntityProperty(EntityMetaPropertyName.COMMAND_BLOCK_EXECUTE_ON_FIRST_TICK, 106)
            .registerEntityProperty(EntityMetaPropertyName.AMBIENT_SOUND_INTERVAL, 107)
            .registerEntityProperty(EntityMetaPropertyName.AMBIENT_SOUND_INTERVAL_RANGE, 108)
            .registerEntityProperty(EntityMetaPropertyName.AMBIENT_SOUND_EVENT_NAME, 109)
            .registerEntityProperty(EntityMetaPropertyName.FALL_DAMAGE_MULTIPLIER, 110)
            // name raw text
            .registerEntityProperty(EntityMetaPropertyName.CAN_RIDE_TARGET, 112)
            .registerEntityProperty(EntityMetaPropertyName.LOW_TIER_CURED_TRADE_DISCOUNT, 113)
            .registerEntityProperty(EntityMetaPropertyName.HIGH_TIER_CURED_TRADE_DISCOUNT, 114)
            .registerEntityProperty(EntityMetaPropertyName.NEARBY_CURED_TRADE_DISCOUNT, 115)
            .registerEntityProperty(EntityMetaPropertyName.DISCOUNT_TIME_STAMP, 116)
            .registerEntityProperty(EntityMetaPropertyName.HITBOX, 117)
            .registerEntityProperty(EntityMetaPropertyName.IS_BUOYANT, 118)
            .registerEntityProperty(EntityMetaPropertyName.BUOYANCY_DATA, 119);
    }

}
