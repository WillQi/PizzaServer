package io.github.willqi.pizzaserver.server.entity.meta.properties;

public enum EntityMetaPropertyName {
    HEALTH(EntityMetaPropertyType.INTEGER),
    VARIANT(EntityMetaPropertyType.INTEGER),
    COLOR(EntityMetaPropertyType.BYTE),
    NAMETAG(EntityMetaPropertyType.STRING),
    OWNER_EID(EntityMetaPropertyType.LONG),
    TARGET_EID(EntityMetaPropertyType.LONG),
    AIR(EntityMetaPropertyType.SHORT),
    POTION_COLOR(EntityMetaPropertyType.INTEGER),
    POTION_AMBIENT(EntityMetaPropertyType.BYTE),
    JUMP_DURATION(EntityMetaPropertyType.LONG),
    HURT_TIME(EntityMetaPropertyType.INTEGER),
    HURT_DIRECTION(EntityMetaPropertyType.INTEGER),
    PADDLE_TIME_LEFT(EntityMetaPropertyType.FLOAT),
    PADDLE_TIME_RIGHT(EntityMetaPropertyType.FLOAT),
    EXPERIENCE_VALUE(EntityMetaPropertyType.INTEGER),
    MINECART_DISPLAY_BLOCK(EntityMetaPropertyType.INTEGER),
    MINECART_DISPLAY_OFFSET(EntityMetaPropertyType.INTEGER),
    HORSE_FLAGS(EntityMetaPropertyType.INTEGER),
    SHOOTER_ID(EntityMetaPropertyType.LONG),
    MINECART_HAS_DISPLAY(EntityMetaPropertyType.BYTE),
    HORSE_TYPE(EntityMetaPropertyType.BYTE),
    CHARGE_AMOUNT(EntityMetaPropertyType.INTEGER),
    ENDERMAN_HELD_ITEM_ID(EntityMetaPropertyType.SHORT),
    ENTITY_AGE(EntityMetaPropertyType.SHORT),
    PLAYER_INDEX(EntityMetaPropertyType.INTEGER),
    PLAYER_BED_POSITION(EntityMetaPropertyType.VECTOR3I),
    FIREBALL_POWER_X(EntityMetaPropertyType.FLOAT),
    FIREBALL_POWER_Y(EntityMetaPropertyType.FLOAT),
    FIREBALL_POWER_Z(EntityMetaPropertyType.FLOAT),
    POTION_AUX_VALUE(EntityMetaPropertyType.SHORT),
    LEAD_HOLDER_EID(EntityMetaPropertyType.LONG),
    SCALE(EntityMetaPropertyType.FLOAT),
    HAS_NPC_COMPONENT(EntityMetaPropertyType.BYTE),
    NPC_SKIN_INDEX(EntityMetaPropertyType.STRING),
    NPC_ACTIONS(EntityMetaPropertyType.STRING),
    MAX_AIR_SUPPLY(EntityMetaPropertyType.SHORT),
    MARK_VARIANT(EntityMetaPropertyType.INTEGER),
    CONTAINER_TYPE(EntityMetaPropertyType.BYTE),
    CONTAINER_BASE_SIZE(EntityMetaPropertyType.INTEGER),
    CONTAINER_EXTRA_SLOTS_PER_STRENGTH(EntityMetaPropertyType.INTEGER),
    BLOCK_TARGET(EntityMetaPropertyType.VECTOR3I),
    WITHER_INVULNERABLE_TICKS(EntityMetaPropertyType.INTEGER),
    WITHER_TARGET_1(EntityMetaPropertyType.LONG),
    WITHER_TARGET_2(EntityMetaPropertyType.LONG),
    WITHER_TARGET_3(EntityMetaPropertyType.LONG),
    WITHER_AERIAL_ATTACK(EntityMetaPropertyType.SHORT),
    BOUNDING_BOX_WIDTH(EntityMetaPropertyType.FLOAT),
    BOUNDING_BOX_HEIGHT(EntityMetaPropertyType.FLOAT),
    FUSE_LENGTH(EntityMetaPropertyType.INTEGER),
    RIDER_SEAT_POSITION(EntityMetaPropertyType.VECTOR3),
    RIDER_ROTATION_LOCKED(EntityMetaPropertyType.BYTE),
    RIDER_MAX_ROTATION(EntityMetaPropertyType.FLOAT),
    RIDER_MIN_ROTATION(EntityMetaPropertyType.FLOAT),
    AREA_EFFECT_CLOUD_RADIUS(EntityMetaPropertyType.FLOAT),
    AREA_EFFECT_CLOUD_WAITING(EntityMetaPropertyType.INTEGER),
    AREA_EFFECT_CLOUD_PARTICLE_ID(EntityMetaPropertyType.INTEGER),
    AREA_EFFECT_CLOUD_DURATION(EntityMetaPropertyType.INTEGER),
    AREA_EFFECT_CLOUD_SPAWN_TIME(EntityMetaPropertyType.INTEGER),
    AREA_EFFECT_CLOUD_CHANGE_RATE(EntityMetaPropertyType.FLOAT),
    AREA_EFFECT_CLOUD_CHANGE_ON_PICKUP(EntityMetaPropertyType.FLOAT),
    SHULKER_PEAK_HEIGHT(EntityMetaPropertyType.INTEGER),
    SHULKER_ATTACH_FACE(EntityMetaPropertyType.BYTE),
    SHULKER_ATTACH_POSITION(EntityMetaPropertyType.VECTOR3I),
    TRADING_TARGET_EID(EntityMetaPropertyType.LONG),
    COMMAND_BLOCK_ENABLED(EntityMetaPropertyType.BYTE),
    COMMAND_BLOCK_COMMAND(EntityMetaPropertyType.STRING),
    COMMAND_BLOCK_LAST_OUTPUT(EntityMetaPropertyType.STRING),
    COMMAND_BLOCK_TRACK_OUTPUT(EntityMetaPropertyType.BYTE),
    COMMAND_BLOCK_TICK_DELAY(EntityMetaPropertyType.INTEGER),
    COMMAND_BLOCK_EXECUTE_ON_FIRST_TICK(EntityMetaPropertyType.BYTE),
    CONTROLLING_RIDER_SEAT_NUMBER(EntityMetaPropertyType.BYTE),
    STRENGTH(EntityMetaPropertyType.INTEGER),
    MAX_STRENGTH(EntityMetaPropertyType.INTEGER),
    EVOKER_SPELL_COLOR(EntityMetaPropertyType.INTEGER),
    LIMITED_LIFE(EntityMetaPropertyType.INTEGER),
    ARMOR_STAND_POSE_INDEX(EntityMetaPropertyType.INTEGER),
    ENDER_CRYSTAL_TIME_OFFSET(EntityMetaPropertyType.INTEGER),
    ALWAYS_SHOW_NAMETAG(EntityMetaPropertyType.BYTE),
    COLOR_2(EntityMetaPropertyType.BYTE),
    SCORE_TAG(EntityMetaPropertyType.STRING),
    BALLOON_ATTACHED_ENTITY(EntityMetaPropertyType.LONG),
    PUFFERFISH_SIZE(EntityMetaPropertyType.BYTE),
    BOAT_BUBBLE_TIME(EntityMetaPropertyType.INTEGER),
    PLAYER_AGENT_EID(EntityMetaPropertyType.LONG),
    EATING_COUNTER(EntityMetaPropertyType.INTEGER),
    INTERACTIVE_TAG(EntityMetaPropertyType.STRING),
    TRADE_TIER(EntityMetaPropertyType.INTEGER),
    MAX_TRADE_TIER(EntityMetaPropertyType.INTEGER),
    TRADE_XP(EntityMetaPropertyType.INTEGER),
    SKIN_ID(EntityMetaPropertyType.INTEGER),
    AMBIENT_SOUND_INTERVAL(EntityMetaPropertyType.FLOAT),
    AMBIENT_SOUND_INTERVAL_RANGE(EntityMetaPropertyType.FLOAT),
    AMBIENT_SOUND_EVENT_NAME(EntityMetaPropertyType.STRING),
    FALL_DAMAGE_MULTIPLIER(EntityMetaPropertyType.FLOAT),
    CAN_RIDE_TARGET(EntityMetaPropertyType.BYTE),
    LOW_TIER_CURED_TRADE_DISCOUNT(EntityMetaPropertyType.INTEGER),
    HIGH_TIER_CURED_TRADE_DISCOUNT(EntityMetaPropertyType.INTEGER),
    NEARBY_CURED_TRADE_DISCOUNT(EntityMetaPropertyType.INTEGER),
    DISCOUNT_TIME_STAMP(EntityMetaPropertyType.INTEGER),
    HITBOX(EntityMetaPropertyType.NBT),
    IS_BUOYANT(EntityMetaPropertyType.BYTE),
    BUOYANCY_DATA(EntityMetaPropertyType.STRING);


    private final EntityMetaPropertyType type;


    EntityMetaPropertyName(EntityMetaPropertyType type) {
        this.type = type;
    }

    public EntityMetaPropertyType getType() {
        return this.type;
    }

}
